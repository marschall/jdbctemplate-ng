package com.github.marschall.jdbctemplateng.api;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@FunctionalInterface
public interface PreparedStatementCreator {

  PreparedStatement createPreparedStatement(Connection connection) throws SQLException;

}
