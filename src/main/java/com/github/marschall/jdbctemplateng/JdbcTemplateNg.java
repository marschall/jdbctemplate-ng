package com.github.marschall.jdbctemplateng;

import java.util.Objects;

import javax.sql.DataSource;

public class JdbcTemplateNg {

  private final DataSource dataSource;

  public JdbcTemplateNg(DataSource dataSource) {
    Objects.requireNonNull(dataSource, "dataSource");
    this.dataSource = dataSource;
  }

  public UnboundStatementProcessor query(PreparedStatementCreator preparedStatementCreator) {
    return null;
  }

  public UnboundStatementProcessor query(String sql) {
    return this.query(connection -> connection.prepareStatement(sql));
  }

}
