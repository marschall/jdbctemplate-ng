package com.github.marschall.jdbctemplateng;

public class QueryBoundStatementProcessor {

  public <T> QueryRowProcessor<T> mapping(RowMapper<T> rowMapper) {
    return null;
  }

  public <T> QueryRowProcessor<T> forObject(Class<T> requiredType) {
    return this.mapping((resultSet, rowNum) -> resultSet.getObject(1, requiredType));
  }

}
