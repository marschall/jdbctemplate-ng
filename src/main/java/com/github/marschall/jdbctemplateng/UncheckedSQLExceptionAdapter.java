package com.github.marschall.jdbctemplateng;

import java.sql.SQLException;

import com.github.marschall.jdbctemplateng.api.SQLExceptionAdapter;

/**
 * A {@link SQLExceptionAdapter} that creates a new {@link UncheckedSQLException}.
 */
final class UncheckedSQLExceptionAdapter implements SQLExceptionAdapter {

  static final SQLExceptionAdapter INSTANCE = new UncheckedSQLExceptionAdapter();

  private UncheckedSQLExceptionAdapter() {
    super();
  }

  @Override
  public RuntimeException translate(String sql, SQLException ex) {
    if (sql != null) {
      return new UncheckedSQLException("failed to execute query: " + sql, sql, ex);
    } else {
      return new UncheckedSQLException("failed to execute query", sql, ex);
    }
  }

  static RuntimeException wrongUpdateCount(int expected, int actual, String sql) {
    // TODO instance variables
    return new UncheckedSQLException("wong update count, expected " + expected + " but was " + actual, sql);
  }


}
