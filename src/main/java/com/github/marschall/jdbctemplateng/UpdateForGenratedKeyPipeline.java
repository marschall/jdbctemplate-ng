package com.github.marschall.jdbctemplateng;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.github.marschall.jdbctemplateng.api.PreparedStatementCreator;
import com.github.marschall.jdbctemplateng.api.PreparedStatementSetter;
import com.github.marschall.jdbctemplateng.api.RowMapper;

final class UpdateForGenratedKeyPipeline<T> {

  private final PreparedStatementCreator creator;

  private final PreparedStatementSetter setter;

  private final DataSource dataSource;

  private final RowMapper<T> mapper;

  UpdateForGenratedKeyPipeline(DataSource dataSource, PreparedStatementCreator creator, PreparedStatementSetter setter, RowMapper<T> mapper) {
    this.dataSource = dataSource;
    this.creator = creator;
    this.setter = setter;
    this.mapper = mapper;
  }

  T executeForGeneratedKeyTranslated() {
    try {
      return this.execute();
    } catch (SQLException e) {
      throw UncheckedSQLExceptionAdapter.INSTANCE.translate(null, e);
    }
  }

  T execute() throws SQLException {
    try (Connection connection = this.dataSource.getConnection();
         PreparedStatement preparedStatement = this.creator.createPreparedStatement(connection)) {
      this.setter.setValues(preparedStatement);

      int updateCount = preparedStatement.executeUpdate();
      if (updateCount != 1) {
        throw UncheckedSQLExceptionAdapter.wrongUpdateCount(1, updateCount, null);
      }

      try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
        if (!generatedKeys.next()) {
          throw UncheckedSQLExceptionAdapter.wrongResultSetSize(1, 0, null);
        }
        T generatedKey = this.mapper.mapRow(generatedKeys, 0);
        if (generatedKeys.next()) {
          throw UncheckedSQLExceptionAdapter.wrongResultSetSize(1, 2, null);
        }
        return generatedKey;
      }
    }
  }

}
