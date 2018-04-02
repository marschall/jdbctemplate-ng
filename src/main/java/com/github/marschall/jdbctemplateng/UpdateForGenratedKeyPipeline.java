package com.github.marschall.jdbctemplateng;

import static com.github.marschall.jdbctemplateng.SqlExtractor.extractSql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.github.marschall.jdbctemplateng.api.PreparedStatementCreator;
import com.github.marschall.jdbctemplateng.api.PreparedStatementSetter;
import com.github.marschall.jdbctemplateng.api.RowMapper;
import com.github.marschall.jdbctemplateng.api.SQLExceptionAdapter;

final class UpdateForGenratedKeyPipeline<T> {

  private final DataSource dataSource;

  private final SQLExceptionAdapter exceptionAdapter;

  private final PreparedStatementCreator creator;

  private final PreparedStatementSetter setter;

  private final RowMapper<T> mapper;

  UpdateForGenratedKeyPipeline(DataSource dataSource, SQLExceptionAdapter exceptionAdapter,
          PreparedStatementCreator creator, PreparedStatementSetter setter, RowMapper<T> mapper) {
    this.dataSource = dataSource;
    this.exceptionAdapter = exceptionAdapter;
    this.creator = creator;
    this.setter = setter;
    this.mapper = mapper;
  }

  T executeForGeneratedKeyTranslated() {
    try {
      return this.execute();
    } catch (SQLException e) {
      throw this.exceptionAdapter.translate(extractSql(this.creator), e);
    }
  }

  private T execute() throws SQLException {
    try (Connection connection = this.dataSource.getConnection();
         PreparedStatement preparedStatement = this.creator.createPreparedStatement(connection)) {
      this.setter.setValues(preparedStatement);

      int updateCount = preparedStatement.executeUpdate();
      if (updateCount != 1) {
        throw this.exceptionAdapter.wrongUpdateCount(1, updateCount, extractSql(this.creator));
      }

      try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
        if (!generatedKeys.next()) {
          throw this.exceptionAdapter.wrongResultSetSize(1, 0, extractSql(this.creator));
        }
        T generatedKey = this.mapper.mapRow(generatedKeys);
        if (generatedKeys.next()) {
          throw this.exceptionAdapter.wrongResultSetSize(1, 2, extractSql(this.creator));
        }
        return generatedKey;
      }
    }
  }

}
