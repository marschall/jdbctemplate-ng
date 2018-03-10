package com.github.marschall.jdbctemplateng;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

import javax.sql.DataSource;

import com.github.marschall.jdbctemplateng.api.ParameterizedPreparedStatementSetter;
import com.github.marschall.jdbctemplateng.api.PreparedStatementCreator;

final class BatchForUpdateCountUpdatePipeline<T> {

  private final DataSource dataSource;
  private final PreparedStatementCreator creator;
  private final Collection<T> batchArguments;
  private final int batchSize;
  private final ParameterizedPreparedStatementSetter<T> setter;

  BatchForUpdateCountUpdatePipeline(DataSource dataSource, PreparedStatementCreator creator, Collection<T> batchArguments, int batchSize, ParameterizedPreparedStatementSetter<T> setter) {
    this.dataSource = dataSource;
    this.creator = creator;
    this.batchArguments = batchArguments;
    this.batchSize = batchSize;
    this.setter = setter;
  }

  int[][] executeForPerBatchUpdateCountTranslated() {
    try {
      return this.execute();
    } catch (SQLException e) {
      throw UncheckedSQLExceptionAdapter.INSTANCE.translate(null, e);
    }
  }

  private int[][] execute() throws SQLException {
    try (Connection connection = this.dataSource.getConnection();
         PreparedStatement preparedStatement = this.creator.createPreparedStatement(connection)) {

      int rowIndex = 0;
      int indexInBatch = 0;
      int batchIndex = 0;
      int elementCount = this.batchArguments.size();
      int batchCount = elementCount / batchSize;
      if (elementCount % batchSize != 0) {
        batchCount += 1;
      }
      int[][] totalBatchUpdateCount = new int[batchCount][];

      for (T element : this.batchArguments) {
        this.setter.setValues(preparedStatement, element);
        preparedStatement.addBatch();

        if (indexInBatch == this.batchSize - 1 || rowIndex == elementCount - 1) {
          int[] batchUpdateCount = preparedStatement.executeBatch();
          totalBatchUpdateCount[batchIndex] = batchUpdateCount;
          batchIndex += 1;
          indexInBatch = 0;
        } else {
          indexInBatch += 1;
        }
        rowIndex += 1;
      }

      return totalBatchUpdateCount;
    }
  }

}
