package com.github.marschall.jdbctemplateng;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

class H2JdbcTemplateNgTest extends AbstractJdbcTemplateNgTest {

  private static DataSource dataSource;
  private static Connection connection;

  @BeforeAll
  static void setUpConnection() throws SQLException {
    JdbcDataSource h2DataSource = new JdbcDataSource();
    h2DataSource.setUrl("jdbc:h2:mem:");

    connection = h2DataSource.getConnection();

    dataSource = new SingleConnectionDataSource(connection, h2DataSource);

    try (Statement statement = connection.createStatement()) {
      statement.execute("CREATE TABLE test_table ("
              + "id IDENTITY PRIMARY KEY,"
              + "test_value INTEGER"
              + ")");
      statement.execute("CREATE TABLE single_row_table ("
              + "dummy VARCHAR(1) PRIMARY KEY"
              + ")");
      statement.execute("INSERT INTO single_row_table(dummy)"
              + " VALUES ('X') ");
    }
  }

  @AfterAll
  static void tearDownConnection() throws SQLException {
    connection.close();
  }

  @Override
  DataSource getDataSource() {
   return dataSource;
 }

  @Override
  boolean largeUpdateSupported() {
    return false;
  }

}
