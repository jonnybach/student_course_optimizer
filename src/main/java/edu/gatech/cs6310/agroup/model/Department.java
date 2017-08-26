package edu.gatech.cs6310.agroup.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Department object
 *
 * <pre>CS6310 Software Architecture and Design, Spring 2016
 * Project 1: Integer programming for student-course assignment</pre>
 *
 * @author <a href="mailto:tcesposito@gatech.edu">Timothy Esposito</a>
 */
@Entity
@Table(name = "departments")
public class Department implements Serializable {

    private static final long serialVersionUID = 20160210L;

    // dept_ID,dept_code,dept_name
    // 1,HIS,History & Sociology

    @Id
    private int ID;
    private String code;
    private String name;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getCode() { return code; }

    public void setCode(String code) { this.code = code; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format("id=%d, code=%s, name=%s", ID, code, name);
    }
}
