package com.github.marschall.jdbctemplateng;

import javax.sql.DataSource;

import com.github.marschall.jdbctemplateng.api.PreparedStatementCreator;
import com.github.marschall.jdbctemplateng.api.PreparedStatementSetter;
import com.github.marschall.jdbctemplateng.api.SQLExceptionAdapter;

abstract class BoundStatementProcessor {

  final DataSource dataSource;
  final SQLExceptionAdapter exceptionAdapter;
  final PreparedStatementCreator creator;
  final PreparedStatementSetter setter;

  BoundStatementProcessor(DataSource dataSource, SQLExceptionAdapter exceptionAdapter,
          PreparedStatementCreator creator, PreparedStatementSetter setter) {
    this.dataSource = dataSource;
    this.exceptionAdapter = exceptionAdapter;
    this.creator = creator;
    this.setter = setter;
  }

}