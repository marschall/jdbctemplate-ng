package com.github.marschall.jdbctemplateng;

import static org.junit.Assert.fail;

import java.util.Optional;
import java.util.stream.Collector;

import org.junit.Test;

public class MoreCollectorsTest {

  @Test
  public void to() {
    Collector<Object, ?, Optional<Object>> collector = MoreCollectors.toOptional();

    fail();
  }

}
