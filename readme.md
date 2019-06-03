# Purpose
To learn Kotlin and Kotlin best practices by collaborating with other engineers on converting a Java SpringBoot application to Kotlin. All Java code will be retained in the repo as a reference point for learning but migrated Kotlin code will be used in the implementation.


# REST version of Spring PetClinic Sample Application (spring-framework-petclinic extend ) [![Build Status](https://travis-ci.org/spring-petclinic/spring-petclinic-rest.png?branch=master)](https://travis-ci.org/spring-petclinic/spring-petclinic-rest/)
[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2Fjbristow%2Fspring-petclinic-rest-kotlin.svg?type=shield)](https://app.fossa.io/projects/git%2Bgithub.com%2Fjbristow%2Fspring-petclinic-rest-kotlin?ref=badge_shield)

This backend version of the Spring Petclinic application only provides a REST API. **There is no UI**.
The [spring-petclinic-angular project](https://github.com/spring-petclinic/spring-petclinic-angular) is a Angular 5 front-end application witch consumes the REST API.

## Understanding the Spring Petclinic application with a few diagrams
<a href="https://speakerdeck.com/michaelisvy/spring-petclinic-sample-application">See the presentation here</a>

## Running petclinic locally
```
	git clone https://github.com/spring-petclinic/spring-petclinic-rest.git
	cd spring-petclinic-rest
	./mvnw spring-boot:run
```

You can then access petclinic here: http://localhost:9966/petclinic/

## Swagger REST API documentation presented here (after application start):
<a href="http://localhost:9966/petclinic/swagger-ui.html">http://localhost:9966/petclinic/swagger-ui.html</a>

## Screenshot of the Angular 5 client

<img width="1427" alt="spring-petclinic-angular2" src="https://cloud.githubusercontent.com/assets/838318/23263243/f4509c4a-f9dd-11e6-951b-69d0ef72d8bd.png">

## In case you find a bug/suggested improvement for Spring Petclinic
Our issue tracker is available here: https://github.com/spring-petclinic/spring-petclinic-rest/issues


## Database configuration

In its default configuration, Petclinic uses an in-memory database (HSQLDB) which
gets populated at startup with data.
A similar setups is provided for MySql and PostgreSQL in case a persistent database configuration is needed.
To run petclinic locally using persistent database, it is needed to change profile defined in application.properties file.

For MySQL database, it is needed to change param "hsqldb" to "mysql" in string
```
spring.profiles.active=hsqldb,spring-data-jpa
```
 defined in application.properties file.

Before do this, would be good to check properties defined in application-mysql.properties file.

```
spring.datasource.url = jdbc:mysql://localhost:3306/petclinic?useUnicode=true
spring.datasource.username=pc
spring.datasource.password=petclinic 
spring.datasource.driver-class-name=com.mysql.jdbc.Driver 
spring.jpa.database=MYSQL
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
spring.jpa.hibernate.ddl-auto=none
```      

You may also start a MySql database with docker:

```
docker run --name mysql-petclinic -e MYSQL_ROOT_PASSWORD=petclinic -e MYSQL_DATABASE=petclinic -p 3306:3306 mysql:5.7.8
```

For PostgeSQL database, it is needed to change param "hsqldb" to "postgresql" in string
```
spring.profiles.active=hsqldb,spring-data-jpa
```
 defined in application.properties file.

Before do this, would be good to check properties defined in application-postgresql.properties file.

```
spring.datasource.url=jdbc:postgresql://localhost:5432/petclinic
spring.datasource.username=postgres
spring.datasource.password=petclinic
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.database=POSTGRESQL
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=none
```
You may also start a Postgres database with docker:

```
docker run --name postgres-petclinic -e POSTGRES_PASSWORD=petclinic -e POSTGRES_DB=petclinic -p 5432:5432 -d postgres:9.6.0
```

## Working with Petclinic in Eclipse/STS

### prerequisites
The following items should be installed in your system:
* Maven 3 (http://www.sonatype.com/books/mvnref-book/reference/installation.html)
* git command line tool (https://help.github.com/articles/set-up-git)
* Eclipse with the m2e plugin (m2e is installed by default when using the STS (http://www.springsource.org/sts) distribution of Eclipse)

Note: when m2e is available, there is an m2 icon in Help -> About dialog.
If m2e is not there, just follow the install process here: http://eclipse.org/m2e/download/


### Steps:

1) In the command line
```
git clone https://github.com/spring-petclinic/spring-petclinic-rest.git
```
2) Inside Eclipse
```
File -> Import -> Maven -> Existing Maven project
```


# Contributing

The [issue tracker](https://github.com/spring-petclinic/spring-petclinic-rest/issues) is the preferred channel for bug reports, features requests and submitting pull requests.

For pull requests, editor preferences are available in the [editor config](https://github.com/spring-petclinic/spring-petclinic-rest/blob/master/.editorconfig) for easy use in common text editors. Read more and download plugins at <http://editorconfig.org>.


# Kotlin Formatting
```
$ mvn antrun:run@ktlint-format
```



## License
[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2Fjbristow%2Fspring-petclinic-rest-kotlin.svg?type=large)](https://app.fossa.io/projects/git%2Bgithub.com%2Fjbristow%2Fspring-petclinic-rest-kotlin?ref=badge_large)