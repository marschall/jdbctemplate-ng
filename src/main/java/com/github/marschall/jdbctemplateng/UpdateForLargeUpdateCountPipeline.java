package com.github.marschall.jdbctemplateng;

import static com.github.marschall.jdbctemplateng.SqlExtractor.extractSql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.github.marschall.jdbctemplateng.api.PreparedStatementCreator;
import com.github.marschall.jdbctemplateng.api.PreparedStatementSetter;
import com.github.marschall.jdbctemplateng.api.SQLExceptionAdapter;

final class UpdateForLargeUpdateCountPipeline {

  private final DataSource dataSource;

  private final SQLExceptionAdapter exceptionAdapter;

  private final PreparedStatementCreator creator;

  private final PreparedStatementSetter setter;

  UpdateForLargeUpdateCountPipeline(DataSource dataSource, SQLExceptionAdapter exceptionAdapter,
          PreparedStatementCreator creator, PreparedStatementSetter setter) {
    this.dataSource = dataSource;
    this.exceptionAdapter = exceptionAdapter;
    this.creator = creator;
    this.setter = setter;
  }

  long executeForUpdateCountTranslated() {
    try {
      return this.execute();
    } catch (SQLException e) {
      throw this.exceptionAdapter.translate(extractSql(this.creator), e);
    }
  }

  void executeAndExpectUpdateCountTranslated(long expected) {
    try {
      long actual = this.execute();
      if (actual != expected) {
        throw this.exceptionAdapter.wrongLargeUpdateCount(expected, actual, extractSql(this.creator));
      }
    } catch (SQLException e) {
      throw this.exceptionAdapter.translate(extractSql(this.creator), e);
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
