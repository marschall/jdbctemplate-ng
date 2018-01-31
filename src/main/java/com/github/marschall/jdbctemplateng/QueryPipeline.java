package com.github.marschall.jdbctemplateng;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.BiConsumer;
import java.util.stream.Collector;

import javax.sql.DataSource;

class QueryPipeline<T, R, A> {

  private PreparedStatementCreator creator;

  private PreparedStatementSetter setter;

  private RowMapper<T> mapper;

  private Collector<? super T, A, R> collector;

  private DataSource dataSource;


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
