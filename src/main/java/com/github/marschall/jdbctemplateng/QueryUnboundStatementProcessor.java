package com.github.marschall.jdbctemplateng;

public class QueryUnboundStatementProcessor {

  public QueryBoundStatementProcessor binding(PreparedStatementSetter preparedStatementSetter) {
    return null;
  }

  public QueryBoundStatementProcessor binding(Object... bindParameters) {
    return this.binding(preparedStatement -> {
      for (int i = 0; i < bindParameters.length; i++) {
        preparedStatement.setObject(i + 1, bindParameters[i]);
      }
    });
  }
}
