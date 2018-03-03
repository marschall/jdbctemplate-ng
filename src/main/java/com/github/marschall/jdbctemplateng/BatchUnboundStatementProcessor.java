package com.github.marschall.jdbctemplateng;

import javax.sql.DataSource;

import com.github.marschall.jdbctemplateng.api.NamedPreparedStatementSetterFactory;
import com.github.marschall.jdbctemplateng.api.PreparedStatementCreator;
import com.github.marschall.jdbctemplateng.api.PreparedStatementCustomizer;

public final class BatchUnboundStatementProcessor  extends UnboundStatementProcessor {

  BatchUnboundStatementProcessor(DataSource dataSource, PreparedStatementCreator creator, NamedPreparedStatementSetterFactory namedFactory) {
    super(dataSource, creator, namedFactory);
  }

  public BatchUnboundStatementProcessor customizeStatement(PreparedStatementCustomizer customizer) {
    return new BatchUnboundStatementProcessor(this.dataSource, this.decorateCreator(customizer), this.namedFactory);
  }

}
