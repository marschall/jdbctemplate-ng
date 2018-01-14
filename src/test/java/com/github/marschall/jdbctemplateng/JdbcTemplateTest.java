package com.github.marschall.jdbctemplateng;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

public class JdbcTemplateTest {

  @Test
  public void simpleDemo() {
    List<Integer> integers = new JdbcTemplate(null)
      .query("SELECT 1 FROM dual WHERE ? > 1")
      .binding(23)
      .forObject(Integer.class)
      .toList();
    assertNotNull(integers);
  }

}
