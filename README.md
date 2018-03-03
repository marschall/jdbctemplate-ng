
A new take on JdbcTemplate.

This project has the following goals

- support Optional
- reduce the number of methods on JdbcOperations
- support named parameters

additional features
- more control over return type by use of a collector
- statemet customizer allows to set fetch size without having to create a prepared statement creator

non-goals

features not supported:
- java.util.Date <-> java.sql.Timestamp and java.util.Calendar <-> java.sql.Timestamp type conversions, use JSR-310 datatypes
- work around JDBC driver bugs
- get JDBC metadata
- support rownum in RowMapper

TODO
====
- batch updates
- connection customizer for readonly
- unprepared statement for statements without bind parameters

???
===
- should binding happen at the end so that operations can be cached? PreparedStatementSetter would have to take arguments

named
=====
- Oracle API
- IBM API
-- https://stackoverflow.com/a/7940495/1349691
-- https://www.ibm.com/support/knowledgecenter/SSEPGG_11.1.0/com.ibm.db2.luw.apdv.java.doc/src/tpc/imjcc_t0054762.html
-- https://www.ibm.com/support/knowledgecenter/SSEPGG_11.1.0/com.ibm.db2.luw.apdv.java.doc/src/tpc/imjcc_t0054764.html


