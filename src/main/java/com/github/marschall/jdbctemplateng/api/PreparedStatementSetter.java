package com.github.marschall.jdbctemplateng.api;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@FunctionalInterface
public interface PreparedStatementSetter {

  void setValues(PreparedStatement preparedStatement) throws SQLException;

}
