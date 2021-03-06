package com.github.marschall.jdbctemplateng;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.derby.jdbc.EmbeddedDataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

public class DerbyJdbcOperationBuilderTest extends AbstractJdbcOperationBuilderTest {

  private static DataSource dataSource;
  private static Connection connection;

  @BeforeAll
  static void setUpConnection() throws SQLException {
    EmbeddedDataSource derbyDataSource = new EmbeddedDataSource();
    derbyDataSource.setDatabaseName("memory:marschall");
    derbyDataSource.setCreateDatabase("create");

    connection = derbyDataSource.getConnection();

    dataSource = new SingleConnectionDataSource(connection, derbyDataSource);

    try (Statement statement = connection.createStatement()) {
      statement.execute("CREATE TABLE test_table ("
              + "id INTEGER NOT NULL GENERATED BY DEFAULT AS IDENTITY (START WITH 1, INCREMENT BY 1) PRIMARY KEY,"
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
    try (Statement statement = connection.createStatement()) {
      statement.execute("DROP TABLE test_table");
      statement.execute("DROP TABLE single_row_table");
    }
    connection.close();
  }

  @Override
  DataSource getDataSource() {
   return dataSource;
 }

  @Override
  void testBatchUpdateGeneratedKey() {
    // https://issues.apache.org/jira/browse/DERBY-6994
    // https://issues.apache.org/jira/browse/DERBY-3609
    return;
  }

}
