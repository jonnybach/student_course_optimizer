package edu.gatech.cs6310.agroup.model;

import edu.gatech.cs6310.agroup.util.SemesterTerm;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Semester object
 *
 * <pre>CS6310 Software Architecture and Design, Spring 2016
 * Project 1: Integer programming for student-course assignment</pre>
 *
 * @author <a href="mailto:tcesposito@gatech.edu">Timothy Esposito</a>
 */
@Entity
@Table(name = "Semester")
public class Semester implements Serializable {

    private static final long serialVersionUID = 20160210L;

    // semester_ID,semester_name,start_date,end_date
    // 1,Fall Year 1,8/1/15,12/1/15

    @Id
    private int id;

    private String name;
    private String startDate;
    private String endDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Semester semester = (Semester) o;

        return id == semester.id;

    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return "Semester{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                '}';
    }

    public SemesterTerm getTerm() {
        String name = this.getName();
        SemesterTerm trm = SemesterTerm.NONE;
        if (name.toLowerCase().contains("fall")) {
            trm = SemesterTerm.FALL;
        } else if (name.toLowerCase().contains("spring")) {
            trm = SemesterTerm.SPRING;
        } else if (name.toLowerCase().contains("summer")) {
            trm = SemesterTerm.SUMMER;
        }
        return trm;
    }

}
