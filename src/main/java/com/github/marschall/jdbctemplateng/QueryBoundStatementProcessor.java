package com.github.marschall.jdbctemplateng;

import javax.sql.DataSource;

import com.github.marschall.jdbctemplateng.api.PreparedStatementCreator;
import com.github.marschall.jdbctemplateng.api.PreparedStatementSetter;
import com.github.marschall.jdbctemplateng.api.RowMapper;

public final class QueryBoundStatementProcessor extends BoundStatementProcessor {

  QueryBoundStatementProcessor(DataSource dataSource, PreparedStatementCreator creator, PreparedStatementSetter setter) {
    super(dataSource, creator, setter);
  }

  public <T> QueryRowProcessor<T> map(RowMapper<T> rowMapper) {
    return new QueryRowProcessor<>(this.dataSource, this.creator, this.setter, rowMapper);
  }

  public <T> QueryRowProcessor<T> mapTo(Class<T> requiredType) {
    return this.map(resultSet -> resultSet.getObject(1, requiredType));
  }

}
