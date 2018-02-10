package com.github.marschall.jdbctemplateng;

import java.sql.SQLException;
import java.util.Objects;

/**
 * Wraps an {@link SQLException} with an unchecked exception.
 *
 * @see java.io.UncheckedIOException
 */
public final class UncheckedSQLException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  private final String sql;

  UncheckedSQLException(String message, String sql, SQLException cause) {
    super(message, Objects.requireNonNull(cause));
    this.sql = sql;
  }

  /**
   * Returns the SQL query that was executed.
   *
   * @return the SQL query that was executed, may be null
   */
  public String getSql() {
    return this.sql;
  }

  /**
   * Convenience method that returns the cause as type {@link SQLException}
   * avoiding the need to cast the result.
   *
   * @return the exception cause, never {@code null}
   */
  @Override
  public SQLException getCause() {
    return (SQLException) super.getCause();
  }

}
