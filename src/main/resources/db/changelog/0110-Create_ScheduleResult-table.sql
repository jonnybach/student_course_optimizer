--liquibase formatted sql

--changeset mlarson:0110-1

CREATE TABLE StudentScheduleResult
(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    eventLogId BIGINT NOT NULL,
    studentId INT NOT NULL,
    courseId INT NOT NULL,
    isAssigned BOOLEAN NOT NULL
);

/* Create indices and FK pointers */

--changeset mlarson:0110-2
CREATE UNIQUE INDEX StudentScheduleResultUniqIdx ON StudentScheduleResult(eventLogId, studentId, courseId);

--changeset mlarson:0110-3
CREATE INDEX StudentScheduleResultStudentIdIdx ON StudentScheduleResult(studentId);

--changeset mlarson:0110-4
CREATE INDEX StudentScheduleResultCourseIdIdx ON StudentScheduleResult(courseId);

--changeset mlarson:0110-5
ALTER TABLE StudentScheduleResult ADD CONSTRAINT StudentScheduleResultFkStudentId FOREIGN KEY (studentId) REFERENCES Student(id);

--changeset mlarson:0110-6
ALTER TABLE StudentScheduleResult ADD CONSTRAINT StudentScheduleResultFkCourseId FOREIGN KEY (courseId) REFERENCES Course(id);

--changeset mlarson:0110-7
ALTER TABLE StudentScheduleResult ADD CONSTRAINT StudentScheduleResultFkEventLogId FOREIGN KEY (eventLogId) REFERENCES EventLog(id);


