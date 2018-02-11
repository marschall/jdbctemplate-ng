package com.github.marschall.jdbctemplateng;

import static com.github.marschall.jdbctemplateng.MoreCollectors.toOptional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.sql.Statement;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JdbcTemplateNgTest {

  private JdbcDataSource dataSource;

  @BeforeEach
  void setUp() {
    this.dataSource = new JdbcDataSource();
    this.dataSource.setUrl("jdbc:h2:mem:");
  }

  @Test
  void testToList() {
    List<Integer> integers = new JdbcTemplateNg(this.dataSource)
      .query("SELECT 1 FROM dual WHERE ? > 1")
      .binding(23)
      .forObject(Integer.class)
      .toList();
    assertNotNull(integers);
    assertEquals(Collections.singletonList(1), integers);
  }

  @Test
  void testToOptionalPresent() {
    Optional<Integer> integer = new JdbcTemplateNg(this.dataSource)
            .query("SELECT 1 FROM dual WHERE ? > 1")
            .binding(23)
            .forObject(Integer.class)
            .collect(toOptional());
    assertNotNull(integer);
    assertEquals(Optional.of(1), integer);
  }

  @Test
  void testToOptionalNotPresent() {
    Optional<Integer> integer = new JdbcTemplateNg(this.dataSource)
            .query("SELECT 1 FROM dual WHERE ? > 1")
            .binding(0)
            .forObject(Integer.class)
            .collect(toOptional());
    assertNotNull(integer);
    assertEquals(Optional.empty(), integer);
  }

  @Test
  void testUpdate() {
      int updateCount = new JdbcTemplateNg(this.dataSource)
              .update("INSERT INTO T(X) VALUES (?)", Statement.RETURN_GENERATED_KEYS)
              .binding(23)
              .execute());
      assertEquals(1, updateCount);
  }

  @Test
  void testBatchUpdate() {
    List<Object[]> batchArgs = Collections.emptyList();
    int batchSize = 10;
    Optional<Integer> integers = new JdbcTemplateNg(this.dataSource)
            .batchUpdate("INSERT INTO T(X) VALUES (?)", Statement.RETURN_GENERATED_KEYS)
            .binding(batchArgs)
            .forObject(Integer.class)
            .collect(toOptional());
    assertNotNull(integers);
  }

}
