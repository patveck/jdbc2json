# com.pascalvaneck.jdbc2json

[![BCH compliance](https://bettercodehub.com/edge/badge/patveck/jdbc2json?branch=master)](https://bettercodehub.com/)
[![Build Status](https://travis-ci.org/patveck/jdbc2json.svg?branch=master)](https://travis-ci.org/patveck/jdbc2json)

Database support

It should work with any SQL database for which a JDBC driver is available, as follows:
- Make sure the JDBC driver you want to use is on the classpath. Drivers for MariaDB / MySQL, PostgreSQL and SQLite
are provided in the Jdbc2Json jar.
- Specify the correct JDBC connection string with the --url parameter.
