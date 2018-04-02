package com.github.marschall.jdbctemplateng.api;

import java.sql.SQLException;

/**
 * Translates a checked {@link SQLException} to an unchecked exception.
 *
 * <p>Very similar to {@link org.springframework.jdbc.support.SQLExceptionTranslator}.</p>
 */
public interface SQLExceptionAdapter {

  /**
   * Translates a checked {@link SQLException} to an unchecked exception.
   * Does not throw the exception, only creates an instance
   *
   * @param sql the JDBC query string used, may be null
   * @param exception the exception to translate, should be passed as cause to
   *  the new exception instance returned by this method
   * @return the unchecked exception instance
   */
  RuntimeException translate(String sql, SQLException exception);

  RuntimeException unsupportedFeature(String featureName);

  RuntimeException wrongUpdateCount(int expected, int actual, String sql);

  RuntimeException wrongUpdateCount(long expected, long actual, String sql);

  RuntimeException wrongResultSetSize(int expected, int actual, String sql);

}