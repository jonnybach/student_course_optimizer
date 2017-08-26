package edu.gatech.cs6310.agroup.model;

import javax.persistence.*;

/**
 * Created by mlarson on 4/16/16.  This table will be used to store schedule optimization results on a per-student basis
 */
@Entity
public class StudentScheduleResult {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "eventLogId", nullable = false)
    private EventLog eventLog;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "studentId", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "courseId", nullable = false)
    private Course course;

    //Variable to indicate whether the student is in the class or not
    private boolean isAssigned;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public EventLog getEventLog() {
        return eventLog;
    }

    public void setEventLog(EventLog eventLog) {
        this.eventLog = eventLog;
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

    public boolean isAssigned() {
        return isAssigned;
    }

    public void setIsAssigned(boolean assigned) {
        isAssigned = assigned;
    }
}
