package edu.gatech.cs6310.agroup.eventmodel;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by matt.larson on 4/6/2016. This captures details for a single course that will be added in a given semester
 * (these will be handled as a group).
 */
public class CourseEvent {

    int courseId;
    int maxSize = -1; //-1 will mean no size limit

    /**
     * Use this constructor for creating a course with no max size
     * @param courseId
     */
    public CourseEvent(int courseId) {
        this.courseId = courseId;
    }

    /**
     * Use this constructor when you want to create a course with a particular max size
     * @param courseId
     * @param maxSize
     */
    public CourseEvent(@JsonProperty("courseId") int courseId, @JsonProperty("maxSize") int maxSize) {
        this.courseId = courseId;
        this.maxSize = maxSize;
    }

    public int getCourseId() {
        return courseId;
    }

    public int getMaxSize() {
        return maxSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CourseEvent that = (CourseEvent) o;

        if (courseId != courseId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return courseId;
    }

    @Override
    public String toString() {
        return "CourseEvent{" +
                "courseId=" + courseId +
                ", maxSize=" + maxSize +
                '}';
    }
}
