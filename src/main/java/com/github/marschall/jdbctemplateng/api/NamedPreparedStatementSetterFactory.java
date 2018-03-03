package com.github.marschall.jdbctemplateng.api;

import java.util.Collection;
import java.util.Map.Entry;

@FunctionalInterface
public interface NamedPreparedStatementSetterFactory {

  PreparedStatementSetter newNamedPreparedStatementSetter(Collection<Entry<String, Object>> namedParameters);

  public static NamedPreparedStatementSetterFactory oracle() {
    return OracleNamedPreparedStatementSetterFactory.INSTANCE;
  }

  public static NamedPreparedStatementSetterFactory db2() {
    return DB2NamedPreparedStatementSetterFactory.INSTANCE;
  }

}
