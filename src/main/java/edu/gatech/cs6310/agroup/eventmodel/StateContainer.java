package edu.gatech.cs6310.agroup.eventmodel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by matt.larson on 4/6/2016. This class provides the central data model for the data that is passed to Gurobi.
 *
 * The eventLogId is the log ID for which this state was calculated.
 *
 */
public class StateContainer {

    Logger logger = LoggerFactory.getLogger(StateContainer.class);

    private long eventLogId;

    public StateContainer(long eventLogId) {
        this.eventLogId = eventLogId;
    }

    //This is the set of courses and sizes for this semester
    Set<CourseEvent> courseEvents = new HashSet<>();

    //Map of student ID to course preferences
    Map<Integer, Set<StudentDemandCourse>> studentDemandEvents = new HashMap<>();

    /**
     * This method just overwrites the set of course events (i.e. the courses offered for this semester), so at the end you'll get the current state
     *
     * @param courseEvents
     */
    public void setCourses(Set<CourseEvent> courseEvents) {
        this.courseEvents = courseEvents;
    }

    public Set<CourseEvent> getCourses() {
        return courseEvents;
    }

    public void addStudentDemand(Integer studentId, Set<StudentDemandCourse> studentDemandCourses) {
        studentDemandEvents.put(studentId, studentDemandCourses);
    }

    public Map<Integer, Set<StudentDemandCourse>> getStudentDemand() {
        return studentDemandEvents;
    }
}
