package com.github.marschall.jdbctemplateng;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class QueryRowProcessor<T> {

  public <R, A> R collect(Collector<? super T, A, R> collector) {
    return null;
  }

  public List<T> toList() {
    // TODO should probably be optimized
    return this.collect(Collectors.toList());
  }

  public Optional<T> toOptional() {
    // TODO
    return Optional.empty();
  }

}
