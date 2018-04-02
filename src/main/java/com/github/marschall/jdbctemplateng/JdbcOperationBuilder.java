package com.github.marschall.jdbctemplateng;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.Objects;

import javax.sql.DataSource;

import com.github.marschall.jdbctemplateng.api.NamedPreparedStatementSetterFactory;
import com.github.marschall.jdbctemplateng.api.PreparedStatementCreator;
import com.github.marschall.jdbctemplateng.api.PreparedStatementSetter;

public final class JdbcOperationBuilder {

  private final DataSource dataSource;

  private final NamedPreparedStatementSetterFactory namedPreparedStatementSetterFactory;

  public JdbcOperationBuilder(DataSource dataSource, NamedPreparedStatementSetterFactory namedPreparedStatementSetterFactory) {
    Objects.requireNonNull(dataSource, "dataSource");
    Objects.requireNonNull(namedPreparedStatementSetterFactory, "namedPreparedStatementSetterFactory");
    this.dataSource = dataSource;
    this.namedPreparedStatementSetterFactory = namedPreparedStatementSetterFactory;
  }

  public JdbcOperationBuilder(DataSource dataSource) {
    this(dataSource, UnsupportedNamedPreparedStatementSetterFactory.INSTANCE);
  }

  public void execute(String sql) {
    try (Connection connection = this.dataSource.getConnection();
         Statement statement = connection.createStatement()) {
      statement.execute(sql);
    } catch (SQLException e) {
      throw UncheckedSQLExceptionAdapter.INSTANCE.translate(sql, e);
    }
  }

  public QueryUnboundStatementProcessor query(PreparedStatementCreator preparedStatementCreator) {
    Objects.requireNonNull(preparedStatementCreator, "preparedStatementCreator");
    return new QueryUnboundStatementProcessor(this.dataSource, preparedStatementCreator, this.namedPreparedStatementSetterFactory);
  }

  public QueryUnboundStatementProcessor query(String sql) {
    Objects.requireNonNull(sql, "sql");
    return this.query(connection -> connection.prepareStatement(sql));
  }

  public QueryUnboundStatementProcessor query(String sql, int resultSetType, int resultSetConcurrency) {
    Objects.requireNonNull(sql, "sql");
    return this.query(connection -> connection.prepareStatement(sql, resultSetType, resultSetConcurrency));
  }

  public QueryUnboundStatementProcessor query(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) {
    Objects.requireNonNull(sql, "sql");
    return this.query(connection -> connection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability));
  }

  public UpdateUnboundStatementProcessor update(String sql) {
    Objects.requireNonNull(sql, "sql");
    return this.update(connection -> connection.prepareStatement(sql));
  }

  public UpdateUnboundStatementProcessor update(String sql, int autoGeneratedKeys) {
    Objects.requireNonNull(sql, "sql");
    return this.update(connection -> connection.prepareStatement(sql, autoGeneratedKeys));
  }

  public UpdateUnboundStatementProcessor update(String sql, int columnIndexes[]) {
    Objects.requireNonNull(sql, "sql");
    Objects.requireNonNull(columnIndexes, "columnIndexes");
    return this.update(connection -> connection.prepareStatement(sql, columnIndexes));
  }

  public UpdateUnboundStatementProcessor update(String sql, String columnNames[]) {
    Objects.requireNonNull(sql, "sql");
    Objects.requireNonNull(columnNames, "columnNames");
    return this.update(connection -> connection.prepareStatement(sql, columnNames));
  }

  public UpdateUnboundStatementProcessor update(PreparedStatementCreator preparedStatementCreator) {
    Objects.requireNonNull(preparedStatementCreator, "preparedStatementCreator");
    return new UpdateUnboundStatementProcessor(this.dataSource, preparedStatementCreator, this.namedPreparedStatementSetterFactory);
  }

  public BatchUnboundStatementProcessor batchUpdate(String sql) {
    Objects.requireNonNull(sql, "sql");
    return this.batchUpdate(connection -> connection.prepareStatement(sql));
  }

  public BatchUnboundStatementProcessor batchUpdate(String sql, int autoGeneratedKeys) {
    Objects.requireNonNull(sql, "sql");
    return this.batchUpdate(connection -> connection.prepareStatement(sql, autoGeneratedKeys));
  }

  public BatchUnboundStatementProcessor batchUpdate(String sql, int columnIndexes[]) {
    Objects.requireNonNull(sql, "sql");
    Objects.requireNonNull(columnIndexes, "columnIndexes");
    return this.batchUpdate(connection -> connection.prepareStatement(sql, columnIndexes));
  }

  public BatchUnboundStatementProcessor batchUpdate(String sql, String columnNames[]) {
    Objects.requireNonNull(sql, "sql");
    Objects.requireNonNull(columnNames, "columnNames");
    return this.batchUpdate(connection -> connection.prepareStatement(sql, columnNames));
  }

  public BatchUnboundStatementProcessor batchUpdate(PreparedStatementCreator preparedStatementCreator) {
    Objects.requireNonNull(preparedStatementCreator, "preparedStatementCreator");
    return new BatchUnboundStatementProcessor(this.dataSource, preparedStatementCreator, this.namedPreparedStatementSetterFactory);
  }

  static final class UnsupportedNamedPreparedStatementSetterFactory implements NamedPreparedStatementSetterFactory {

    static final NamedPreparedStatementSetterFactory INSTANCE = new UnsupportedNamedPreparedStatementSetterFactory();

    @Override
    public PreparedStatementSetter newNamedPreparedStatementSetter(Collection<Entry<String, Object>> namedParameters) {
      throw UncheckedSQLExceptionAdapter.unsupportedFeature("named parameters");
    }

  }

}