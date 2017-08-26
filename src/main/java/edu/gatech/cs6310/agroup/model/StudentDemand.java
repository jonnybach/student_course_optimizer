package edu.gatech.cs6310.agroup.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Student demand object
 *
 * <pre>CS6310 Software Architecture and Design, Spring 2016
 * Project 1: Integer programming for student-course assignment</pre>
 *
 * @author <a href="mailto:tcesposito@gatech.edu">Timothy Esposito</a>
 */
@Entity
@Table(name = "StudentDemand")
public class StudentDemand implements Serializable {

    private static final long serialVersionUID = 20160210L;

    // student_ID,course_ID,semester_ID
    // 1,16,1

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "studentId", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "courseId", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semesterId", nullable = false)
    private Semester semester;

    public int getId() { return id; }

    public void setId(int id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Semester getSemester() {
        return semester;
    }

    public void setSemester(Semester semester) {
        this.semester = semester;
    }
}
