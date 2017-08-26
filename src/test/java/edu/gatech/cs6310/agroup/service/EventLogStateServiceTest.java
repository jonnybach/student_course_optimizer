package edu.gatech.cs6310.agroup.service;

import edu.gatech.cs6310.agroup.app.Project3Application;
import edu.gatech.cs6310.agroup.eventmodel.*;
import edu.gatech.cs6310.agroup.exception.EventSerializationException;
import edu.gatech.cs6310.agroup.model.Course;
import edu.gatech.cs6310.agroup.model.EventLog;
import edu.gatech.cs6310.agroup.model.EventLogType;
import edu.gatech.cs6310.agroup.model.Semester;
import edu.gatech.cs6310.agroup.repository.CourseRepository;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;

/**
 * Created by ubuntu on 4/7/16.
 */
@SuppressWarnings("SpringJavaAutowiringInspection")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Project3Application.class)
public class EventLogStateServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(EventLogStateServiceTest.class);

    @Autowired
    EventLogStateService eventLogStateService;

    @Autowired
    SerializableEventService serializableEventService;

    @Autowired
    CourseRepository courseRepository;

    @Test
    public void testStateCalculationOfStudentDemand() throws EventSerializationException {

        //Create some student demand events
        StudentDemandEventContainer studentDemandEventContainer = new StudentDemandEventContainer(9000);
        studentDemandEventContainer.addStudentDemandCourse(new StudentDemandCourse(9000, 1));
        studentDemandEventContainer.addStudentDemandCourse(new StudentDemandCourse(9001, 2));

        StudentDemandEventContainer studentDemandEventContainer2 = new StudentDemandEventContainer(9500);
        studentDemandEventContainer2.addStudentDemandCourse(new StudentDemandCourse(9501, 3));
        studentDemandEventContainer2.addStudentDemandCourse(new StudentDemandCourse(9502, 2));
        studentDemandEventContainer2.addStudentDemandCourse(new StudentDemandCourse(9503, 1));
        studentDemandEventContainer2.addStudentDemandCourse(new StudentDemandCourse(9504, 4));
        studentDemandEventContainer2.addStudentDemandCourse(new StudentDemandCourse(9504, 4)); //Test adding a course twice

        //Now add a state update for the first student
        StudentDemandEventContainer studentDemandEventContainer3 = new StudentDemandEventContainer(9000);
        studentDemandEventContainer3.addStudentDemandCourse(new StudentDemandCourse(9000, 1));
        studentDemandEventContainer3.addStudentDemandCourse(new StudentDemandCourse(9001, 2));
        studentDemandEventContainer3.addStudentDemandCourse(new StudentDemandCourse(9002, 3));

        //Create the right EventLogType for the student demand
        EventLogType eventLogType = new EventLogType();
        eventLogType.setId(1);
        eventLogType.setTypeName(EventLogType.EVENT_LOG_TYPE.STUDENT_DEMAND_ADDED.name());

        //Create the EventLog(s)
        List<EventLog> eventLogs = new ArrayList<>();
        EventLog eventLog = new EventLog();
        eventLog.setId(1);
        eventLog.setEventLogType(eventLogType);
        eventLog.setEventData(serializableEventService.serialize(studentDemandEventContainer2));
        eventLogs.add(eventLog);

        EventLog eventLog2 = new EventLog();
        eventLog2.setId(2);
        eventLog2.setEventLogType(eventLogType);
        eventLog2.setEventData(serializableEventService.serialize(studentDemandEventContainer));
        eventLogs.add(eventLog2);

        EventLog eventLog3 = new EventLog();
        eventLog3.setId(3);
        eventLog3.setEventLogType(eventLogType);
        eventLog3.setEventData(serializableEventService.serialize(studentDemandEventContainer3));
        eventLogs.add(eventLog3);

        //Now just throw in a CourseEvent to make sure that works together as well
        EventLogType eventLogTypeCourse = new EventLogType();
        eventLogTypeCourse.setId(2);
        eventLogTypeCourse.setTypeName(EventLogType.EVENT_LOG_TYPE.COURSES_ADDED.name());

        CourseEventContainer courseEventContainer = new CourseEventContainer();
        courseEventContainer.addCourseEvent(new CourseEvent(9502));
        EventLog eventLogCourse = new EventLog();
        eventLogCourse.setId(4);
        eventLogCourse.setEventLogType(eventLogTypeCourse);
        eventLogCourse.setEventData(serializableEventService.serialize(courseEventContainer));
        eventLogs.add(eventLogCourse);

        //Pass those into the state calculation engine
        StateContainer stateContainer = eventLogStateService.calculateStateForEventLogs(eventLogs, 3);

        assertTrue("Total size of the student demand map should be 2 for students with id 9000 and 9500", stateContainer.getStudentDemand().size() == 2);

        //Verify that you only get back the state in the first StudentDemandEventContainer
        assertTrue("The total size of the student demand for student with ID 9000 should be 3", stateContainer.getStudentDemand().get(9000).size() == 3);
        assertTrue("The total size of the student demand for student with ID 9500 should be 4", stateContainer.getStudentDemand().get(9500).size() == 4);

        //Make sure this student's demand contains 9000,9001,9002
        Set<Integer> s9000Courses = stateContainer.getStudentDemand().get(9000)
                .stream()
                .map(studentDemandCourse -> studentDemandCourse.getCourseId())
                .filter(id -> id == 9000 || id == 9001 || id == 9002)
                .collect(Collectors.toSet());

        assertTrue(s9000Courses.size() == 3);
        assertTrue("Student courses should be 9000, 9001, 9002", s9000Courses.contains(9000) && s9000Courses.contains(9001) && s9000Courses.contains(9002));
        assertTrue("Should be one course available too", stateContainer.getCourses().size() == 1);

    }

    @Test
    public void testStateCalculationOfCourseAdditions() throws EventSerializationException {

        //Create some course add events
        CourseEventContainer courseEventContainer = new CourseEventContainer();
        courseEventContainer.addCourseEvent(new CourseEvent(9500));
        courseEventContainer.addCourseEvent(new CourseEvent(9600));
        courseEventContainer.addCourseEvent(new CourseEvent(9700));

        CourseEventContainer courseEventContainer2 = new CourseEventContainer();
        courseEventContainer2.addCourseEvent(new CourseEvent(9300));
        courseEventContainer2.addCourseEvent(new CourseEvent(9400));

        //Now create the list of EventLogs
        List<EventLog> eventLogs = new ArrayList<>();

        EventLogType eventLogTypeCourse = new EventLogType();
        eventLogTypeCourse.setId(2);
        eventLogTypeCourse.setTypeName(EventLogType.EVENT_LOG_TYPE.COURSES_ADDED.name());

        EventLog eventLogCourse = new EventLog();
        eventLogCourse.setId(1);
        eventLogCourse.setEventLogType(eventLogTypeCourse);
        eventLogCourse.setEventData(serializableEventService.serialize(courseEventContainer));
        eventLogs.add(eventLogCourse);

        EventLog eventLogCourse2 = new EventLog();
        eventLogCourse2.setId(2);
        eventLogCourse2.setEventLogType(eventLogTypeCourse);
        eventLogCourse2.setEventData(serializableEventService.serialize(courseEventContainer2));
        eventLogs.add(eventLogCourse2);

        //Pass those into the state calculation engine
        StateContainer stateContainer = eventLogStateService.calculateStateForEventLogs(eventLogs, 2);

        //Check that the total size is two courses
        assertTrue("Total course size now should be 2", stateContainer.getCourses().size() == 2);

        //Check that the expected courses are there
        Set<Integer> courses = stateContainer.getCourses()
                .stream()
                .map(course -> course.getCourseId())
                .filter(id -> id == 9300 || id == 9400)
                .collect(Collectors.toSet());

        assertTrue(courses.size() == 2);
        assertTrue("Available courses should be 9300 and 9400", courses.contains(9300) && courses.contains(9400));
    }

    @Test
    @Transactional
    public void testStateContainerMapping() throws EventSerializationException {
        Semester semester = new Semester();
        semester.setId(1);
        StateContainer stateContainer = eventLogStateService.calculateState(semester, 12);

        Map<Course, CourseEvent> courseEventMap = eventLogStateService.getCourseMapForCourseEvents(stateContainer);

        Course course = courseRepository.findOne(6);
        logger.debug("Course contains key: [{}]", courseEventMap.get(course));
        assertTrue("Course event map should contain course ID 6", courseEventMap.containsKey(course));
    }
}
