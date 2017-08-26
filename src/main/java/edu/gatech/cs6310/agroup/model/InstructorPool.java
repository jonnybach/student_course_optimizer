package edu.gatech.cs6310.agroup.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Instructor pool object
 *
 * <pre>CS6310 Software Architecture and Design, Spring 2016
 * Project 1: Integer programming for student-course assignment</pre>
 *
 * @author <a href="mailto:tcesposito@gatech.edu">Timothy Esposito</a>
 */
@Entity
@Table(name = "instructor_pool")
public class InstructorPool implements Serializable {

    private static final long serialVersionUID = 20160210L;

    // faculty_ID,course_ID,role_ID,semester_ID
    // 11,16,1,3

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int ID;

    private int facultyID;
    private int courseID;
    private int roleID;
    private int semesterID;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getFacultyID() {
        return facultyID;
    }

    public void setFacultyID(int facultyID) {
        this.facultyID = facultyID;
    }

    public int getCourseID() {
        return courseID;
    }

    public void setCourseID(int courseID) {
        this.courseID = courseID;
    }

    public int getRoleID() {
        return roleID;
    }

    public void setRoleID(int roleID) {
        this.roleID = roleID;
    }

    public int getSemesterID() {
        return semesterID;
    }

    public void setSemesterID(int semesterID) {
        this.semesterID = semesterID;
    }

    @Override
    public String toString() {
        return String.format("facultyID=%d, courseID=%d, roleID=%d, semesterID=%d", facultyID, courseID, roleID, semesterID);
    }
}
