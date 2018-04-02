package com.github.marschall.jdbctemplateng;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;

import javax.sql.DataSource;

import com.github.marschall.jdbctemplateng.api.NamedPreparedStatementSetterFactory;
import com.github.marschall.jdbctemplateng.api.ParameterizedPreparedStatementSetter;
import com.github.marschall.jdbctemplateng.api.PreparedStatementCreator;
import com.github.marschall.jdbctemplateng.api.PreparedStatementCustomizer;
import com.github.marschall.jdbctemplateng.api.SQLExceptionAdapter;

public final class BatchUnboundStatementProcessor extends UnboundStatementProcessor {

  BatchUnboundStatementProcessor(DataSource dataSource, SQLExceptionAdapter exceptionAdapter,
          PreparedStatementCreator creator, NamedPreparedStatementSetterFactory namedFactory) {
    super(dataSource, exceptionAdapter, creator, namedFactory);
  }

  public BatchUnboundStatementProcessor customizeStatement(PreparedStatementCustomizer customizer) {
    Objects.requireNonNull(customizer, "customizer");
    return new BatchUnboundStatementProcessor(this.dataSource, this.exceptionAdapter, this.decorateCreator(customizer), this.namedFactory);
  }

  public BatchBoundStatementProcessor<Object[]> binding(List<Object[]> batchArgs) {
    return binding(batchArgs, batchArgs.size());
  }

  public BatchBoundStatementProcessor<Object[]> binding(List<Object[]> batchArgs, int batchSize) {
    return binding(batchArgs, batchSize, (PreparedStatement preparedStatement, Object[] values) -> {
      for (int i = 0; i < values.length; i++) {
        Object value = values[i];
        preparedStatement.setObject(i + 1, value);
      }
    });
  }

  public <T> BatchBoundStatementProcessor<T> binding(List<T> batchArgs, int batchSize, ParameterizedPreparedStatementSetter<T> setter) {
    Objects.requireNonNull(batchArgs, "batchArgs");
    if (batchSize <= 0) {
      throw new IllegalArgumentException("batch size must be positive");
    }
    Objects.requireNonNull(setter, "setter");
    return new BatchBoundStatementProcessor<>(this.dataSource, this.exceptionAdapter, this.creator, batchArgs, batchSize, setter);
  }

}
