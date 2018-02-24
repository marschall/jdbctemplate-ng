package com.github.marschall.jdbctemplateng;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.BiConsumer;
import java.util.stream.Collector;

import javax.sql.DataSource;

import com.github.marschall.jdbctemplateng.api.PreparedStatementCreator;
import com.github.marschall.jdbctemplateng.api.PreparedStatementSetter;
import com.github.marschall.jdbctemplateng.api.RowMapper;

class QueryPipeline<T, R, A> {

  private final DataSource dataSource;

  private final PreparedStatementCreator creator;

  private final PreparedStatementSetter setter;

  private final RowMapper<T> mapper;

  private final Collector<? super T, A, R> collector;


  QueryPipeline(DataSource dataSource, PreparedStatementCreator creator, PreparedStatementSetter setter, RowMapper<T> mapper, Collector<? super T, A, R> collector) {
    this.dataSource = dataSource;
    this.creator = creator;
    this.setter = setter;
    this.mapper = mapper;
    this.collector = collector;
  }

  R executeTranslated() {
    try {
      return this.execute();
    } catch (SQLException e) {
      throw UncheckedSQLExceptionAdapter.INSTANCE.translate(null, e);
    }
  }

  R execute() throws SQLException {
    try (Connection connection = this.dataSource.getConnection();
         PreparedStatement preparedStatement = this.creator.createPreparedStatement(connection)) {
      this.setter.setValues(preparedStatement);

      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        A state = this.collector.supplier().get();
        BiConsumer<A, ? super T> accumulator = this.collector.accumulator();
        int rowNum = 0;

        while (resultSet.next()) {
          T row = this.mapper.mapRow(resultSet, rowNum++);
          accumulator.accept(state, row);
        }

        // TODO
//        if (this.collector.characteristics().contains(Characteristics.IDENTITY_FINISH)) {
//
//        }

        return this.collector.finisher().apply(state);
      }
    }
  }

}
