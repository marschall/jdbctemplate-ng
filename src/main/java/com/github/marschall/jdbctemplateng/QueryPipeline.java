package com.github.marschall.jdbctemplateng;

import static com.github.marschall.jdbctemplateng.SqlExtractor.extractSql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.BiConsumer;
import java.util.stream.Collector;
import java.util.stream.Collector.Characteristics;

import javax.sql.DataSource;

import com.github.marschall.jdbctemplateng.api.PreparedStatementCreator;
import com.github.marschall.jdbctemplateng.api.PreparedStatementSetter;
import com.github.marschall.jdbctemplateng.api.RowMapper;
import com.github.marschall.jdbctemplateng.api.SQLExceptionAdapter;

class QueryPipeline<T, R, A> {

  private final DataSource dataSource;

  private final SQLExceptionAdapter exceptionAdapter;

  private final PreparedStatementCreator creator;

  private final PreparedStatementSetter setter;

  private final RowMapper<T> mapper;

  private final Collector<? super T, A, R> collector;


  QueryPipeline(DataSource dataSource, SQLExceptionAdapter exceptionAdapter,
          PreparedStatementCreator creator, PreparedStatementSetter setter, RowMapper<T> mapper, Collector<? super T, A, R> collector) {
    this.dataSource = dataSource;
    this.exceptionAdapter = exceptionAdapter;
    this.creator = creator;
    this.setter = setter;
    this.mapper = mapper;
    this.collector = collector;
  }

  R executeTranslated() {
    try {
      return this.execute();
    } catch (SQLException e) {
      throw this.exceptionAdapter.translate(extractSql(this.creator), e);
    }
  }

  R execute() throws SQLException {
    try (Connection connection = this.dataSource.getConnection();
         PreparedStatement preparedStatement = this.creator.createPreparedStatement(connection)) {
      this.setter.setValues(preparedStatement);

      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        A state = this.collector.supplier().get();
        BiConsumer<A, ? super T> accumulator = this.collector.accumulator();

        while (resultSet.next()) {
          T row = this.mapper.mapRow(resultSet);
          accumulator.accept(state, row);
        }

        if (this.collector.characteristics().contains(Characteristics.IDENTITY_FINISH)) {
          return (R) state;
        } else {
          return this.collector.finisher().apply(state);
        }

      }
    }
  }

}
