package com.github.marschall.jdbctemplateng;

public class UnboundStatementProcessor {

  public BoundStatementProcessor binding(PreparedStatementSetter preparedStatementSetter) {
    return null;
  }

  public BoundStatementProcessor binding(Object... bindParameters) {
    return this.binding(preparedStatement -> {
      for (int i = 0; i < bindParameters.length; i++) {
        preparedStatement.setObject(i + 1, bindParameters[i]);
      }
    });
  }
}
