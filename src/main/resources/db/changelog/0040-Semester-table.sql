/*Create the Semester table*/
CREATE TABLE Semester
(
    id INT PRIMARY KEY,
    name VARCHAR(25) not null,
    startDate VARCHAR(20),
    endDate VARCHAR(20)
);