package com.github.marschall.jdbctemplateng;

import javax.sql.DataSource;

import com.github.marschall.jdbctemplateng.api.PreparedStatementCreator;
import com.github.marschall.jdbctemplateng.api.PreparedStatementCustomizer;
import com.github.marschall.jdbctemplateng.api.PreparedStatementSetter;

public class QueryUnboundStatementProcessor {

  private final DataSource dataSource;

  private final PreparedStatementCreator creator;

  QueryUnboundStatementProcessor(DataSource dataSource, PreparedStatementCreator creator) {
    this.dataSource = dataSource;
    this.creator = creator;
  }

  public QueryBoundStatementProcessor binding(PreparedStatementSetter preparedStatementSetter) {
    return new QueryBoundStatementProcessor(dataSource, creator, preparedStatementSetter);
  }

  public QueryBoundStatementProcessor binding(Object... bindParameters) {
    return this.binding(preparedStatement -> {
      for (int i = 0; i < bindParameters.length; i++) {
        preparedStatement.setObject(i + 1, bindParameters[i]);
      }
    });
  }

  public QueryUnboundStatementProcessor customizeStatement(PreparedStatementCustomizer customizer) {
    PreparedStatementCreator decorated;
    if (this.creator instanceof DecoratedPreparedStatementCreator) {
      // TODO copy instead of modify?
      ((DecoratedPreparedStatementCreator) this.creator).addCustomizer(customizer);
      decorated = this.creator;
    } else {
      decorated = new DecoratedPreparedStatementCreator(this.creator, customizer);
    }
    return new QueryUnboundStatementProcessor(dataSource, decorated);
  }
}
