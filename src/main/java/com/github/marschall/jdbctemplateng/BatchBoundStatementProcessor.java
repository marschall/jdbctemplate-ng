package com.github.marschall.jdbctemplateng;

import java.util.Collection;

import javax.sql.DataSource;

import com.github.marschall.jdbctemplateng.api.ParameterizedPreparedStatementSetter;
import com.github.marschall.jdbctemplateng.api.PreparedStatementCreator;

public final class BatchBoundStatementProcessor<T> {

  private final DataSource dataSource;
  private final PreparedStatementCreator creator;
  private final Collection<T> batchArgs;
  private final int batchSize;
  private final ParameterizedPreparedStatementSetter<T> setter;

  BatchBoundStatementProcessor(DataSource dataSource, PreparedStatementCreator creator, Collection<T> batchArgs, int batchSize, ParameterizedPreparedStatementSetter<T> setter) {
    this.dataSource = dataSource;
    this.creator = creator;
    this.batchArgs = batchArgs;
    this.batchSize = batchSize;
    this.setter = setter;
  }

}
