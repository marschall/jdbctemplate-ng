package com.github.marschall.jdbctemplateng;

import javax.sql.DataSource;

import com.github.marschall.jdbctemplateng.api.PreparedStatementCreator;
import com.github.marschall.jdbctemplateng.api.PreparedStatementSetter;

abstract class BoundStatementProcessor {

  final DataSource dataSource;
  final PreparedStatementCreator creator;
  final PreparedStatementSetter setter;

  BoundStatementProcessor(DataSource dataSource, PreparedStatementCreator creator, PreparedStatementSetter setter) {
    this.dataSource = dataSource;
    this.creator = creator;
    this.setter = setter;
  }

}