package com.github.marschall.jdbctemplateng.api;

@FunctionalInterface
public interface GeneratedKeyCallback<R, K> {

  void generatedKey(R row, K key);

}
