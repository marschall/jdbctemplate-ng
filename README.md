JdbcTemplateNg
==============

A new take on JdbcTemplate.

This project has the following goals:

- support Optional
- reduce the number of methods on JdbcOperations
- support named parameters

additional features:
- more control over return type by use of a collector
- statement customizer allows to set fetch size without having to create a prepared statement creator
- large update support

non-goals

features not supported:
- java.util.Date <-> java.sql.Timestamp and java.util.Calendar <-> java.sql.Timestamp type conversions, use JSR-310 datatypes
- work around JDBC driver bugs
- consider JDBC metadata
- jdbc type support
- rownum in RowMapper
- stored procedures

TODO
====
- batch updates generated keys
- connection customizer for readonly
- unprepared statement for statements without bind parameters

???
===
- should binding happen at the end so that operations can be cached? PreparedStatementSetter would have to take arguments
- naming: query().collectTo...
- naming: update().for...


