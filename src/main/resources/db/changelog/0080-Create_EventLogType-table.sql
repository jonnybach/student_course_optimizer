--liquibase formatted sql

--changeset mlarson:0080-1
CREATE TABLE EventLogType
(
  id INT PRIMARY KEY,
  typeName VARCHAR(20) NOT NULL,
  description VARCHAR(500)
);

/* Insert some initial type data */
--changeset mlarson:0080-2
INSERT INTO EventLogType (id, typeName, description)
VALUES (1, 'COURSES_ADDED', 'Added a set of courses for a particular semester');

--changeset mlarson:0080-3
INSERT INTO EventLogType (id, typeName, description)
VALUES (2, 'STUDENT_DEMAND_ADDED', 'Added a set of student demanded courses for a particular semester');



