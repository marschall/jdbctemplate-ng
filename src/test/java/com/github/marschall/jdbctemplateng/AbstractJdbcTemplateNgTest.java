package com.github.marschall.jdbctemplateng;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.PrintWriter;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.github.marschall.jdbctemplateng.api.RowMapper;

abstract class AbstractJdbcTemplateNgTest {

  private JdbcTemplateNg jdbcTemplate;

   abstract DataSource getDataSource();

  @BeforeEach
  void setUp() {
    this.jdbcTemplate = new JdbcTemplateNg(getDataSource());
  }

  @Test
  void testToList() {
    List<Integer> integers = this.jdbcTemplate
      .query("SELECT 1 FROM single_row_table WHERE ? > 1")
      .binding(23)
      .mapTo(Integer.class)
      .collectToList();
    assertNotNull(integers);
    assertEquals(Collections.singletonList(1), integers);
  }

  @Test
  void customizeStatement() {
    List<Integer> integers = this.jdbcTemplate
            .query("SELECT 1 FROM single_row_table")
            .fetchSize(1)
            .withoutBindParameters()
            .mapTo(Integer.class)
            .collectToList();
    assertNotNull(integers);
    assertEquals(Collections.singletonList(1), integers);
  }

  @Test
  void queryForMap() {
    Map<String, Object> row = this.jdbcTemplate
            .query("SELECT 1, '2' as TWO FROM single_row_table")
            .withoutBindParameters()
            .map(RowMapper.toMap())
            .collectToUniqueObject();

    assertThat(row).hasSize(2);

    assertEquals("2", row.get("TWO"));
  }

  @Test
  @Disabled("unsure if we should support that")
  void queryForMapCaseInsensitive() {
    Map<String, Object> row = this.jdbcTemplate
            .query("SELECT 1 as \"X\", 2 as \"x\" from single_row_table")
            .withoutBindParameters()
            .map(RowMapper.toMap())
            .collectToUniqueObject();

    assertThat(row).hasSize(1);

    assertEquals(Integer.valueOf(1), row.get("X"));
    assertEquals(Integer.valueOf(1), row.get("x"));
  }

  @Test
  void queryForList() {
    List<Object> row = this.jdbcTemplate
            .query("SELECT 1, '2' as TWO FROM single_row_table")
            .withoutBindParameters()
            .map(RowMapper.toList())
            .collectToUniqueObject();

    assertEquals(Arrays.asList(1, "2"), row);
  }

  @Test
  void queryForArray() {
    Object[] row = this.jdbcTemplate
            .query("SELECT 1, '2' as TWO FROM single_row_table")
            .withoutBindParameters()
            .map(RowMapper.toArray())
            .collectToUniqueObject();

    assertArrayEquals(new Object[] {1, "2"}, row);
  }

  @Test
  void withoutBindVariables() {
    Optional<Integer> integer = this.jdbcTemplate
            .query("SELECT 1 FROM single_row_table")
            .withoutBindParameters()
            .mapTo(Integer.class)
            .collectToOptional();
    assertNotNull(integer);
    assertEquals(Optional.of(1), integer);
  }

  @Test
  void testToOptionalPresent() {
    Optional<Integer> integer = this.jdbcTemplate
            .query("SELECT 1 FROM single_row_table WHERE ? > 1")
            .binding(23)
            .mapTo(Integer.class)
            .collectToOptional();
    assertNotNull(integer);
    assertEquals(Optional.of(1), integer);
  }

  @Test
  void testToUniqueObjectPresent() {
    Integer integer = this.jdbcTemplate
            .query("SELECT 1 FROM single_row_table")
            .withoutBindParameters()
            .mapTo(Integer.class)
            .collectToUniqueObject();
    assertEquals(Integer.valueOf(1), integer);
  }

  @Test
  void testToUniqueObjectNotPresent() {
    assertThrows(UncheckedSQLException.class, () -> {
      this.jdbcTemplate
      .query("SELECT 1 FROM single_row_table WHERE 2 < 1")
      .withoutBindParameters()
      .mapTo(Integer.class)
      .collectToUniqueObject();
    });
  }

  @Test
  void testToOptionalNotPresent() {
    Optional<Integer> integer = this.jdbcTemplate
            .query("SELECT 1 FROM single_row_table WHERE ? > 1")
            .binding(0)
            .mapTo(Integer.class)
            .collectToOptional();
    assertNotNull(integer);
    assertEquals(Optional.empty(), integer);
  }

  @Test
  void testUpdateWithGeneratedKeys() {
    Integer generatedKey =
            this.jdbcTemplate
            .update("INSERT INTO test_table(test_value) VALUES (?)", Statement.RETURN_GENERATED_KEYS)
            .binding(23)
            .forGeneratedKey(Integer.class);
    assertThat(generatedKey).isGreaterThan(0);
  }

  @Test
  void testExpectUpdateCount() {
    this.jdbcTemplate
      .update("INSERT INTO test_table(id, test_value) VALUES (?, ?)")
      .binding(1000, 23)
      .expectUpdateCount(1);
  }

  @Test
  void testUpdateCount() {
    int updateCount = this.jdbcTemplate
            .update("INSERT INTO test_table(id) VALUES (?)")
            .binding(23)
            .forUpdateCount();
    assertEquals(1, updateCount);
  }

  @Test
  @Disabled("not implemented in H2")
  void testExpectLargeUpdateCount() {
    this.jdbcTemplate
    .update("INSERT INTO test_table(id) VALUES (?)")
    .binding(23)
    .expectLargeUpdateCount(1L);
  }

