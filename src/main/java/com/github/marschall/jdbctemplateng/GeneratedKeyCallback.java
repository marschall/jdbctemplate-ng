package com.github.marschall.jdbctemplateng;

public interface GeneratedKeyCallback<R, K> {

  void generatedKey(R row, K key);

}
