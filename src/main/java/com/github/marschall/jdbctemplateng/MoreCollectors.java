package com.github.marschall.jdbctemplateng;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public final class MoreCollectors {

  // Like java.util.Collections#EMPTY_LIST
  @SuppressWarnings("rawtypes")
  private static final OptionalCollector OPTIONAL_COLLECTOR = new OptionalCollector<>();

  private MoreCollectors() {
    throw new AssertionError("not instantiable");
  }

  @SuppressWarnings("unchecked")
  public static <T> Collector<T, ?, Optional<T>> toOptional(){
    return OPTIONAL_COLLECTOR;
  }

  static final class OptionalCollector<T> implements Collector<T, OptionalState<T>, Optional<T>> {

    @Override
    public Supplier<OptionalState<T>> supplier() {
      return OptionalState::new;
    }

    @Override
    public BiConsumer<OptionalState<T>, T> accumulator() {
      return OptionalState::accumulate;
    }

    @Override
    public BinaryOperator<OptionalState<T>> combiner() {
      return OptionalState::combine;
    }

    @Override
    public Function<OptionalState<T>, Optional<T>> finisher() {
      return OptionalState::finish;
    }

    @Override
    public Set<Characteristics> characteristics() {
      return Collections.emptySet();
    }

  }

  static final class OptionalState<T> {

    private T value;

    void accumulate(T value) {
      // TODO SQL exception
      Objects.requireNonNull(value, "value");

      if (this.value != null) {
        // TODO SQL exception
        throw new IllegalStateException("more than one row returned");
      }
      this.value = value;
    }

    OptionalState<T> combine(OptionalState<T> other) {
      if (this.value == null) {
        return other;
      } else {
        if (other.value != null) {
          // TODO SQL exception
          throw new IllegalStateException("more than one row returned");
        }
        return this;
      }
    }

    Optional<T> finish() {
      return Optional.ofNullable(this.value);
    }

  }

}
