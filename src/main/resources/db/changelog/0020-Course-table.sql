/*Create the Course table*/
CREATE TABLE Course
(
    id INT PRIMARY KEY AUTO_INCREMENT,
    courseId INT NOT NULL,
    name VARCHAR(50) NOT NULL,
    number VARCHAR(50) NOT NULL,
    fall BOOLEAN NOT NULL,
    spring BOOLEAN NOT NULL,
    summer BOOLEAN NOT NULL,
    availability VARCHAR(50) NOT NULL
);

/* Create business key index on the courseId--shouldn't be repeated */
CREATE UNIQUE INDEX CourseCourseIdUniqIdx on Course(courseId);