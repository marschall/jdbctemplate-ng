package com.github.marschall.jdbctemplateng;

import static com.github.marschall.jdbctemplateng.MoreCollectors.toOptional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.marschall.jdbctemplateng.api.RowMapper;

class JdbcTemplateNgTest {

  private DataSource dataSource;
  private Connection connection;
  private JdbcTemplateNg jdbcTemplate;

  @BeforeEach
  void setUp() throws SQLException {
    JdbcDataSource h2dataSource = new JdbcDataSource();
    h2dataSource.setUrl("jdbc:h2:mem:");

    this.connection = h2dataSource.getConnection();
    this.dataSource = new SingleConnectionDataSource(this.connection, h2dataSource);
    this.jdbcTemplate = new JdbcTemplateNg(this.dataSource);
  }

  @AfterEach
  void tearDown() throws SQLException {
    this.connection.close();
  }

  @Test
  void testToList() {
    List<Integer> integers = this.jdbcTemplate
      .query("SELECT 1 FROM dual WHERE ? > 1")
      .binding(23)
      .forObject(Integer.class)
      .toList();
    assertNotNull(integers);
    assertEquals(Collections.singletonList(1), integers);
  }

  @Test
  void customizeStatement() {
    List<Integer> integers = this.jdbcTemplate
            .query("SELECT 1 FROM dual")
            .fetchSize(1)
            .withoutBindParameters()
            .forObject(Integer.class)
            .toList();
    assertNotNull(integers);
    assertEquals(Collections.singletonList(1), integers);
  }

  @Test
  void queryForMap() {
    List<Map<String, Object>> values = this.jdbcTemplate
            .query("SELECT 1, '2' as TWO FROM dual")
            .withoutBindParameters()
            .mapping(RowMapper.toMap())
            .toList();

    assertNotNull(values);
    assertThat(values).hasSize(1);

    Map<String, Object> row = values.get(0);

    Set<String> expected = new HashSet<>(4);
    expected.add("1");
    expected.add("TWO");
    assertEquals(expected, row.keySet());

    assertEquals(Integer.valueOf(1), row.get("1"));
    assertEquals("2", row.get("TWO"));
  }

  @Test
  void withoutBindVariables() {
    // TODO statement instead?
    Optional<Integer> integer = this.jdbcTemplate
            .query("SELECT 1 FROM dual")
            .withoutBindParameters()
            .forObject(Integer.class)
            .toOptional();
    assertNotNull(integer);
    assertEquals(Optional.of(1), integer);
  }

  @Test
  void testToOptionalPresent() {
    Optional<Integer> integer = this.jdbcTemplate
            .query("SELECT 1 FROM dual WHERE ? > 1")
            .binding(23)
            .forObject(Integer.class)
            .toOptional();
    assertNotNull(integer);
    assertEquals(Optional.of(1), integer);
  }

  @Test
  void testToOptionalNotPresent() {
    Optional<Integer> integer = this.jdbcTemplate
            .query("SELECT 1 FROM dual WHERE ? > 1")
            .binding(0)
            .forObject(Integer.class)
            .toOptional();
    assertNotNull(integer);
    assertEquals(Optional.empty(), integer);
  }

  @Test
  void testUpdateWithGeneratedKeys() throws SQLException {
    try (Connection connection = this.dataSource.getConnection();
         Statement statement = connection.createStatement()) {
      statement.execute("CREATE TABLE test_table ("
              + "id IDENTITY PRIMARY KEY,"
              + "test_value INTEGER,"
              + ")");
      Long generatedKey =
              this.jdbcTemplate
              .update("INSERT INTO test_table(test_value) VALUES (?)", Statement.RETURN_GENERATED_KEYS)
              .binding(23)
              .forGeneratedKey(Long.class);
      assertEquals(Long.valueOf(1L), generatedKey);
    }
  }

  @Test
  void testUpdate() throws SQLException {
    try (Connection connection = this.dataSource.getConnection();
         Statement statement = connection.createStatement()) {
         statement.execute("CREATE TABLE test_table ("
                 + "id INTEGER PRIMARY KEY"
                 + ")");
         this.jdbcTemplate
                 .update("INSERT INTO test_table(id) VALUES (?)")
                 .binding(23)
                 .expectUpdateCount(1);
       }
  }

  @Test
  void testBatchUpdate() {
    List<Object[]> batchArgs = Collections.emptyList();
    int batchSize = 10;
    Optional<Integer> integers = this.jdbcTemplate
            .batchUpdate("INSERT INTO T(X) VALUES (?)", Statement.RETURN_GENERATED_KEYS)
            .binding(batchArgs)
            .forObject(Integer.class)
            .collect(toOptional());
    assertNotNull(integers);
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
