package com.github.marschall.jdbctemplateng;

public final class FailedUpdate<T> {

  private final int updateCount;

  private final T element;

  FailedUpdate(int updateCount, T element) {
    this.updateCount = updateCount;
    this.element = element;
  }

  public int getUpdateCount() {
    return  this.updateCount;
  }

  public T getElement() {
    return this.element;
  }

}
