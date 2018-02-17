package com.github.marschall.jdbctemplateng.api;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface RowMapper<T> {

  // REVIEW rowNum?
  T mapRow(ResultSet resultSet, int rowNum) throws SQLException;

}
