/*Create the StudentDemand table*/

CREATE TABLE StudentDemand
(
    id INT PRIMARY KEY AUTO_INCREMENT,
    studentId INT NOT NULL,
    courseId INT NOT NULL,
    semesterId INT NOT NULL
);

/* Create indices and FK pointers */
CREATE UNIQUE INDEX StudentDemandUniqIdx ON StudentDemand(studentId, courseId, semesterId);
CREATE INDEX StudentDemandCourseIdIdx ON StudentDemand(courseId);
CREATE INDEX StudentDemandSemesterIdIdx ON StudentDemand(semesterId);

ALTER TABLE StudentDemand ADD CONSTRAINT StudentDemandFkStudentId FOREIGN KEY (studentId) REFERENCES Student(id) ON DELETE CASCADE;
ALTER TABLE StudentDemand ADD CONSTRAINT StudentDemandFkCourseId FOREIGN KEY (courseId) REFERENCES Course(id) ON DELETE CASCADE;
ALTER TABLE StudentDemand ADD CONSTRAINT StudentDemandFkSemesterId FOREIGN KEY (semesterId) REFERENCES Semester(id) ON DELETE CASCADE;

