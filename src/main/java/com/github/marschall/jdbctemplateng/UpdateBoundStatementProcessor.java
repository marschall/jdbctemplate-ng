package com.github.marschall.jdbctemplateng;

import javax.sql.DataSource;

import com.github.marschall.jdbctemplateng.api.PreparedStatementCreator;
import com.github.marschall.jdbctemplateng.api.PreparedStatementSetter;
import com.github.marschall.jdbctemplateng.api.RowMapper;
import com.github.marschall.jdbctemplateng.api.SQLExceptionAdapter;

public final class UpdateBoundStatementProcessor extends BoundStatementProcessor {

  UpdateBoundStatementProcessor(DataSource dataSource, SQLExceptionAdapter exceptionAdapter,
          PreparedStatementCreator creator, PreparedStatementSetter setter) {
    super(dataSource, exceptionAdapter, creator, setter);
  }

  public int forUpdateCount() {
    UpdateForUpdateCountPipeline pipeline = new UpdateForUpdateCountPipeline(
            this.dataSource, this.exceptionAdapter, this.creator, this.setter);
    return pipeline.executeForUpdateCountTranslated();
  }

  public void expectUpdateCount(int expected) {
    if (expected < 0) {
      throw new IllegalArgumentException("expected update count must be positive");
    }
    UpdateForUpdateCountPipeline pipeline = new UpdateForUpdateCountPipeline(
            this.dataSource, this.exceptionAdapter, this.creator, this.setter);
    pipeline.executeAndExpectUpdateCountTranslated(expected);
  }

  public long forLargeUpdateCount() {
    UpdateForLargeUpdateCountPipeline pipeline = new UpdateForLargeUpdateCountPipeline(
            this.dataSource, this.exceptionAdapter, this.creator, this.setter);
    return pipeline.executeForUpdateCountTranslated();
  }

  public void expectLargeUpdateCount(long expected) {
    if (expected < 0L) {
      throw new IllegalArgumentException("expected update count must be positive");
    }
    UpdateForLargeUpdateCountPipeline pipeline = new UpdateForLargeUpdateCountPipeline(
            this.dataSource, this.exceptionAdapter, this.creator, this.setter);
    pipeline.executeAndExpectUpdateCountTranslated(expected);
  }

  public <T> T forGeneratedKey(RowMapper<T> keyExtractor) {
    UpdateForGenratedKeyPipeline<T> pipeline = new UpdateForGenratedKeyPipeline<>(
            this.dataSource, this.exceptionAdapter, this.creator, this.setter, keyExtractor);
    return pipeline.executeForGeneratedKeyTranslated();
  }

  public <T> T forGeneratedKey(Class<T> type) {
    return this.forGeneratedKey(resultSet -> resultSet.getObject(1, type));
  }

}
