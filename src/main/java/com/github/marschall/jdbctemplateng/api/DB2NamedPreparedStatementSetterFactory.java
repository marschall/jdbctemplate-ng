package com.github.marschall.jdbctemplateng.api;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map.Entry;

final class DB2NamedPreparedStatementSetterFactory implements NamedPreparedStatementSetterFactory {

  static final NamedPreparedStatementSetterFactory INSTANCE = new DB2NamedPreparedStatementSetterFactory();

  @Override
  public PreparedStatementSetter newNamedPreparedStatementSetter(Collection<Entry<String, Object>> namedParameters) {
    return new DB2NamedPreparedStatementSetter(namedParameters);
  }

  static final class DB2NamedPreparedStatementSetter implements PreparedStatementSetter {

    private static final Class<?> DB2_PREPARED_STATEMENT;
    private static final MethodHandle SET_JCC_OBJECT_AT_NAME;

    static {
      // https://www.ibm.com/support/knowledgecenter/SSEPGG_11.1.0/com.ibm.db2.luw.apdv.java.doc/src/tpc/imjcc_r0021833.html
      Class<?> db2PreparedStatement;
      MethodHandle setJccObjectAtName;
      try {
        db2PreparedStatement = Class.forName("com.ibm.db2.jcc.DB2PreparedStatement");

        Method setObjectAtNameMethod = db2PreparedStatement.getDeclaredMethod(
                "setJccObjectAtName", String.class, Object.class);
        setJccObjectAtName = MethodHandles.publicLookup().unreflect(setObjectAtNameMethod);

      } catch (ReflectiveOperationException e) {
        db2PreparedStatement = null;
        setJccObjectAtName = null;
      }
      DB2_PREPARED_STATEMENT = db2PreparedStatement;
      SET_JCC_OBJECT_AT_NAME = setJccObjectAtName;
    }

    private final Collection<Entry<String, Object>> parameters;

    DB2NamedPreparedStatementSetter(Collection<Entry<String, Object>> parameters) {
      this.parameters = parameters;
    }

    @Override
    public void setValues(PreparedStatement preparedStatement) throws SQLException {
      if (DB2_PREPARED_STATEMENT == null || SET_JCC_OBJECT_AT_NAME == null) {
        throw new IllegalStateException("DB2 JDBC classes not found in expected shape");
      }
      Object db2PreparedStatement = preparedStatement.unwrap(DB2_PREPARED_STATEMENT);
      for (Entry<String, Object> parameter : this.parameters) {
        String parameterName = parameter.getKey();
        Object parameterValue = parameter.getValue();
        try {
          SET_JCC_OBJECT_AT_NAME.invoke(db2PreparedStatement, parameterName, parameterValue);
        } catch (SQLException e) {
          throw (SQLException) e;
        } catch (RuntimeException e) {
          throw (RuntimeException) e;
        } catch (Error e) {
          throw (Error) e;
        } catch (Throwable e) {
          // should not happen, does not fall into type signature
          throw new RuntimeException("unknwon exception occured when calling DB2PreparedStatement method", e);
        }
      }
    }

  }

}
