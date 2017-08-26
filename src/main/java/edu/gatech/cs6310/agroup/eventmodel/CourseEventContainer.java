package edu.gatech.cs6310.agroup.eventmodel;

import edu.gatech.cs6310.agroup.model.EventLogType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by mlarson on 4/9/16. This is going to contain one or more {@link StudentDemandCourse}s.
 *
 * This event is identified by the student ID
 */
public class CourseEventContainer implements TopLevelSerializableEvent {

    Logger logger = LoggerFactory.getLogger(CourseEventContainer.class);

    Set<CourseEvent> courseEvents = new HashSet<>();

    /**
     * Use this to add a course event, i.e. a new course that will exist in this semester
     * @param courseEvent
     */
    public void addCourseEvent(CourseEvent courseEvent) {

        boolean added = courseEvents.add(courseEvent);

        if (added)
            logger.debug("Added courseEvent [{}]", courseEvent);
        else
            logger.warn("Did NOT add courseEvent (already exists in the data structure--this is probably a bug) [{}])", courseEvent);
    }

    public Set<CourseEvent> getCourseEvents() {
        return courseEvents;
    }

    @Override
    public EventLogType.EVENT_LOG_TYPE getEventLogTypeEnum() {
        return EventLogType.EVENT_LOG_TYPE.COURSES_ADDED;
    }

    @Override
    public StateContainer addSerializableEventToStateContainer(StateContainer stateContainer) {
        //Note that this will override whatever might have been there before for this student
        stateContainer.setCourses(courseEvents);

        //OK in reality this is the same StateContainer that was passed in
        return stateContainer;
    }
}
