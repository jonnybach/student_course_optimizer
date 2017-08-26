Follow the instructions in README.md to create your database.

After that, here are additional notes on the development process:

##Liquibase database migration

In order to make a database change (i.e. a DDL change), create a script in the src/main/resources/db/changelog directory.
The script should start with a 4 digit number including leading zeros, in order to make
 sure that it runs in sequential/alphabetical order.  The name should indicate
 something else about what the script does.  You can include more than one change
 per file, but if so the changes should logically fit together. For example:

 1. 0010-course-table.sql
 2. 0020-course_dependencies-table.sql

 The plan is to use Liquibase here but with SQL rather than XML/YAML, which
 are also available for Liquibase.

 Liquibase will then automatically apply all these changes on app startup.

##Using Spring repositories
The easiest way to interact with JPA here is to use Spring repositories.  You can see these in the
*edu.gatech.cs6310.agroup.repository* package.  All you have to do is to create the interface according to the pattern in the
current code and Spring will add a large number of useful methods.  Custom methods and queries can easily be added as well.

Once you have created the repository interface for the JPA entity, then this repository interface can be injected
via Spring autowiring in any class where it is needed (see the class *edu.gatech.cs6310.agroup.ui.TestJpaView* for a very basic, logging-only
example).

##Logging
Logging is done via the Logback framework, but called through the SLF4J API (which is a generic API). In order to update the logging
in the application, edit the src/main/resources/logback-spring.xml file.

##Sample queries

### Insert a row into the Course table:

INSERT INTO Course (courseId, name, number, fall , spring , summer , availability)
VALUES (1,'Advanced Operating Systems',6210, true, false, false, 'Fall Only');


##How to fix
###```INFO 4/20/16 11:15 PM: liquibase: Waiting for changelog lock....```
##error when debugging
taken from [http://www.the-forristers.com/home/pages/bella-forrister-public/coding-reference/sql-server/liquibase---waiting-for-changelog-lock]()
