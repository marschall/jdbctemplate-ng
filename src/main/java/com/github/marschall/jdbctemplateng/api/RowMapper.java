package com.github.marschall.jdbctemplateng.api;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@FunctionalInterface
public interface RowMapper<T> {

  // REVIEW rowNum?
  T mapRow(ResultSet resultSet) throws SQLException;

  public static RowMapper<Map<String, Object>> toMap() {
    return resultSet -> {
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

  public static RowMapper<Object[]> toArray() {
    return resultSet -> {
      ResultSetMetaData metaData = resultSet.getMetaData();
      int columnCount = metaData.getColumnCount();
      Object[] columnValues = new Object[columnCount];

      for (int i = 1; i <= columnCount; i++) {
        Object columnValue = resultSet.getObject(i);
        columnValues[i - 1] = columnValue;
      }
      return columnValues;
    };
  }

  public static RowMapper<List<Object>> toList() {
    return resultSet -> {
      ResultSetMetaData metaData = resultSet.getMetaData();
      int columnCount = metaData.getColumnCount();
      List<Object> columnValues = new ArrayList<>(columnCount);

      for (int i = 1; i <= columnCount; i++) {
        Object columnValue = resultSet.getObject(i);
        columnValues.add(columnValue);
      }
      return columnValues;
    };
  }

}
