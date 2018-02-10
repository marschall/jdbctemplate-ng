package com.github.marschall.jdbctemplateng;

import javax.sql.DataSource;

public class QueryUnboundStatementProcessor {

  private final DataSource dataSource;

  private final PreparedStatementCreator creator;

  QueryUnboundStatementProcessor(DataSource dataSource, PreparedStatementCreator creator) {
    this.dataSource = dataSource;
    this.creator = creator;
  }

  public QueryBoundStatementProcessor binding(PreparedStatementSetter preparedStatementSetter) {
    return new QueryBoundStatementProcessor(dataSource, creator, preparedStatementSetter);
  }

  public QueryBoundStatementProcessor binding(Object... bindParameters) {
    return this.binding(preparedStatement -> {
      for (int i = 0; i < bindParameters.length; i++) {
        preparedStatement.setObject(i + 1, bindParameters[i]);
      }
    });
  }
}
