package com.github.marschall.jdbctemplateng.api;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

@FunctionalInterface
public interface RowMapper<T> {

  // REVIEW rowNum?
  T mapRow(ResultSet resultSet, int rowNum) throws SQLException;

  public static RowMapper<Map<String, Object>> toMap() {
    return (resultSet, rowNum) -> {
      // REVIEW case insensitive?
      Map<String, Object> columnValues = new LinkedHashMap<>();
      ResultSetMetaData metaData = resultSet.getMetaData();
      int columnCount = metaData.getColumnCount();
      for (int i = 1; i <= columnCount; i++) {
        String columnName = metaData.getColumnLabel(i);
        if (columnName == null || columnName.isEmpty()) {
          columnName = metaData.getColumnName(columnCount);
        }
        Object columnValue = resultSet.getObject(i);
        columnValues.put(columnName, columnValue);
      }
      return columnValues;
    };
  }

}
