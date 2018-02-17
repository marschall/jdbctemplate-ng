package com.github.marschall.jdbctemplateng;

import javax.sql.DataSource;

import com.github.marschall.jdbctemplateng.api.PreparedStatementCreator;
import com.github.marschall.jdbctemplateng.api.PreparedStatementCustomizer;
import com.github.marschall.jdbctemplateng.api.PreparedStatementSetter;

public final class QueryUnboundStatementProcessor extends UnboundStatementProcessor {

  QueryUnboundStatementProcessor(DataSource dataSource, PreparedStatementCreator creator) {
    super(dataSource, creator);
  }

  public QueryBoundStatementProcessor binding(PreparedStatementSetter preparedStatementSetter) {
    return new QueryBoundStatementProcessor(this.dataSource, this.creator, preparedStatementSetter);
  }

  public QueryBoundStatementProcessor binding(Object... bindParameters) {
    return this.binding(preparedStatementSetter(bindParameters));
  }

  public QueryUnboundStatementProcessor customizeStatement(PreparedStatementCustomizer customizer) {
    return new QueryUnboundStatementProcessor(this.dataSource, this.decorateCreator(customizer));
  }

}
