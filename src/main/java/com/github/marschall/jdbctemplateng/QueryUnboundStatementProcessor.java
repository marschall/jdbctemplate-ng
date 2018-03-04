package com.github.marschall.jdbctemplateng;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.assertj.core.util.Arrays;

import com.github.marschall.jdbctemplateng.api.NamedPreparedStatementSetterFactory;
import com.github.marschall.jdbctemplateng.api.PreparedStatementCreator;
import com.github.marschall.jdbctemplateng.api.PreparedStatementCustomizer;
import com.github.marschall.jdbctemplateng.api.PreparedStatementSetter;

public final class QueryUnboundStatementProcessor extends UnboundStatementProcessor {

  QueryUnboundStatementProcessor(DataSource dataSource, PreparedStatementCreator creator, NamedPreparedStatementSetterFactory namedFactory) {
    super(dataSource, creator, namedFactory);
  }

  public QueryBoundStatementProcessor binding(PreparedStatementSetter preparedStatementSetter) {
    Objects.requireNonNull(preparedStatementSetter, "preparedStatementSetter");
    return new QueryBoundStatementProcessor(this.dataSource, this.creator, preparedStatementSetter);
  }


  public QueryBoundStatementProcessor binding(Object... bindParameters) {
    Objects.requireNonNull(bindParameters, "bindParameters");
    return this.binding(this.preparedStatementSetter(bindParameters));
  }

  public QueryBoundStatementProcessor binding(String parameterName, Object parameterValue) {
    Objects.requireNonNull(parameterName, "parameterName");
    return binding(Collections.singletonList(new SimpleEntry(parameterName, parameterValue)));
  }

  public QueryBoundStatementProcessor binding(Map<String, Object> parameters) {
    Objects.requireNonNull(parameters, "parameters");
    return binding(parameters.entrySet());
  }

  public QueryBoundStatementProcessor binding(Entry<String, Object>... bindParameters) {
    Objects.requireNonNull(bindParameters, "bindParameters");
    return binding(Arrays.asList(bindParameters));
  }

  public QueryBoundStatementProcessor binding(Collection<Entry<String, Object>> bindParameters) {
    Objects.requireNonNull(bindParameters, "bindParameters");
    return new QueryBoundStatementProcessor(this.dataSource, this.creator, this.namedFactory.newNamedPreparedStatementSetter(bindParameters));
  }

  // REVIEW can we skip this
  public QueryBoundStatementProcessor withoutBindParameters() {
    return this.binding(statement -> {});
  }

  public QueryUnboundStatementProcessor fetchSize(int fetchSize) {
    if (fetchSize < 0) {
      throw new IllegalArgumentException("fetch size must not be negative");
    }
    return this.customizeStatement(statement -> statement.setFetchSize(fetchSize));
  }

  public QueryUnboundStatementProcessor customizeStatement(PreparedStatementCustomizer customizer) {
    Objects.requireNonNull(customizer, "customizer");
    return new QueryUnboundStatementProcessor(this.dataSource, this.decorateCreator(customizer), this.namedFactory);
  }

  static final class SimpleEntry implements Entry<String, Object> {

    private final String key;

    private final Object value;

    SimpleEntry(String key, Object value) {
      this.key = key;
      this.value = value;
    }

    @Override
    public String getKey() {
      return this.key;
    }

    @Override
    public Object getValue() {
      return this.value;
    }

    @Override
    public Object setValue(Object value) {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof Entry)) {
        return false;
      }
      Entry<?, ?> other = (Entry<?, ?>) obj;
      return this.key.equals(other.getKey())
              && Objects.equals(this.value, other.getValue());
    }

    @Override
    public int hashCode() {
      return this.key.hashCode() ^ Objects.hashCode(this.value);
    }

    @Override
    public String toString() {
      return this.key + '=' + this.value;
    }

  }

}
