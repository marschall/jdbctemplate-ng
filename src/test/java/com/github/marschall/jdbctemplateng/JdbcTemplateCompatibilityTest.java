package com.github.marschall.jdbctemplateng;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.github.marschall.jdbctemplateng.JdbcTemplateCompatibilityTest.TestConfiguration;

@SpringJUnitConfig(TestConfiguration.class)
class JdbcTemplateCompatibilityTest {

  @Autowired
  private JdbcOperations jdbcOperations;

  @Test // SPR-16578
  void caseInsensitiveKeys() {
    Map<String, Object> map = this.jdbcOperations.queryForMap("SELECT 1 as \"X\", 2 as \"x\" from dual");

    assertThat(map).hasSize(1);

    assertEquals(Integer.valueOf(1), map.get("X"));
    assertEquals(Integer.valueOf(1), map.get("x"));
  }

  @Test
  void unnamedKeys() {
    Map<String, Object> map = this.jdbcOperations.queryForMap("SELECT 1 + ? from dual", 1);

    assertThat(map).hasSize(1);

    assertEquals(Integer.valueOf(2), map.get("1 + ?1"));
  }

  @Test
  void caseSensitiveKeys() {
    List<Map<String, Object>> queryResult = this.jdbcOperations.query("SELECT 1 as \"X\", 2 as \"x\" from dual", (ResultSet resultSet) -> {
      List<Map<String, Object>> rows = new ArrayList<>();
      while (resultSet.next()) {
        Map<String, Object> row = new HashMap<>(4);
        row.put("X", resultSet.getObject("X"));
        row.put("x", resultSet.getObject("x"));
        rows.add(row);
      }
      return rows;
    });

    assertThat(queryResult).hasSize(1);

    Map<String, Object> map = queryResult.get(0);

    assertThat(map).hasSize(2);

    assertEquals(Integer.valueOf(1), map.get("X"));
    assertEquals(Integer.valueOf(1), map.get("x"));
  }

  @Test
  void caseSensitiveIndexes() {
    List<Map<String, Object>> queryResult = this.jdbcOperations.query("SELECT 1 as \"X\", 2 as \"x\" from dual", (ResultSet resultSet) -> {
      List<Map<String, Object>> rows = new ArrayList<>();
      ResultSetMetaData metaData = resultSet.getMetaData();
      int columnCount = metaData.getColumnCount();
      while (resultSet.next()) {
        Map<String, Object> row = new HashMap<>(4);
        for (int i = 1; i <= columnCount; i++) {
          String key = JdbcUtils.lookupColumnName(metaData, i);
          Object value = resultSet.getObject(i);
          row.put(key, value);
        }
        rows.add(row);
      }
      return rows;
    });

    assertThat(queryResult).hasSize(1);

    Map<String, Object> map = queryResult.get(0);

    assertThat(map).hasSize(2);

    assertEquals(Integer.valueOf(1), map.get("X"));
    assertEquals(Integer.valueOf(2), map.get("x"));
  }

  @Configuration
  static class TestConfiguration {

    @Bean
    public JdbcOperations JdbcOperations() {
      return new JdbcTemplate(this.dataSource());
    }

    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .build();
    }

  }

}
