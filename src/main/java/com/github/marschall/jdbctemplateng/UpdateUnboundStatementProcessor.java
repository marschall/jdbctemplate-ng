package com.github.marschall.jdbctemplateng;

import javax.sql.DataSource;

import com.github.marschall.jdbctemplateng.api.PreparedStatementCreator;

public class UpdateUnboundStatementProcessor {

  private final DataSource dataSource;

  private final PreparedStatementCreator creator;

  UpdateUnboundStatementProcessor(DataSource dataSource, PreparedStatementCreator creator) {
    this.dataSource = dataSource;
    this.creator = creator;
  }

}
