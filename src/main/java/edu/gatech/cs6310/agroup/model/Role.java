package edu.gatech.cs6310.agroup.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Role object
 *
 * <pre>CS6310 Software Architecture and Design, Spring 2016
 * Project 1: Integer programming for student-course assignment</pre>
 *
 * @author <a href="mailto:tcesposito@gatech.edu">Timothy Esposito</a>
 */
@Entity
@Table(name = "roles")
public class Role implements Serializable {

    private static final long serialVersionUID = 20160210L;

    // role_ID,role_name,description
    // 1,TA,Teaching Assistant

    @Id
    private int ID;
    private String name;
    private String description;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return String.format("id=%d, name=%s, description=%s", ID, name, description);
    }
}
