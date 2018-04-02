package com.github.marschall.jdbctemplateng;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import javax.sql.DataSource;

import com.github.marschall.jdbctemplateng.api.NamedPreparedStatementSetterFactory;
import com.github.marschall.jdbctemplateng.api.PreparedStatementCreator;
import com.github.marschall.jdbctemplateng.api.PreparedStatementCustomizer;
import com.github.marschall.jdbctemplateng.api.PreparedStatementSetter;
import com.github.marschall.jdbctemplateng.api.SQLExceptionAdapter;

public final class QueryUnboundStatementProcessor extends UnboundStatementProcessor {

  QueryUnboundStatementProcessor(DataSource dataSource, SQLExceptionAdapter exceptionAdapter,
          PreparedStatementCreator creator, NamedPreparedStatementSetterFactory namedFactory) {
    super(dataSource, exceptionAdapter, creator, namedFactory);
  }

  public QueryBoundStatementProcessor binding(PreparedStatementSetter preparedStatementSetter) {
    Objects.requireNonNull(preparedStatementSetter, "preparedStatementSetter");
    return new QueryBoundStatementProcessor(this.dataSource, this.exceptionAdapter, this.creator, preparedStatementSetter);
  }


  public QueryBoundStatementProcessor binding(Object... bindParameters) {
    Objects.requireNonNull(bindParameters, "bindParameters");
    return this.binding(this.preparedStatementSetter(bindParameters));
  }

  public QueryBoundStatementProcessor binding(String parameterName, Object parameterValue) {
    Objects.requireNonNull(parameterName, "parameterName");
    return binding(Collections.singletonList(new SimpleEntry(parameterName, parameterValue)));
  }

  public QueryBoundStatementProcessor binding(String parameterName1, Object parameterValue1,
                                              String parameterName2, Object parameterValue2) {
    Objects.requireNonNull(parameterName1, "parameterName1");
    Objects.requireNonNull(parameterName2, "parameterName2");
    return binding(Arrays.asList(new SimpleEntry(parameterName1, parameterValue1),
                                 new SimpleEntry(parameterName2, parameterValue2)));
  }

  public QueryBoundStatementProcessor binding(String parameterName1, Object parameterValue1,
                                              String parameterName2, Object parameterValue2,
                                              String parameterName3, Object parameterValue3) {
    Objects.requireNonNull(parameterName1, "parameterName1");
    Objects.requireNonNull(parameterName2, "parameterName2");
    Objects.requireNonNull(parameterName3, "parameterName3");
    return binding(Arrays.asList(new SimpleEntry(parameterName1, parameterValue1),
                                 new SimpleEntry(parameterName2, parameterValue2),
                                 new SimpleEntry(parameterName3, parameterValue3)));
  }

  public QueryBoundStatementProcessor binding(String parameterName1, Object parameterValue1,
                                              String parameterName2, Object parameterValue2,
                                              String parameterName3, Object parameterValue3,
                                              String parameterName4, Object parameterValue4) {
    Objects.requireNonNull(parameterName1, "parameterName1");
    Objects.requireNonNull(parameterName2, "parameterName2");
    Objects.requireNonNull(parameterName3, "parameterName3");
    Objects.requireNonNull(parameterName4, "parameterName4");
    return binding(Arrays.asList(new SimpleEntry(parameterName1, parameterValue1),
                                 new SimpleEntry(parameterName2, parameterValue2),
                                 new SimpleEntry(parameterName3, parameterValue3),
                                 new SimpleEntry(parameterName4, parameterValue4)));
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
    return new QueryBoundStatementProcessor(this.dataSource, this.exceptionAdapter, this.creator, this.namedFactory.newNamedPreparedStatementSetter(bindParameters));
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
    return new QueryUnboundStatementProcessor(this.dataSource, this.exceptionAdapter, this.decorateCreator(customizer), this.namedFactory);
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
