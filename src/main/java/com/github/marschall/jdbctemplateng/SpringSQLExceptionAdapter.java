package com.github.marschall.jdbctemplateng;

import java.sql.SQLException;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.JdbcUpdateAffectedIncorrectNumberOfRowsException;
import org.springframework.jdbc.support.SQLExceptionTranslator;

import com.github.marschall.jdbctemplateng.api.SQLExceptionAdapter;

final class SpringSQLExceptionAdapter implements SQLExceptionAdapter {

  private final SQLExceptionTranslator translator;

  SpringSQLExceptionAdapter(SQLExceptionTranslator translator) {
    this.translator = translator;
  }

  @Override
  public RuntimeException translate(String sql, SQLException exception) {
    return this.translator.translate(null, sql, exception);
  }

  @Override
  public RuntimeException unsupportedFeature(String featureName) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public RuntimeException wrongUpdateCount(int expected, int actual, String sql) {
    return new JdbcUpdateAffectedIncorrectNumberOfRowsException(sql, expected, actual);
  }

  @Override
  public RuntimeException wrongUpdateCount(long expected, long actual, String sql) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public RuntimeException wrongResultSetSize(int expected, int actual, String sql) {
    return new IncorrectResultSizeDataAccessException(sql, expected, actual);
  }

}