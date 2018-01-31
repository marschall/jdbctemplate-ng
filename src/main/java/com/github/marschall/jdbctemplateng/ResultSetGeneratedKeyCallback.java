package com.github.marschall.jdbctemplateng;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultSetGeneratedKeyCallback<R> {

  void generatedKey(R row, ResultSet resultSet) throws SQLException;

}
