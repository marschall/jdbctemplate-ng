package com.github.marschall.jdbctemplateng;

import java.sql.Statement;
import java.util.List;
import java.util.function.BiConsumer;

import javax.sql.DataSource;

import com.github.marschall.jdbctemplateng.api.ParameterizedPreparedStatementSetter;
import com.github.marschall.jdbctemplateng.api.PreparedStatementCreator;
import com.github.marschall.jdbctemplateng.api.RowMapper;
import com.github.marschall.jdbctemplateng.api.SQLExceptionAdapter;

public final class BatchBoundStatementProcessor<T> {

  private final DataSource dataSource;
  private final SQLExceptionAdapter exceptionAdapter;
  private final PreparedStatementCreator creator;
  private final List<T> batchArgs;
  private final int batchSize;
  private final ParameterizedPreparedStatementSetter<T> setter;

  BatchBoundStatementProcessor(DataSource dataSource, SQLExceptionAdapter exceptionAdapter,
          PreparedStatementCreator creator, List<T> batchArgs, int batchSize, ParameterizedPreparedStatementSetter<T> setter) {
    this.dataSource = dataSource;
    this.exceptionAdapter = exceptionAdapter;
    this.creator = creator;
    this.batchArgs = batchArgs;
    this.batchSize = batchSize;
    this.setter = setter;
  }

  public int forTotalUpdateCount() {
    // TODO could be optimized
    int totalUpdateCount = 0;
    for (int[] batchUpdateCount : this.forPerBatchUpdateCount()) {
      for (int rowUpdateCount : batchUpdateCount) {
        if (rowUpdateCount == Statement.SUCCESS_NO_INFO) {
          return Statement.SUCCESS_NO_INFO;
        }
        try {
          totalUpdateCount = Math.addExact(totalUpdateCount, rowUpdateCount);
        } catch (ArithmeticException e) {
          return Statement.SUCCESS_NO_INFO;
        }
      }
    }
    return totalUpdateCount;
  }

  public int[][] forPerBatchUpdateCount() {
    BatchUpdateForUpdateCountPipeline<T> pipeline = new BatchUpdateForUpdateCountPipeline<>(
            this.dataSource, this.exceptionAdapter, this.creator, this.batchArgs, this.batchSize, this.setter);
    return pipeline.executeForPerBatchUpdateCountTranslated();
  }

  public <K> List<FailedUpdate<T>> forGeneratedKeysAndFailedUpdates(Class<K> keyType, BiConsumer<T, K> callback) {
    return this.forGeneratedKeysAndFailedUpdates(resultSet -> resultSet.getObject(1, keyType), callback);
  }

  public <K> List<FailedUpdate<T>> forGeneratedKeysAndFailedUpdates(RowMapper<K> keyExtractor, BiConsumer<T, K> callback) {
    BatchUpdateForFailedUpdatesAndGeneratedKeysPipeline<T, K> pipeline = new BatchUpdateForFailedUpdatesAndGeneratedKeysPipeline<>(
            this.dataSource, this.exceptionAdapter, this.creator, this.batchArgs, this.batchSize, this.setter, keyExtractor, callback);
    return pipeline.forFailedUpdatesAndGeneratedKeys();
  }

  public List<FailedUpdate<T>> forFailedUpdates() {
    return this.forFailedUpdates(1);
  }

  public List<FailedUpdate<T>> forFailedUpdates(int expectedUpdateCount) {
    if (expectedUpdateCount < 0) {
      throw new IllegalArgumentException("expected update count must be positive");
    }
    BatchUpdateForFailedUpdatesPipeline<T> pipeline = new BatchUpdateForFailedUpdatesPipeline<>(
            this.dataSource, this.exceptionAdapter, this.creator, this.batchArgs, this.batchSize, this.setter);
    return pipeline.forFailedUpdates(expectedUpdateCount);
  }

}
