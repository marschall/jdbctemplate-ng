package com.github.marschall.jdbctemplateng.api;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface ResultSetGeneratedKeyCallback<R> {

  void generatedKey(R row, ResultSet resultSet) throws SQLException;

}
