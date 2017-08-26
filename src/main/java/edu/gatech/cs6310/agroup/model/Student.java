package edu.gatech.cs6310.agroup.model;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Student Record object
 *
 * <pre>CS6310 Software Architecture and Design, Spring 2016
 * Project 1: Integer programming for student-course assignment</pre>
 *
 * @author <a href="mailto:tcesposito@gatech.edu">Timothy Esposito</a>
 */
@Entity
@Table(name = "Student")
public class Student implements Serializable {

    private static final long serialVersionUID = 20160210L;

    // user_ID,seniority,gpa
    // 1,4,3

    @Id
    private int id;

    private int seniority;
    private BigDecimal gpa; // should gpa be a float?

    public int getId() {
        return id;
    }

    public void setId(int userID) {
        this.id = userID;
    }

    public int getSeniority() {
        return seniority;
    }

    public void setSeniority(int seniority) {
        this.seniority = seniority;
    }

    public BigDecimal getGpa() {
        return gpa;
    }

    public void setGpa(BigDecimal gpa) {
        this.gpa = gpa;
    }

    @OneToMany(mappedBy="student")
    private List<StudentDemand> studentDemands;

    @OneToMany(mappedBy="student")
    private List<StudentScheduleResult> scheduleResults;

    public List<StudentDemand> getStudentDemands() {
        return studentDemands;
    }

    public void setStudentDemands(List<StudentDemand> studentDemands) {
        this.studentDemands = studentDemands;
    }

    public List<StudentScheduleResult> getScheduleResults() {
        return scheduleResults;
    }

    public void setScheduleResults(List<StudentScheduleResult> scheduleResults) {
        this.scheduleResults = scheduleResults;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Student student = (Student) o;

        return id == student.id;

    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return String.format("userID=%d, seniority=%d, gpa=%d", id, seniority, gpa);
    }
}