  @Test
  @Disabled("not implemented in H2")
  void testLargeUpdateCount() {
    long updateCount = this.jdbcTemplate
            .update("INSERT INTO test_table(id) VALUES (?)")
            .binding(23L)
            .forLargeUpdateCount();
    assertEquals(1L, updateCount);
  }

  @Test
  void testBatchUpdateFullBatch() {
    List<Object[]> batchArgs = Arrays.asList(new Object[] {11}, new Object[] {22});
    int updateCount = this.jdbcTemplate
            .batchUpdate("INSERT INTO test_table(test_value) VALUES (?)")
            .binding(batchArgs)
            .forTotalUpdateCount();
    assertEquals(2, updateCount);
  }

  @Test
  void testBatchUpdateNotFullBatch() {
    List<Object[]> batchArgs = Arrays.asList(new Object[] {11}, new Object[] {22}, new Object[] {33});
    int[][] updateCounts = this.jdbcTemplate
            .batchUpdate("INSERT INTO test_table(test_value) VALUES (?)")
            .binding(batchArgs, 2)
            .forPerBatchUpdateCount();
    assertArrayEquals(new int[][] {new int[] {1, 1}, new int[] {1}}, updateCounts);
  }

  static final class SingleConnectionDataSource implements DataSource {

    private final Connection connection;
    private final DataSource delegate;

    SingleConnectionDataSource(Connection connection, DataSource delegate) {
      this.delegate = delegate;
      this.connection = new CloseSuppressingConnection(connection);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
      return this.delegate.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
      this.delegate.setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
      this.delegate.setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
      return this.delegate.getLoginTimeout();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
      return this.delegate.getParentLogger();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
      return this.delegate.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
      return this.delegate.isWrapperFor(iface);
    }

    @Override
    public Connection getConnection() {
      return this.connection;
    }

    @Override
    public Connection getConnection(String username, String password) {
      return this.connection;
    }

  }

  static final class CloseSuppressingConnection implements Connection {

    private final Connection connection;

    CloseSuppressingConnection(Connection connection) {
      this.connection = connection;
    }

    @Override
    public void close() {
      // intentionally nothing
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
      return this.connection.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
      return this.connection.isWrapperFor(iface);
    }

    @Override
    public Statement createStatement() throws SQLException {
      return this.connection.createStatement();
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
      return this.connection.prepareStatement(sql);
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
      return this.connection.prepareCall(sql);
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
      return this.connection.nativeSQL(sql);
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
      this.connection.setAutoCommit(autoCommit);
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
      return this.connection.getAutoCommit();
    }

    @Override
    public void commit() throws SQLException {
      this.connection.commit();
    }

    @Override
    public void rollback() throws SQLException {
      this.connection.rollback();
    }

    @Override
    public boolean isClosed() throws SQLException {
      return this.connection.isClosed();
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
      return this.connection.getMetaData();
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
      this.connection.setReadOnly(readOnly);
    }

    @Override
    public boolean isReadOnly() throws SQLException {
      return this.connection.isReadOnly();
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
      this.connection.setCatalog(catalog);
    }

    @Override
    public String getCatalog() throws SQLException {
      return this.connection.getCatalog();
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
      this.connection.setTransactionIsolation(level);
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
      return this.connection.getTransactionIsolation();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
      return this.connection.getWarnings();
    }

    @Override
    public void clearWarnings() throws SQLException {
      this.connection.clearWarnings();
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
      return this.connection.createStatement(resultSetType, resultSetConcurrency);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
      return this.connection.prepareStatement(sql, resultSetType,
              resultSetConcurrency);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
      return this.connection.prepareCall(sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
      return this.connection.getTypeMap();
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
      this.connection.setTypeMap(map);
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
      this.connection.setHoldability(holdability);
    }

    @Override
    public int getHoldability() throws SQLException {
      return this.connection.getHoldability();
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
      return this.connection.setSavepoint();
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
      return this.connection.setSavepoint(name);
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
      this.connection.rollback(savepoint);
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
      this.connection.releaseSavepoint(savepoint);
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
      return this.connection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
      return this.connection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
      return this.connection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
      return this.connection.prepareStatement(sql, autoGeneratedKeys);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
      return this.connection.prepareStatement(sql, columnIndexes);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
      return this.connection.prepareStatement(sql, columnNames);
    }

    @Override
    public Clob createClob() throws SQLException {
      return this.connection.createClob();
    }

    @Override
    public Blob createBlob() throws SQLException {
      return this.connection.createBlob();
    }

    @Override
    public NClob createNClob() throws SQLException {
      return this.connection.createNClob();
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
      return this.connection.createSQLXML();
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
      return this.connection.isValid(timeout);
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
      this.connection.setClientInfo(name, value);
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
      this.connection.setClientInfo(properties);
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
      return this.connection.getClientInfo(name);
    }

    @Override
    public Properties getClientInfo() throws SQLException {
      return this.connection.getClientInfo();
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
      return this.connection.createArrayOf(typeName, elements);
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
      return this.connection.createStruct(typeName, attributes);
    }

    @Override
    public void setSchema(String schema) throws SQLException {
      this.connection.setSchema(schema);
    }

    @Override
    public String getSchema() throws SQLException {
      return this.connection.getSchema();
    }

    @Override
    public void abort(Executor executor) throws SQLException {
      this.connection.abort(executor);
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
      this.connection.setNetworkTimeout(executor, milliseconds);
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
      return this.connection.getNetworkTimeout();
    }

  }

}
