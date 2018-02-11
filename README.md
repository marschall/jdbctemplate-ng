
A new take on JdbcTemplate.

This project has the following goals

- support Optional
- reduce the number of methods on JdbcOperations
- support named parameters

non-goals

features not supported:
- java.util.Date <-> java.sql.Timestamp and java.util.Calendar <-> java.sql.Timestamp type conversions, use JSR-310 datatypes
- work around JDBC driver bugs
- get JDBC metadata

TODO
====
- updates
- batch updates
- statement customizer, fetch size, other things

???
===
- should binding happen at the end so that operations can be cached? PreparedStatementSetter would have to take arguments

named
=====
- Oracle API
-- https://docs.oracle.com/en/database/oracle/oracle-database/12.2/jajdb/oracle/jdbc/OraclePreparedStatement.html
- IBM API
-- https://stackoverflow.com/a/7940495/1349691
-- https://www.ibm.com/support/knowledgecenter/SSEPGG_11.1.0/com.ibm.db2.luw.apdv.java.doc/src/tpc/imjcc_t0054762.html
-- https://www.ibm.com/support/knowledgecenter/SSEPGG_11.1.0/com.ibm.db2.luw.apdv.java.doc/src/tpc/imjcc_t0054764.html


query <-> update like JDBC


