package com.github.marschall.jdbctemplateng.api;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@FunctionalInterface
public interface PreparedStatementCustomizer {

  void customize(PreparedStatement preparedStatement) throws SQLException;

}
