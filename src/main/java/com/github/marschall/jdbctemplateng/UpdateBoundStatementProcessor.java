package com.github.marschall.jdbctemplateng;

import javax.sql.DataSource;

import com.github.marschall.jdbctemplateng.api.PreparedStatementCreator;
import com.github.marschall.jdbctemplateng.api.PreparedStatementSetter;

public final class UpdateBoundStatementProcessor extends BoundStatementProcessor {

  UpdateBoundStatementProcessor(DataSource dataSource, PreparedStatementCreator creator, PreparedStatementSetter setter) {
    super(dataSource, creator, setter);
  }

  public int forUpdateCount() {
    UpdatePipeline pipeline = new UpdatePipeline(this.dataSource, this.creator, this.setter);
    return pipeline.executeForUpdateCountTranslated();
  }

  public void expectUpdateCount(int expected) {
    UpdatePipeline pipeline = new UpdatePipeline(this.dataSource, this.creator, this.setter);
    pipeline.executeAndExpectUpdateCountTranslated(expected);
  }

}
