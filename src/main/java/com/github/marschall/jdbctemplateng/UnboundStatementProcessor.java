package com.github.marschall.jdbctemplateng;

import javax.sql.DataSource;

import com.github.marschall.jdbctemplateng.api.PreparedStatementCreator;
import com.github.marschall.jdbctemplateng.api.PreparedStatementCustomizer;

public class UnboundStatementProcessor {

  final DataSource dataSource;
  final PreparedStatementCreator creator;

  UnboundStatementProcessor(DataSource dataSource, PreparedStatementCreator creator) {
    this.dataSource = dataSource;
    this.creator = creator;
  }


  PreparedStatementCreator decorateCreator(PreparedStatementCustomizer customizer) {
    if (this.creator instanceof DecoratedPreparedStatementCreator) {
      // TODO copy instead of modify?
      ((DecoratedPreparedStatementCreator) this.creator).addCustomizer(customizer);
      return this.creator;
    } else {
      return new DecoratedPreparedStatementCreator(this.creator, customizer);
    }
  }

}