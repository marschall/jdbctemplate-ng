package com.github.marschall.jdbctemplateng;

import static com.github.marschall.jdbctemplateng.SqlExtractor.extractSql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import javax.sql.DataSource;

import com.github.marschall.jdbctemplateng.api.ParameterizedPreparedStatementSetter;
import com.github.marschall.jdbctemplateng.api.PreparedStatementCreator;
import com.github.marschall.jdbctemplateng.api.RowMapper;
import com.github.marschall.jdbctemplateng.api.SQLExceptionAdapter;

final class BatchUpdateForFailedUpdatesAndGeneratedKeysPipeline<T, K> {

  private final DataSource dataSource;
  private final SQLExceptionAdapter exceptionAdapter;
  private final PreparedStatementCreator creator;
  private final List<T> batchArguments;
  private final int batchSize;
  private final ParameterizedPreparedStatementSetter<T> setter;
  private final RowMapper<K> keyExtractor;
  private final BiConsumer<T, K> callback;

  BatchUpdateForFailedUpdatesAndGeneratedKeysPipeline(DataSource dataSource, SQLExceptionAdapter exceptionAdapter,
          PreparedStatementCreator creator, List<T> batchArguments, int batchSize,
          ParameterizedPreparedStatementSetter<T> setter, RowMapper<K> keyExtractor, BiConsumer<T, K> callback) {
    this.dataSource = dataSource;
    this.exceptionAdapter = exceptionAdapter;
    this.creator = creator;
    this.batchArguments = batchArguments;
    this.batchSize = batchSize;
    this.setter = setter;
    this.keyExtractor = keyExtractor;
    this.callback = callback;
  }

  List<FailedUpdate<T>> forFailedUpdatesAndGeneratedKeys() {
    try {
      return this.execute();
    } catch (SQLException e) {
      throw this.exceptionAdapter.translate(extractSql(this.creator), e);
    }
  }

  private List<FailedUpdate<T>> execute() throws SQLException {
    try (Connection connection = this.dataSource.getConnection();
         PreparedStatement preparedStatement = this.creator.createPreparedStatement(connection)) {

      int rowIndex = 0;
      int indexInBatch = 0;
      int batchIndex = 0;
      int elementCount = this.batchArguments.size();
      List<FailedUpdate<T>> failedUpdates = new ArrayList<>(1);

      for (T element : this.batchArguments) {
        this.setter.setValues(preparedStatement, element);
        preparedStatement.addBatch();

        if (indexInBatch == this.batchSize - 1 || rowIndex == elementCount - 1) {
          int[] batchUpdateCount = preparedStatement.executeBatch();

          try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
            for (int i = 0; i < batchUpdateCount.length; i++) {
              int updateCount = batchUpdateCount[i];
              T updatedElement = this.batchArguments.get(batchIndex * this.batchSize + i);

              if (updateCount == 1) {
                if (!generatedKeys.next()) {
                  throw new IllegalStateException("update performed but no generated key");
                }
                K key = this.keyExtractor.mapRow(generatedKeys);
                this.callback.accept(updatedElement, key);
              } else if (updateCount == Statement.SUCCESS_NO_INFO) {
                failedUpdates.add(new FailedUpdate<>(updateCount, updatedElement));
              } else {
                if (generatedKeys.getType() == ResultSet.TYPE_FORWARD_ONLY) {
                  for (int j = 0; j < updateCount; j++) {
                    generatedKeys.next();
                  }
                } else {
                  generatedKeys.relative(updateCount);
                }
              }
            }
          }
          batchIndex += 1;
          indexInBatch = 0;
        } else {
          indexInBatch += 1;
        }
        rowIndex += 1;
      }

      return failedUpdates;
    }
  }

}
