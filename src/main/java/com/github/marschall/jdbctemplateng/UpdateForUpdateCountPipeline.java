package com.github.marschall.jdbctemplateng;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.github.marschall.jdbctemplateng.api.PreparedStatementCreator;
import com.github.marschall.jdbctemplateng.api.PreparedStatementSetter;
import com.github.marschall.jdbctemplateng.api.SQLExceptionAdapter;

final class UpdateForUpdateCountPipeline {

  private final DataSource dataSource;

  private final SQLExceptionAdapter exceptionAdapter;

  private final PreparedStatementCreator creator;

  private final PreparedStatementSetter setter;

  UpdateForUpdateCountPipeline(DataSource dataSource, SQLExceptionAdapter exceptionAdapter,
          PreparedStatementCreator creator, PreparedStatementSetter setter) {
    this.dataSource = dataSource;
    this.exceptionAdapter = exceptionAdapter;
    this.creator = creator;
    this.setter = setter;
  }

  int executeForUpdateCountTranslated() {
    try {
      return this.execute();
    } catch (SQLException e) {
      throw this.exceptionAdapter.translate(null, e);
    }
  }

  void executeAndExpectUpdateCountTranslated(int expected) {
    try {
      int actual = this.execute();
      if (actual != expected) {
        throw this.exceptionAdapter.wrongUpdateCount(expected, actual, null);
      }
    } catch (SQLException e) {
      throw this.exceptionAdapter.translate(null, e);
    }
  }

  private int execute() throws SQLException {
    try (Connection connection = this.dataSource.getConnection();
         PreparedStatement preparedStatement = this.creator.createPreparedStatement(connection)) {
      this.setter.setValues(preparedStatement);

      return preparedStatement.executeUpdate();
    }
  }

}
