package edu.gatech.cs6310.agroup.service;

import edu.gatech.cs6310.agroup.app.Project3Application;
import edu.gatech.cs6310.agroup.eventmodel.CourseEvent;
import edu.gatech.cs6310.agroup.eventmodel.CourseEventContainer;
import edu.gatech.cs6310.agroup.eventmodel.StudentDemandCourse;
import edu.gatech.cs6310.agroup.eventmodel.StudentDemandEventContainer;
import edu.gatech.cs6310.agroup.exception.EventSerializationException;
import edu.gatech.cs6310.agroup.model.Course;
import edu.gatech.cs6310.agroup.model.EventLog;
import edu.gatech.cs6310.agroup.model.Semester;
import edu.gatech.cs6310.agroup.model.Student;
import edu.gatech.cs6310.agroup.repository.CourseRepository;
import edu.gatech.cs6310.agroup.repository.EventLogRepository;
import edu.gatech.cs6310.agroup.repository.SemesterRepository;
import edu.gatech.cs6310.agroup.repository.StudentRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;

import static org.junit.Assert.assertTrue;

/**
 * Created by ubuntu on 4/7/16.
 */
@SuppressWarnings("ALL")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Project3Application.class)
public class EventLogInsertServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(edu.gatech.cs6310.agroup.service.EventLogInsertServiceTest.class);

    @Autowired
    EventLogInsertService eventLogInsertService;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    SemesterRepository semesterRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    EventLogRepository eventLogRepository;

    Course course = new Course();
    Semester semester = new Semester();
    Student student = new Student();
    Course course2 = new Course();

    @Before
    public void setup() {

        //Create courses
        course.setId(9000);
        course.setNumber("9000");
        course.setName("test course");
        course.setAvailability("asdf");
        courseRepository.save(course);

        course2.setId(9001);
        course2.setNumber("9000");
        course2.setName("Second test course");
        course2.setAvailability("asdf2");
        courseRepository.save(course2);

        //Create semester
        semester.setId(9000);
        semester.setName("Fall Sem 9000");
        semesterRepository.save(semester);

        //Create student
        student.setId(9000);
        student.setSeniority(99);
        student.setGpa(new BigDecimal(3.5));
        studentRepository.save(student);

    }

    @Ignore
    @Test
    public void testInsertCourseEvents() throws EventSerializationException {

        //Create the top level container for the course events
        CourseEventContainer courseEventContainer = new CourseEventContainer();
        courseEventContainer.addCourseEvent(new CourseEvent(course.getId())); //Add one with infinite maxSize
        courseEventContainer.addCourseEvent(new CourseEvent(course2.getId(), 150));

        long countBefore = eventLogRepository.count();
        EventLog eventLog = eventLogInsertService.insertCourseEvents(courseEventContainer, semester, false);
        long countAfter = eventLogRepository.count();
        logger.debug("Added EventLog: [{}]", eventLog);

        assertTrue("Event log count should increase by one", countAfter == countBefore + 1);

        eventLogRepository.delete(eventLog);
    }

    @Test
    public void testInsertStudentDemandEvent() throws EventSerializationException {
        //Create the student demand top level container object
        StudentDemandEventContainer studentDemandEventContainer = new StudentDemandEventContainer(student.getId());

        //Create the courses that this student wants with their associated priorities
        studentDemandEventContainer.addStudentDemandCourse(new StudentDemandCourse(course.getId(), 1));
        studentDemandEventContainer.addStudentDemandCourse(new StudentDemandCourse(course2.getId(), 2));

        //Insert the event into the event log and test
        long countBefore = eventLogRepository.count();
        EventLog eventLog = eventLogInsertService.insertStudentDemandEvent(studentDemandEventContainer, semester, true);
        long countAfter = eventLogRepository.count();
        logger.debug("Added EventLog: [{}]", eventLog);

        assertTrue("Event log count should increase by one", countAfter == countBefore + 1);

        //Maybe confirm that isShadowMode is true

        //Maybe check that the object serialization is as expected

        //Clean up the eventLog here
        eventLogRepository.delete(eventLog);
    }

    @After
    public void cleanUp() {
        courseRepository.delete(course);
        courseRepository.delete(course2);
        semesterRepository.delete(semester);
        studentRepository.delete(student);
    }
}
