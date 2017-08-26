package edu.gatech.cs6310.agroup.repository;

import edu.gatech.cs6310.agroup.app.Project3Application;
import edu.gatech.cs6310.agroup.eventmodel.CourseEvent;
import edu.gatech.cs6310.agroup.eventmodel.CourseEventContainer;
import edu.gatech.cs6310.agroup.exception.EventSerializationException;
import edu.gatech.cs6310.agroup.model.*;
import edu.gatech.cs6310.agroup.service.EventLogInsertService;
import edu.gatech.cs6310.agroup.service.EventLogStateService;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by ubuntu on 4/7/16.
 */
@SuppressWarnings("SpringJavaAutowiringInspection")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Project3Application.class)
public class StudentScheduleResultRepositoryTest {
    private static final Logger logger = LoggerFactory.getLogger(StudentScheduleResultRepositoryTest.class);

    @Autowired
    private StudentScheduleResultRepository studentScheduleResultRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Ignore
    @Test
    @Transactional //Note this will allow for lazy requests, i.e. there will be a session here
    public void testInsert() {
        Course course = new Course();
        course.setId(1);

        EventLog eventLog = new EventLog();
        eventLog.setId(1);

        Student student = new Student();
        student.setId(1);

        StudentScheduleResult studentScheduleResult = new StudentScheduleResult();

        studentScheduleResult.setCourse(course);
        studentScheduleResult.setStudent(student);
        studentScheduleResult.setEventLog(eventLog);
        studentScheduleResult.setIsAssigned(true);

        long totalResults = studentScheduleResultRepository.count();
        studentScheduleResult = studentScheduleResultRepository.save(studentScheduleResult);
        assertTrue("Count should be one greater", studentScheduleResultRepository.count() == totalResults + 1);

        Student theStudent = studentRepository.getOne(1);
        assertTrue("Count of student schedule results should be one", theStudent.getScheduleResults().size() == 1);

        //Note could not get delete working oddly...?

    }
}
