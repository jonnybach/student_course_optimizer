package edu.gatech.cs6310.agroup.service;

import edu.gatech.cs6310.agroup.eventmodel.CourseEvent;
import edu.gatech.cs6310.agroup.eventmodel.StateContainer;
import edu.gatech.cs6310.agroup.eventmodel.StudentDemandCourse;
import edu.gatech.cs6310.agroup.eventmodel.TopLevelSerializableEvent;
import edu.gatech.cs6310.agroup.exception.EventSerializationException;
import edu.gatech.cs6310.agroup.model.Course;
import edu.gatech.cs6310.agroup.model.EventLog;
import edu.gatech.cs6310.agroup.model.EventLogType;
import edu.gatech.cs6310.agroup.model.Semester;
import edu.gatech.cs6310.agroup.repository.CourseRepository;
import edu.gatech.cs6310.agroup.repository.EventLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by mlarson on 4/6/16.
 *
 * This class will be used to calculate the state for a given EventLog (really, a given EventLogId)
 */
@Service
public class EventLogStateService {

    Logger logger = LoggerFactory.getLogger(edu.gatech.cs6310.agroup.service.EventLogStateService.class);

    @Autowired
    private EventLogRepository eventLogRepository;

    @Autowired
    private SerializableEventService serializableEventService;

    @Autowired
    private CourseRepository courseRepository;

    public List<EventLog> getSemesterEventLogsWithLimit(Semester semester) {
        List<EventLog> eventLogs = eventLogRepository.getTopEventsLogsBySemesterWithResultsCalculated(semester);
      return eventLogs;
    }

    @Transactional
    public StateContainer calculateState(Semester semester, long eventLogId) throws EventSerializationException {
        //First get all the events for this semester up to and including this eventLog
        //logger.debug("Retrieving all event logs for semester [{}] and eventLog.id [{}]", semester.getId(), eventLogId);
        List<EventLog> eventLogs = eventLogRepository.getEventLogsBySemesterUpToEventLogId(semester, eventLogId);

        return calculateStateForEventLogs(eventLogs, eventLogId);
    }

    /**
     * Does the state calculation given a set of EventLog(s). Uses package private access for unit testing.
     *
     *
     * @param eventLogs
     * @param eventLogId
     * @return
     * @throws EventSerializationException
     */
    StateContainer calculateStateForEventLogs(List<EventLog> eventLogs, long eventLogId) throws EventSerializationException {

        logger.debug("Calculating state up to eventLog.id [{}]", eventLogId);

        StateContainer stateContainer = new StateContainer(eventLogId);

        //Then iterate over them and calculate the state
        for (EventLog eventLogLocal: eventLogs) {

            //Get the right class to pass into the deserializer
            String eventTypeName = eventLogLocal.getEventLogType().getTypeName();
            EventLogType.EVENT_LOG_TYPE eventLogType = EventLogType.EVENT_LOG_TYPE.valueOf(eventTypeName);

            //Get the deserialized object
            TopLevelSerializableEvent topLevelSerializableEvent = serializableEventService.deserialize(eventLogLocal.getEventData(), eventLogType.getTopLevelSerializableEventClass());

            //Do the correct calculation for the StateContainer, i.e. add the correct state for this event type
            stateContainer = topLevelSerializableEvent.addSerializableEventToStateContainer(stateContainer);
        }

        return stateContainer;
    }

    /**
     * Convenience method for translating a CourseEvent to a Course
     * @param courseEvent
     * @return
     */
    public Course getCourseForCourseEvent(CourseEvent courseEvent) {
        return courseRepository.getOne(courseEvent.getCourseId());
    }

    /**
     * Convenience method for translating the StudentDemandCourse into a fully populated Course object
     *
     * @param studentDemandCourse
     * @return
     */
    public Course getCourseForStudentDemandCourse(StudentDemandCourse studentDemandCourse) {
        return courseRepository.getOne(studentDemandCourse.getCourseId());
    }

    /**
     * Convenience method for translating a populated StateContainer into a Map with the fully populated Courses
     * @param stateContainer
     * @return Map of Course to CourseEvent (CourseEvent will contain the maxSize)
     */
    public Map<Course, CourseEvent> getCourseMapForCourseEvents(StateContainer stateContainer) {
        return stateContainer.getCourses().stream()
                .collect(Collectors.toMap(ce -> courseRepository.getOne(ce.getCourseId()), ce -> ce));
    }

    /**
     * Use this method to translate the student demand for a particular student into a Map of Course and StudentDemandCourse
     *
     * @param studentId - the student ID (basically the student who is logged in)
     * @param stateContainer
     * @return
     */
    public Map<Course, StudentDemandCourse> getCourseMapForStudentDemand(int studentId, StateContainer stateContainer) {
        return stateContainer.getStudentDemand().get(studentId).stream()
                .collect(Collectors.toMap(sd -> courseRepository.getOne(sd.getCourseId()), sd -> sd));
    }
}
