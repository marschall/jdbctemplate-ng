package com.github.marschall.jdbctemplateng;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import com.github.marschall.jdbctemplateng.api.PreparedStatementCreator;
import com.github.marschall.jdbctemplateng.api.PreparedStatementSetter;
import com.github.marschall.jdbctemplateng.api.RowMapper;
import com.github.marschall.jdbctemplateng.api.SQLExceptionAdapter;

public final class QueryRowProcessor<T> {

  private final DataSource dataSource;
  private final SQLExceptionAdapter exceptionAdapter;
  private final PreparedStatementCreator creator;
  private final PreparedStatementSetter setter;
  private final RowMapper<T> rowMapper;

  QueryRowProcessor(DataSource dataSource, SQLExceptionAdapter exceptionAdapter,
          PreparedStatementCreator creator, PreparedStatementSetter setter, RowMapper<T> rowMapper) {
    this.dataSource = dataSource;
    this.exceptionAdapter = exceptionAdapter;
    this.creator = creator;
    this.setter = setter;
    this.rowMapper = rowMapper;
  }

  public <R, A> R collect(Collector<? super T, A, R> collector) {
    QueryPipeline<T,R,A> pipeline = new QueryPipeline<>(
            this.dataSource, this.exceptionAdapter, this.creator, this.setter, this.rowMapper, collector);
    return pipeline.executeTranslated();
  }

  public List<T> collectToList() {
    // TODO should probably be optimized
    return this.collect(Collectors.toList());
  }

  public Optional<T> collectToOptional() {
    return this.collect(MoreCollectors.toOptional());
  }

  public T collectToUniqueObject() {
    // TODO should probably be optimized
    return this.collectToOptional().orElseThrow(() -> {
      // TODO better exception
      return this.exceptionAdapter.wrongResultSetSize(1, 0, null);
    });
  }

}
