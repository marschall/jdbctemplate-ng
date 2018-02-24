package com.github.marschall.jdbctemplateng;

import javax.sql.DataSource;

import com.github.marschall.jdbctemplateng.api.PreparedStatementCreator;
import com.github.marschall.jdbctemplateng.api.PreparedStatementSetter;
import com.github.marschall.jdbctemplateng.api.RowMapper;

public final class QueryBoundStatementProcessor extends BoundStatementProcessor {

  QueryBoundStatementProcessor(DataSource dataSource, PreparedStatementCreator creator, PreparedStatementSetter setter) {
    super(dataSource, creator, setter);
  }

  public <T> QueryRowProcessor<T> mapping(RowMapper<T> rowMapper) {
    return new QueryRowProcessor<>(this.dataSource, this.creator, this.setter, rowMapper);
  }

  public <T> QueryRowProcessor<T> forObject(Class<T> requiredType) {
    return this.mapping((resultSet, rowNum) -> resultSet.getObject(1, requiredType));
  }

}
