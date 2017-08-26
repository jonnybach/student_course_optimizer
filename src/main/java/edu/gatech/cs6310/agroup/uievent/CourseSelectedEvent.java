package edu.gatech.cs6310.agroup.uievent;

import org.springframework.context.ApplicationEvent;

/**
 * Created by jonathan on 4/18/16.
 */
public class CourseSelectedEvent extends ApplicationEvent {

    public enum SelectionAction {
        NONE,
        ADD,
        REMOVE
    }

    private Object course;
    private SelectionAction action;

    public CourseSelectedEvent(Object source, Object selectedCourse, SelectionAction selectedAction) {
        super(source);
        course = selectedCourse;
        action = selectedAction;
    }

    public Object getCourse() { return course; }

    public SelectionAction getAction() { return action; }

    public String toString(){
        return String.format("Course selected event");
    }

}
