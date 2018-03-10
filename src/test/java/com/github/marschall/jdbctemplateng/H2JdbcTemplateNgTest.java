package com.github.marschall.jdbctemplateng;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

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



  @Test
  void testBatchUpdateGeneratedKey() {
    // work around for https://github.com/h2database/h2database/pull/955
    SimpleDTO dto1 = new SimpleDTO(23);
    SimpleDTO dto2 = new SimpleDTO(42);

    List<SimpleDTO> dtos = Arrays.asList(dto1, dto2);
    List<FailedUpdate<SimpleDTO>> failedUpdates = this.getJdbcTemplate()
            .batchUpdate("INSERT INTO test_table(test_value) VALUES (?)", Statement.RETURN_GENERATED_KEYS)
            .binding(dtos, 10, (preparedStatement, dto) -> preparedStatement.setInt(1, dto.getTestValue()))
            .forFailedUpdates(resultSet -> resultSet.getInt(1), SimpleDTO::setPrimaryKey);

    assertThat(failedUpdates).isEmpty();;
    assertNotNull(dto1.getPrimaryKey());
    assertNotNull(dto2.getPrimaryKey());
  }

}
