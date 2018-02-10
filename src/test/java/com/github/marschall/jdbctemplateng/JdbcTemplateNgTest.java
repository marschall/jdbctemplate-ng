package com.github.marschall.jdbctemplateng;

import static com.github.marschall.jdbctemplateng.MoreCollectors.toOptional;
import static org.junit.Assert.assertNotNull;

import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import org.junit.Test;

public class JdbcTemplateNgTest {

  @Test
  public void testToList() {
    List<Integer> integers = new JdbcTemplateNg(null)
      .query("SELECT 1 FROM dual WHERE ? > 1")
      .binding(23)
      .forObject(Integer.class)
      .toList();
    assertNotNull(integers);
  }

  @Test
  public void testToOptional() {
    Optional<Integer> integers = new JdbcTemplateNg(null)
            .query("SELECT 1 FROM dual WHERE ? > 1")
            .binding(23)
            .forObject(Integer.class)
            .collect(toOptional());
    assertNotNull(integers);
  }

  @Test
  public void testBatchUpdate() {
    Optional<Integer> integers = new JdbcTemplateNg(null)
            .batchUpdate("INSERT INTO T(X) VALUES (?)", Statement.RETURN_GENERATED_KEYS)
            .binding(23)
            .forObject(Integer.class)
            .collect(toOptional());
    assertNotNull(integers);
  }

}
