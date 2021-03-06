package com.github.marschall.jdbctemplateng;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.hsqldb.jdbc.JDBCDataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

public class HsqlJdbcOperationBuilderTest extends AbstractJdbcOperationBuilderTest {

  private static DataSource dataSource;
  private static Connection connection;

  @BeforeAll
  static void setUpConnection() throws SQLException {
    JDBCDataSource hsqlDataSource = new JDBCDataSource();
    hsqlDataSource.setUrl("jdbc:hsqldb:mem:marschall");

    connection = hsqlDataSource.getConnection();

    dataSource = new SingleConnectionDataSource(connection, hsqlDataSource);

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

}
