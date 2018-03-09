package com.github.marschall.jdbctemplateng;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.github.marschall.jdbctemplateng.api.NamedPreparedStatementSetterFactory;

@Disabled
class JdbcTemplateNgOracleTest {

  private JdbcTemplateNg jdbcTemplate;

  @BeforeEach
  void setUp() {
    this.jdbcTemplate = new JdbcTemplateNg(null, NamedPreparedStatementSetterFactory.oracle());
  }

  @Test
  void testBindingOneKeyValuePair() {
    Integer integer = this.jdbcTemplate
            .query("SELECT :x FROM dual")
            .binding("x", 1)
            .mapTo(Integer.class)
            .collectToUniqueObject();
    assertEquals(Integer.valueOf(1), integer);
  }

}
