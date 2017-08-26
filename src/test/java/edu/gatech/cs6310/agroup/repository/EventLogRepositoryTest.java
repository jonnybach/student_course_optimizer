package edu.gatech.cs6310.agroup.repository;

import edu.gatech.cs6310.agroup.app.Project3Application;
import edu.gatech.cs6310.agroup.eventmodel.CourseEvent;
import edu.gatech.cs6310.agroup.eventmodel.CourseEventContainer;
import edu.gatech.cs6310.agroup.exception.EventSerializationException;
import edu.gatech.cs6310.agroup.model.Course;
import edu.gatech.cs6310.agroup.model.EventLog;
import edu.gatech.cs6310.agroup.model.Semester;
import edu.gatech.cs6310.agroup.service.EventLogInsertService;
import edu.gatech.cs6310.agroup.service.EventLogStateService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * Created by ubuntu on 4/7/16.
 */
@SuppressWarnings("SpringJavaAutowiringInspection")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Project3Application.class)
public class EventLogRepositoryTest {
    private static final Logger logger = LoggerFactory.getLogger(EventLogRepositoryTest.class);

    @Autowired
    EventLogInsertService eventLogInsertService;

    @Autowired
    EventLogStateService eventLogStateService;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    SemesterRepository semesterRepository;

    @Autowired
    EventLogRepository eventLogRepository;

    Course course = new Course();
    Semester semester = new Semester();
    EventLog eventLog;

    @Before
    public void setup() throws EventSerializationException {

        course.setId(9999);
        course.setNumber("9999");
        course.setName("test course");
        course.setAvailability("asdf");
        courseRepository.save(course);

        semester.setId(9999);
        semester.setName("Fall Sem 9999");
        semesterRepository.save(semester);

        CourseEventContainer courseEventContainer = new CourseEventContainer();
        courseEventContainer.addCourseEvent(new CourseEvent(course.getId()));
        eventLog = eventLogInsertService.insertCourseEvents(courseEventContainer, semester, false);

    }

    @Test
    public void testEventLogList() throws EventSerializationException {

        List<EventLog> eventLogs = eventLogRepository.getEventLogsBySemesterUpToEventLogId(semester, eventLog.getId());

        eventLogs.stream().forEach(eventLog -> logger.debug("EVENTLOG: [{}]", eventLog));
    }

    @After
    public void cleanUp() {
        eventLogRepository.delete(eventLog);
        courseRepository.delete(course);
        semesterRepository.delete(semester);
    }
}
