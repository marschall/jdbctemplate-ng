package com.github.marschall.jdbctemplateng;

import javax.sql.DataSource;

import com.github.marschall.jdbctemplateng.api.PreparedStatementCreator;
import com.github.marschall.jdbctemplateng.api.PreparedStatementCustomizer;
import com.github.marschall.jdbctemplateng.api.PreparedStatementSetter;

public final class BatchUnboundStatementProcessor  extends UnboundStatementProcessor {

  BatchUnboundStatementProcessor(DataSource dataSource, PreparedStatementCreator creator) {
    super(dataSource, creator);
  }

  public BatchUnboundStatementProcessor customizeStatement(PreparedStatementCustomizer customizer) {
    return new BatchUnboundStatementProcessor(this.dataSource, this.decorateCreator(customizer));
  }

}
