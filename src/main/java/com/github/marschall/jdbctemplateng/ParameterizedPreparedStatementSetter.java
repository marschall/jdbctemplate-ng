package com.github.marschall.jdbctemplateng;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@FunctionalInterface
public interface ParameterizedPreparedStatementSetter<T> {

	void setValues(PreparedStatement preparedStatement, T argument) throws SQLException;

}