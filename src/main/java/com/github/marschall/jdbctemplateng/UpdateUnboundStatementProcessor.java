package com.github.marschall.jdbctemplateng;

import java.util.Objects;

import javax.sql.DataSource;

import com.github.marschall.jdbctemplateng.api.NamedPreparedStatementSetterFactory;
import com.github.marschall.jdbctemplateng.api.PreparedStatementCreator;
import com.github.marschall.jdbctemplateng.api.PreparedStatementCustomizer;
import com.github.marschall.jdbctemplateng.api.PreparedStatementSetter;

public final class UpdateUnboundStatementProcessor extends UnboundStatementProcessor {

  UpdateUnboundStatementProcessor(DataSource dataSource, PreparedStatementCreator creator, NamedPreparedStatementSetterFactory namedFactory) {
    super(dataSource, creator, namedFactory);
  }

  public QueryUnboundStatementProcessor customizeStatement(PreparedStatementCustomizer customizer) {
    Objects.requireNonNull(customizer, "customizer");
    return new QueryUnboundStatementProcessor(this.dataSource, this.decorateCreator(customizer), this.namedFactory);
  }

  public UpdateBoundStatementProcessor binding(PreparedStatementSetter preparedStatementSetter) {
    Objects.requireNonNull(preparedStatementSetter, "preparedStatementSetter");
    return new UpdateBoundStatementProcessor(this.dataSource, this.creator, preparedStatementSetter);
  }

  public UpdateBoundStatementProcessor binding(Object... bindParameters) {
    Objects.requireNonNull(bindParameters, "bindParameters");
    return this.binding(this.preparedStatementSetter(bindParameters));
  }

}
