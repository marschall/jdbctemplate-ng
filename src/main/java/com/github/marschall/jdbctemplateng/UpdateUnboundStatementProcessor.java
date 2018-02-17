package com.github.marschall.jdbctemplateng;

import javax.sql.DataSource;

import com.github.marschall.jdbctemplateng.api.PreparedStatementCreator;
import com.github.marschall.jdbctemplateng.api.PreparedStatementCustomizer;
import com.github.marschall.jdbctemplateng.api.PreparedStatementSetter;

public final class UpdateUnboundStatementProcessor extends UnboundStatementProcessor {

  UpdateUnboundStatementProcessor(DataSource dataSource, PreparedStatementCreator creator) {
    super(dataSource, creator);
  }

  public QueryUnboundStatementProcessor customizeStatement(PreparedStatementCustomizer customizer) {
    return new QueryUnboundStatementProcessor(this.dataSource, this.decorateCreator(customizer));
  }

  public int binding(PreparedStatementSetter preparedStatementSetter) {
    UpdatePipeline pipeline = new UpdatePipeline(this.dataSource, this.creator, preparedStatementSetter);
    return pipeline.executeTranslated();
  }

  public int binding(Object... bindParameters) {
    return this.binding(preparedStatementSetter(bindParameters));
  }

}
