package com.github.marschall.jdbctemplateng;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.github.marschall.jdbctemplateng.api.PreparedStatementCreator;
import com.github.marschall.jdbctemplateng.api.PreparedStatementSetter;

final class UpdateForLargeUpdateCountPipeline {

  private final DataSource dataSource;

  private final PreparedStatementCreator creator;

  private final PreparedStatementSetter setter;

  UpdateForLargeUpdateCountPipeline(DataSource dataSource, PreparedStatementCreator creator, PreparedStatementSetter setter) {
    this.dataSource = dataSource;
    this.creator = creator;
    this.setter = setter;
  }

  long executeForUpdateCountTranslated() {
    try {
      return this.execute();
    } catch (SQLException e) {
      throw UncheckedSQLExceptionAdapter.INSTANCE.translate(null, e);
    }
  }

  void executeAndExpectUpdateCountTranslated(long expected) {
    try {
      long actual = this.execute();
      if (actual != expected) {
        throw UncheckedSQLExceptionAdapter.wrongUpdateCount(expected, actual, null);
      }
    } catch (SQLException e) {
      throw UncheckedSQLExceptionAdapter.INSTANCE.translate(null, e);
    }
  }

  private long execute() throws SQLException {
    try (Connection connection = this.dataSource.getConnection();
         PreparedStatement preparedStatement = this.creator.createPreparedStatement(connection)) {
      this.setter.setValues(preparedStatement);

      return preparedStatement.executeLargeUpdate();
    }
  }

}
