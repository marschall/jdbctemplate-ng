package com.github.marschall.jdbctemplateng;

public class BoundStatementProcessor {

  public <T> RowProcessor<T> mapping(RowMapper<T> rowMapper) {
    return null;
  }

  public <T> RowProcessor<T> forObject(Class<T> requiredType) {
    return this.mapping((resultSet, rowNum) -> resultSet.getObject(1, requiredType));
  }

}
