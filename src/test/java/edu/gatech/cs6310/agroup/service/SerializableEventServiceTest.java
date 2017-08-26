package edu.gatech.cs6310.agroup.service;

import edu.gatech.cs6310.agroup.app.Project3Application;
import edu.gatech.cs6310.agroup.eventmodel.*;
import edu.gatech.cs6310.agroup.exception.EventSerializationException;
import edu.gatech.cs6310.agroup.model.*;
import edu.gatech.cs6310.agroup.repository.CourseRepository;
import edu.gatech.cs6310.agroup.repository.EventLogRepository;
import edu.gatech.cs6310.agroup.repository.SemesterRepository;
import edu.gatech.cs6310.agroup.repository.StudentRepository;
import edu.gatech.cs6310.agroup.service.EventLogInsertService;
import edu.gatech.cs6310.agroup.service.SerializableEventService;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.security.cert.PKIXRevocationChecker;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


/**
 * Note that this doesn't work at the moment due to problem applying the DB access properties
 */
@SuppressWarnings("SpringJavaAutowiringInspection")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Project3Application.class)
public class SerializableEventServiceTest {

    @Autowired
    SerializableEventService serializableEventService;

    @Autowired
    EventLogStateService eventLogStateService;

    @Test
    public void testCourseRetrieval() throws EventSerializationException {
        CourseEvent courseEvent = new CourseEvent(1); //This test does assume the Course table has been set up
        Course course = eventLogStateService.getCourseForCourseEvent(courseEvent);

        assertTrue("Course is not null", course != null);
    }

    @Test
    public void testCourseEventContainerDeserialization() throws EventSerializationException {
        //Create the top level container for the course events
        CourseEventContainer courseEventContainer = new CourseEventContainer();
        courseEventContainer.addCourseEvent(new CourseEvent(9000)); //Add one with infinite maxSize
        courseEventContainer.addCourseEvent(new CourseEvent(9001, 150));

        //Serialize this
        String serializedCourseEventContainer = serializableEventService.serialize(courseEventContainer);

        //Get it back out
        CourseEventContainer courseEventContainer2 = serializableEventService.deserialize(serializedCourseEventContainer, CourseEventContainer.class);

        assertTrue("There should be two courses in the deserialized courseEvents", courseEventContainer2.getCourseEvents().size() == 2);

        Optional<CourseEvent> courseEventOptional = courseEventContainer.getCourseEvents().stream().filter(ce -> ce.getCourseId() == 9001).findFirst();

        assertTrue("There should be a course with 9001 in it", courseEventOptional.isPresent());
        assertTrue("The max size is 150", courseEventOptional.get().getMaxSize() == 150);
    }

    @Test
    public void testStudentDemandEventDeserialization() throws EventSerializationException {
        //Create the top level container for the student demand events
        StudentDemandEventContainer studentDemandEventContainer = new StudentDemandEventContainer(9000);

        studentDemandEventContainer.addStudentDemandCourse(new StudentDemandCourse(9000, 1));
        studentDemandEventContainer.addStudentDemandCourse(new StudentDemandCourse(9001, 3));
        studentDemandEventContainer.addStudentDemandCourse(new StudentDemandCourse(9002, 2));

        //Serialize this
        String serializedCourseEventContainer = serializableEventService.serialize(studentDemandEventContainer);

        //Get it back out
        StudentDemandEventContainer studentDemandEventContainer2 = serializableEventService.deserialize(serializedCourseEventContainer, StudentDemandEventContainer.class);

        assertTrue("There should be 3 courses in the deserialized courseEvents", studentDemandEventContainer2.getStudentDemandCourseEvents().size() == 3);

        Optional<StudentDemandCourse> studentDemandCourseOptional = studentDemandEventContainer2.getStudentDemandCourseEvents().stream().filter(sd -> sd.getCourseId() == 9001).findFirst();

        assertTrue("There should be a course with 9001 in it", studentDemandCourseOptional.isPresent());
        assertTrue("The max size is 150", studentDemandCourseOptional.get().getPriority() == 3);
    }
}
