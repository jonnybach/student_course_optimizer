DROP TABLE IF EXISTS course_dependencies;

CREATE TABLE CourseDependency
(
  id int NOT NULL PRIMARY KEY AUTO_INCREMENT,
  prereq int NOT NULL,
  dependency int NOT NULL
);

/* Create indices and FK pointers */
CREATE UNIQUE INDEX CourseDependencyPrereqIdx ON CourseDependency(prereq, dependency);
CREATE INDEX CourseDependencyDepIdx ON CourseDependency(dependency);

ALTER TABLE CourseDependency ADD CONSTRAINT CourseDependencyFkPreCourseId FOREIGN KEY (prereq) REFERENCES Course(id) ON DELETE CASCADE;
ALTER TABLE CourseDependency ADD CONSTRAINT CourseDependencyFkDepCourseId FOREIGN KEY (dependency) REFERENCES Course(id) ON DELETE CASCADE;
