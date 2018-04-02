package com.github.marschall.jdbctemplateng;

import com.github.marschall.jdbctemplateng.api.SqlProvider;

final class SqlExtractor {

  private SqlExtractor() {
    throw new AssertionError("not instantiable");
  }

  static String extractSql(Object o) {
    if (o instanceof SqlProvider) {
      return ((SqlProvider) o).getSql();
    }
    return null;
  }

}
