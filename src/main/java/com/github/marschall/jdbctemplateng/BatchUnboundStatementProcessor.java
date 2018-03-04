package com.github.marschall.jdbctemplateng;

import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.Objects;

import javax.sql.DataSource;

import com.github.marschall.jdbctemplateng.api.NamedPreparedStatementSetterFactory;
import com.github.marschall.jdbctemplateng.api.ParameterizedPreparedStatementSetter;
import com.github.marschall.jdbctemplateng.api.PreparedStatementCreator;
import com.github.marschall.jdbctemplateng.api.PreparedStatementCustomizer;

public final class BatchUnboundStatementProcessor extends UnboundStatementProcessor {

  BatchUnboundStatementProcessor(DataSource dataSource, PreparedStatementCreator creator, NamedPreparedStatementSetterFactory namedFactory) {
    super(dataSource, creator, namedFactory);
  }

  public BatchUnboundStatementProcessor customizeStatement(PreparedStatementCustomizer customizer) {
    Objects.requireNonNull(customizer, "customizer");
    return new BatchUnboundStatementProcessor(this.dataSource, this.decorateCreator(customizer), this.namedFactory);
  }

  public BatchBoundStatementProcessor<Object[]> binding(Collection<Object[]> batchArgs) {
    return binding(batchArgs, batchArgs.size());
  }

  public BatchBoundStatementProcessor<Object[]> binding(Collection<Object[]> batchArgs, int batchSize) {
    return binding(batchArgs, batchSize, (PreparedStatement preparedStatement, Object[] values) -> {
      for (int i = 0; i < values.length; i++) {
        Object value = values[i];
        preparedStatement.setObject(i + 1, value);
      }
    });
  }

  public <T> BatchBoundStatementProcessor<T> binding(Collection<T> batchArgs, int batchSize, ParameterizedPreparedStatementSetter<T> setter) {
    Objects.requireNonNull(batchArgs, "batchArgs");
    if (batchSize <= 0) {
      throw new IllegalArgumentException("batch size must be positive");
    }
    Objects.requireNonNull(setter, "setter");
    return new BatchBoundStatementProcessor<>(this.dataSource, this.creator, batchArgs, batchSize, setter);
  }

}
