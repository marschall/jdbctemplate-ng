package com.github.marschall.jdbctemplateng;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import com.github.marschall.jdbctemplateng.api.PreparedStatementCreator;
import com.github.marschall.jdbctemplateng.api.PreparedStatementSetter;
import com.github.marschall.jdbctemplateng.api.RowMapper;

public class QueryRowProcessor<T> {

  private final DataSource dataSource;
  private final PreparedStatementCreator creator;
  private final PreparedStatementSetter setter;
  private final RowMapper<T> rowMapper;

  QueryRowProcessor(DataSource dataSource, PreparedStatementCreator creator, PreparedStatementSetter setter, RowMapper<T> rowMapper) {
    this.dataSource = dataSource;
    this.creator = creator;
    this.setter = setter;
    this.rowMapper = rowMapper;
    // TODO Auto-generated constructor stub
  }

  public <R, A> R collect(Collector<? super T, A, R> collector) {
    QueryPipeline<T,R,A> pipeline = new QueryPipeline<T, R, A>(this.dataSource, this.creator, this.setter, this.rowMapper, collector);
    return pipeline.executeTranslated();
  }

  public List<T> toList() {
    // TODO should probably be optimized
    return this.collect(Collectors.toList());
  }

  public Optional<T> toOptional() {
    // TODO
    return Optional.empty();
  }

}
