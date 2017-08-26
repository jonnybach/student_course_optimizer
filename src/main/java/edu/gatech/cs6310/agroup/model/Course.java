package edu.gatech.cs6310.agroup.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Course object
 *
 * <pre>CS6310 Software Architecture and Design, Spring 2016
 * Project 1: Integer programming for student-course assignment</pre>
 *
 * @author <a href="mailto:tcesposito@gatech.edu">Timothy Esposito</a>
 */
@Entity
@Table(name = "Course")
public class Course implements Serializable {

    private static final long serialVersionUID = 20160210L;

    // course_ID,course_name,course_number,fall_term,spring_term,summer_term,availability
    // 1,Advanced Operating Systems,6210,1,0,0,Fall Only

    //Note that we have to set the Id ourselves (which will work because it's from the file)
    @Id
    private int id;

    private String name;
    private String number;
    private boolean fall;
    private boolean spring;
    private boolean summer;
    private String availability;

    public int getId() {
        return id;
    }

    public void setId(int ID) {
        this.id = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public boolean isFall() {
        return fall;
    }

    public void setFall(boolean fall) {
        this.fall = fall;
    }

    public boolean isSpring() {
        return spring;
    }

    public void setSpring(boolean spring) {
        this.spring = spring;
    }

    public boolean isSummer() {
        return summer;
    }

    public void setSummer(boolean summer) {
        this.summer = summer;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public boolean availableInSemester(int semesterID) {
        boolean available = false;

        // making an assumption that first semester index is fall, followed spring and summer
        int mod = semesterID % 3;

        if (mod == 1 && fall) {
            available = true;
        } else if (mod == 2 && spring) {
            available = true;
        } else if (mod == 0 && summer) {
            available = true;
        }

        return available;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Course course = (Course) o;

        return id == course.id;

    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return String.format("id=%d, name=%s, number=%s", id, name, number);
    }
}
