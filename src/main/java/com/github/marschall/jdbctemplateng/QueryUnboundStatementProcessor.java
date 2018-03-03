package com.github.marschall.jdbctemplateng;

import java.util.Collection;
import java.util.Map.Entry;

import javax.sql.DataSource;

import com.github.marschall.jdbctemplateng.api.NamedPreparedStatementSetterFactory;
import com.github.marschall.jdbctemplateng.api.PreparedStatementCreator;
import com.github.marschall.jdbctemplateng.api.PreparedStatementCustomizer;
import com.github.marschall.jdbctemplateng.api.PreparedStatementSetter;

public final class QueryUnboundStatementProcessor extends UnboundStatementProcessor {

  QueryUnboundStatementProcessor(DataSource dataSource, PreparedStatementCreator creator, NamedPreparedStatementSetterFactory namedFactory) {
    super(dataSource, creator, namedFactory);
  }

  public QueryBoundStatementProcessor binding(PreparedStatementSetter preparedStatementSetter) {
    return new QueryBoundStatementProcessor(this.dataSource, this.creator, preparedStatementSetter);
  }

  public QueryBoundStatementProcessor binding(Collection<Entry<String, Object>> parameters) {
    return new QueryBoundStatementProcessor(this.dataSource, this.creator, this.namedFactory.newNamedPreparedStatementSetter(parameters));
  }

  public QueryBoundStatementProcessor binding(Object... bindParameters) {
    return this.binding(this.preparedStatementSetter(bindParameters));
  }

  // REVIEW can we skip this
  public QueryBoundStatementProcessor withoutBindParameters() {
    return this.binding(statement -> {});
  }

  public QueryUnboundStatementProcessor fetchSize(int fetchSize) {
    return this.customizeStatement(statement -> statement.setFetchSize(fetchSize));
  }

  public QueryUnboundStatementProcessor customizeStatement(PreparedStatementCustomizer customizer) {
    return new QueryUnboundStatementProcessor(this.dataSource, this.decorateCreator(customizer), this.namedFactory);
  }

}
