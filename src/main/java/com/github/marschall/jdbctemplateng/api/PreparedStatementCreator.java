package com.github.marschall.jdbctemplateng.api;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@FunctionalInterface
public interface PreparedStatementCreator {

  // TODO org.springframework.jdbc.core.SqlProvider

  PreparedStatement createPreparedStatement(Connection connection) throws SQLException;

}
