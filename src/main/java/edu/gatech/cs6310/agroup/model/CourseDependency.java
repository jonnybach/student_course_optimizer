package edu.gatech.cs6310.agroup.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Course dependency object
 *
 * <pre>CS6310 Software Architecture and Design, Spring 2016
 * Project 1: Integer programming for student-course assignment</pre>
 *
 * @author <a href="mailto:tcesposito@gatech.edu">Timothy Esposito</a>
 */
@Entity
@Table(name = "CourseDependency")
public class CourseDependency implements Serializable {

    private static final long serialVersionUID = 20160210L;

    // prereq_course_ID,dependent_course_ID
    // 4,16

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    int Id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prereq", nullable = false)
    private Course prereq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dependency", nullable = false)
    private Course dependency;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        this.Id = id;
    }

    public Course getPrereq() {
        return prereq;
    }

    public void setPrereq(Course prereq) {
        this.prereq = prereq;
    }

    public Course getDependency() {
        return dependency;
    }

    public void setDependency(Course dependent) {
        this.dependency = dependent;
    }
}
