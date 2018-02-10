package com.github.marschall.jdbctemplateng;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collector;

import org.junit.jupiter.api.Test;

class MoreCollectorsTest {

  @Test
  void toOptionalNotPresent() {
    Collector collector = MoreCollectors.toOptional();

    Object state = collector.supplier().get();

    Object result = collector.finisher().apply(state);

    assertTrue(result instanceof Optional);
    Optional<?> optional = (Optional<?>) result;
    assertFalse(optional.isPresent());
  }

  @Test
  void toOptionalPresent() {
    String row = "hello";
    Collector collector = MoreCollectors.toOptional();

    Object state = collector.supplier().get();

    BiConsumer accumulator = collector.accumulator();

    accumulator.accept(state, row);

    Object result = collector.finisher().apply(state);

    assertTrue(result instanceof Optional);
    Optional<?> optional = (Optional<?>) result;
    assertTrue(optional.isPresent());
    assertEquals(row, optional.get());
  }

  @Test
  void toOptionalAlreadyPresent() {
    Collector collector = MoreCollectors.toOptional();

    Object state = collector.supplier().get();

    BiConsumer accumulator = collector.accumulator();

    accumulator.accept(state, "first");
    assertThrows(IllegalStateException.class, () -> accumulator.accept(state, "second"));
  }

}
