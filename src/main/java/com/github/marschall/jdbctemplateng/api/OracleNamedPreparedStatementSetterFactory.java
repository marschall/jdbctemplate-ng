package com.github.marschall.jdbctemplateng.api;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;
import java.util.Map.Entry;

final class OracleNamedPreparedStatementSetterFactory implements NamedPreparedStatementSetterFactory {

  static final NamedPreparedStatementSetterFactory INSTANCE = new OracleNamedPreparedStatementSetterFactory();

  @Override
  public PreparedStatementSetter newNamedPreparedStatementSetter(Collection<Entry<String, Object>> parameters) {
    return new OracleNamedPreparedStatementSetter(parameters);
  }

  static final class OracleNamedPreparedStatementSetter implements PreparedStatementSetter {

    private static final Class<?> ORACLE_PREPARED_STATEMENT;
    private static final MethodHandle SET_OBJECT_AT_NAME;
    private static final MethodHandle SET_NULL_AT_NAME;

    static {
      // https://docs.oracle.com/en/database/oracle/oracle-database/12.2/jajdb/oracle/jdbc/OraclePreparedStatement.html
      Class<?> oraclePreparedStatement;
      MethodHandle setObjectAtName;
      MethodHandle setNullAtName;
      try {
        oraclePreparedStatement = Class.forName("oracle.jdbc.OraclePreparedStatement");

        Method setObjectAtNameMethod = oraclePreparedStatement.getDeclaredMethod(
                "setObjectAtName", String.class, Object.class);
        setObjectAtName = MethodHandles.publicLookup().unreflect(setObjectAtNameMethod);

        Method setNullAtNameMethod = oraclePreparedStatement.getDeclaredMethod(
                "setNullAtName", String.class, int.class);
        setNullAtName = MethodHandles.publicLookup().unreflect(setNullAtNameMethod);

      } catch (ReflectiveOperationException e) {
        oraclePreparedStatement = null;
        setObjectAtName = null;
        setNullAtName = null;
      }
      ORACLE_PREPARED_STATEMENT = oraclePreparedStatement;
      SET_OBJECT_AT_NAME = setObjectAtName;
      SET_NULL_AT_NAME = setNullAtName;
    }

    private final Collection<Entry<String, Object>> parameters;

    OracleNamedPreparedStatementSetter(Collection<Entry<String, Object>> parameters) {
      this.parameters = parameters;
    }

    @Override
    public void setValues(PreparedStatement preparedStatement) throws SQLException {
      if (ORACLE_PREPARED_STATEMENT == null || SET_OBJECT_AT_NAME == null || SET_NULL_AT_NAME == null) {
        throw new IllegalStateException("Oracle JDBC classes not found in expected shape");
      }
      Object oraclePreparedStatement = preparedStatement.unwrap(ORACLE_PREPARED_STATEMENT);
      for (Entry<String, Object> parameter : this.parameters) {
        String parameterName = parameter.getKey();
        Object parameterValue = parameter.getValue();
        try {
          if (parameterValue != null) {
            SET_OBJECT_AT_NAME.invoke(oraclePreparedStatement, parameterName, parameterValue);
          } else {
            SET_NULL_AT_NAME.invoke(oraclePreparedStatement, parameterName, Types.NULL);
          }
        } catch (SQLException e) {
          throw (SQLException) e;
        } catch (RuntimeException e) {
          throw (RuntimeException) e;
        } catch (Error e) {
          throw (Error) e;
        } catch (Throwable e) {
          // should not happen, does not fall into type signature
          throw new RuntimeException("unknwon exception occured when calling OraclePreparedStatement method", e);
        }
      }
    }

  }

}
