/* Update the Course table to have only one id - we don't really need two */
ALTER TABLE Course DROP COLUMN courseId;

/* Also drop the auto increment, we will just set this for now */
ALTER TABLE CourseDependency DROP FOREIGN KEY CourseDependencyFkPreCourseId;
ALTER TABLE CourseDependency DROP FOREIGN KEY CourseDependencyFkDepCourseId;

ALTER TABLE Course CHANGE id id INT NOT NULL;

/* Add the constraints again */
ALTER TABLE CourseDependency ADD CONSTRAINT CourseDependencyFkPreCourseId FOREIGN KEY (prereq) REFERENCES Course(id) ON DELETE CASCADE;
ALTER TABLE CourseDependency ADD CONSTRAINT CourseDependencyFkDepCourseId FOREIGN KEY (dependency) REFERENCES Course(id) ON DELETE CASCADE;
