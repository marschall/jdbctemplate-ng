package com.github.marschall.jdbctemplateng;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.github.marschall.jdbctemplateng.api.PreparedStatementCreator;
import com.github.marschall.jdbctemplateng.api.PreparedStatementCustomizer;

final class DecoratedPreparedStatementCreator implements PreparedStatementCreator {

  private final PreparedStatementCreator creator;

  private final List<PreparedStatementCustomizer> customizers;

  DecoratedPreparedStatementCreator(PreparedStatementCreator creator, PreparedStatementCustomizer customizer) {
    Objects.requireNonNull(creator);
    Objects.requireNonNull(customizer);
    this.creator = creator;
    this.customizers = new ArrayList<>(1);
    this.customizers.add(customizer);
  }

  @Override
  public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
    PreparedStatement statement = this.creator.createPreparedStatement(connection);
    for (PreparedStatementCustomizer customizer : this.customizers) {
      customizer.customize(statement);
    }
    return statement;
  }

  void addCustomizer(PreparedStatementCustomizer customizer) {
    Objects.requireNonNull(customizer);
    this.customizers.add(customizer);
  }


}
