
A new take on JdbcTemplate.

This project has the following goals

- support Optional
- reduce the number of methods on JdbcOperations
- support named parameters

non-goals

- support the java.util.Date <-> java.sql.Timestamp and java.util.Calendar <-> java.sql.Timestamp type conversions, use JSR-310 datatypes
- work around JDBC driver bugs

???
===
- should binding happen at the end so that operations can be cached? PreparedStatementSetter would have to take arguments

