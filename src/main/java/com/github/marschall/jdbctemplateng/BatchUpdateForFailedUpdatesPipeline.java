package com.github.marschall.jdbctemplateng;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.github.marschall.jdbctemplateng.api.ParameterizedPreparedStatementSetter;
import com.github.marschall.jdbctemplateng.api.PreparedStatementCreator;

final class BatchUpdateForFailedUpdatesPipeline<T> {

  private final DataSource dataSource;
  private final PreparedStatementCreator creator;
  private final List<T> batchArguments;
  private final int batchSize;
  private final ParameterizedPreparedStatementSetter<T> setter;

  BatchUpdateForFailedUpdatesPipeline(DataSource dataSource, PreparedStatementCreator creator, List<T> batchArguments, int batchSize,
          ParameterizedPreparedStatementSetter<T> setter) {
    this.dataSource = dataSource;
    this.creator = creator;
    this.batchArguments = batchArguments;
    this.batchSize = batchSize;
    this.setter = setter;
  }

  List<FailedUpdate<T>> forFailedUpdates(int expectedUpdateCount) {
    try {
      return this.execute(expectedUpdateCount);
    } catch (SQLException e) {
      throw UncheckedSQLExceptionAdapter.INSTANCE.translate(null, e);
    }
  }

  private List<FailedUpdate<T>> execute(int expectedUpdateCount) throws SQLException {
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

        if ((indexInBatch == (this.batchSize - 1)) || (rowIndex == (elementCount - 1))) {
          int[] batchUpdateCount = preparedStatement.executeBatch();

          for (int i = 0; i < batchUpdateCount.length; i++) {
            int updateCount = batchUpdateCount[i];

            if (updateCount != expectedUpdateCount) {
              T updatedElement = this.batchArguments.get((batchIndex * this.batchSize) + i);
              failedUpdates.add(new FailedUpdate<>(updateCount, updatedElement));
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
