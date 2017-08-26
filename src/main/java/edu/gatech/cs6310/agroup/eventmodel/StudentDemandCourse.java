package edu.gatech.cs6310.agroup.eventmodel;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by matt.larson on 4/6/2016. This holds details for one particular course and will be wrapped up into a set of courses.
 */
public class StudentDemandCourse {
    int courseId;
    int priority;

    public StudentDemandCourse(@JsonProperty("courseId") int courseId, @JsonProperty("priority") int priority) {
        this.courseId = courseId;
        this.priority = priority;
    }


    public int getCourseId() {
        return courseId;
    }

    public int getPriority() {
        return priority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StudentDemandCourse that = (StudentDemandCourse) o;

        return courseId == that.courseId;

    }

    @Override
    public int hashCode() {
        return courseId;
    }

    @Override
    public String toString() {
        return "StudentDemandCourse{" +
                "courseId=" + courseId +
                ", priority=" + priority +
                '}';
    }
}
