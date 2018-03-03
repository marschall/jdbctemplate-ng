package com.github.marschall.jdbctemplateng;

import javax.sql.DataSource;

import com.github.marschall.jdbctemplateng.api.PreparedStatementCreator;
import com.github.marschall.jdbctemplateng.api.PreparedStatementSetter;
import com.github.marschall.jdbctemplateng.api.RowMapper;

public final class UpdateBoundStatementProcessor extends BoundStatementProcessor {

  UpdateBoundStatementProcessor(DataSource dataSource, PreparedStatementCreator creator, PreparedStatementSetter setter) {
    super(dataSource, creator, setter);
  }

  public int forUpdateCount() {
    UpdateForRowCountPipeline pipeline = new UpdateForRowCountPipeline(this.dataSource, this.creator, this.setter);
    return pipeline.executeForUpdateCountTranslated();
  }

  public void expectUpdateCount(int expected) {
    UpdateForRowCountPipeline pipeline = new UpdateForRowCountPipeline(this.dataSource, this.creator, this.setter);
    pipeline.executeAndExpectUpdateCountTranslated(expected);
  }

  public <T> T forGeneratedKey(RowMapper<T> keyExtractor) {
    UpdateForGenratedKeyPipeline<T> pipeline = new UpdateForGenratedKeyPipeline<>(this.dataSource, this.creator, this.setter, keyExtractor);
    return pipeline.executeForGeneratedKeyTranslated();
  }

  public <T> T forGeneratedKey(Class<T> type) {
    return this.forGeneratedKey(resultSet -> resultSet.getObject(1, type));
  }

}
