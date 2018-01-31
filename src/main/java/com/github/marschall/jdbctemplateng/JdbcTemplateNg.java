package com.github.marschall.jdbctemplateng;

import java.util.Objects;

import javax.sql.DataSource;

public class JdbcTemplateNg {

  private final DataSource dataSource;

  public JdbcTemplateNg(DataSource dataSource) {
    Objects.requireNonNull(dataSource, "dataSource");
    this.dataSource = dataSource;
  }

  public QueryUnboundStatementProcessor query(PreparedStatementCreator preparedStatementCreator) {
    return null;
  }

  public QueryUnboundStatementProcessor query(String sql) {
    return this.query(connection -> connection.prepareStatement(sql));
  }

  public QueryUnboundStatementProcessor query(String sql, int resultSetType, int resultSetConcurrency) {
    return this.query(connection -> connection.prepareStatement(sql, resultSetType, resultSetConcurrency));
  }

  public QueryUnboundStatementProcessor query(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) {
    return this.query(connection -> connection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability));
  }

  public UpdateUnboundStatementProcessor update(String sql) {
    return this.update(connection -> connection.prepareStatement(sql));
  }

  public UpdateUnboundStatementProcessor update(String sql, int columnIndexes[]) {
    return this.update(connection -> connection.prepareStatement(sql, columnIndexes));
  }

  public UpdateUnboundStatementProcessor update(String sql, String columnNames[]) {
    return this.update(connection -> connection.prepareStatement(sql, columnNames));
  }

  public UpdateUnboundStatementProcessor update(PreparedStatementCreator preparedStatementCreator) {
    return null;
  }

}
