package edu.gatech.cs6310.agroup.eventmodel;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.gatech.cs6310.agroup.model.EventLogType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by mlarson on 4/9/16. This is going to contain one or more {@link StudentDemandCourse}s.
 *
 */
public class StudentDemandEventContainer implements TopLevelSerializableEvent {

    Logger logger = LoggerFactory.getLogger(StudentDemandEventContainer.class);

    int studentId;

    public StudentDemandEventContainer(@JsonProperty("studentId") int studentId) {
        this.studentId = studentId;
    }

    //The individual items in this set are uniquely identified by their course ID
    Set<StudentDemandCourse> studentDemandCourseEvents = new HashSet<>();

    public void addStudentDemandCourse(StudentDemandCourse studentDemandCourseEvent) {
        boolean added = studentDemandCourseEvents.add(studentDemandCourseEvent);

        if (added)
            logger.debug("Added studentDemandCourseEvent for student id [{}]: [{}]", studentId, studentDemandCourseEvent);
        else
            logger.warn("Did NOT add studentDemandCourseEvent (already exists for student ID [{}]--this is probably a bug) [{}])", studentId, studentDemandCourseEvent);
    }

    //Note this breaks encapsulation and we should probably return a copy but for now just return this
    public Set<StudentDemandCourse> getStudentDemandCourseEvents() {
        return studentDemandCourseEvents;
    }

    public int getStudentId() {
        return studentId;
    }

    @Override
    public EventLogType.EVENT_LOG_TYPE getEventLogTypeEnum() {
        return EventLogType.EVENT_LOG_TYPE.STUDENT_DEMAND_ADDED;
    }

    @Override
    public StateContainer addSerializableEventToStateContainer(StateContainer stateContainer) {

        //Note that this will override whatever might have been there before for this student
        stateContainer.addStudentDemand(studentId, studentDemandCourseEvents);

        //OK in reality this is the same StateContainer that was passed in
        return stateContainer;
    }
}
