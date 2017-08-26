package edu.gatech.cs6310.agroup.service;

import edu.gatech.cs6310.agroup.app.Project3Application;
import edu.gatech.cs6310.agroup.exception.EventSerializationException;
import edu.gatech.cs6310.agroup.model.Semester;
import edu.gatech.cs6310.agroup.repository.EventLogRepository;
import edu.gatech.cs6310.agroup.repository.SemesterRepository;
import edu.gatech.cs6310.agroup.repository.StudentRepository;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertTrue;


/**
 * Note that this doesn't work at the moment due to problem applying the DB access properties
 */
@SuppressWarnings("SpringJavaAutowiringInspection")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Project3Application.class)
public class StaticLoaderServiceTest {

    @Autowired
    StaticLoaderService staticLoaderService;

    @Autowired
    SemesterRepository semesterRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    EventLogRepository eventLogRepository;

    @Autowired
    EventLogStateService eventLogStateService;

    @Ignore
    @Test
    public void testSemesterLoading() {

        //Cleanup
        semesterRepository.findAll().stream().forEach(item -> semesterRepository.delete(item));

        //Make sure the current semester count is 0
        //ssertTrue("Count of semesters should be 0", semesterRepository.count() == 0);

        staticLoaderService.loadSemesters();

        assertTrue("Count of semesters should be 12 or greater", semesterRepository.count() == 12);

        //Cleanup
        semesterRepository.findAll().stream().forEach(item -> semesterRepository.delete(item));
    }

    @Ignore
    @Test
    public void testSmallStudentLoading() {

        //Cleanup
        studentRepository.findAll().stream().forEach(item -> studentRepository.delete(item));

        assertTrue("Count of students should be 0", studentRepository.count() == 0);
        staticLoaderService.loadStudents();

        assertTrue("Count of students should be 10", studentRepository.count() == 10);

        //Cleanup
        studentRepository.findAll().stream().forEach(item -> studentRepository.delete(item));

    }

    @Ignore
    @Test
    public void testSemesterPrepopulation() throws EventSerializationException {

        //Cleanup
        eventLogRepository.findAll(new PageRequest(0, Integer.MAX_VALUE)).getContent().stream().forEach(item -> eventLogRepository.delete(item));

        staticLoaderService.prePopulateEventLogCoursesIfEmpty();

        assertTrue("There should be 12 rows, one for each semester", eventLogRepository.count() == 12);

        Semester semester = new Semester();
        semester.setId(1);

        assertTrue("There should be 13 classes in the first semester",
                eventLogStateService.calculateState(semester, eventLogRepository.getMaxId()).getCourses().size() == 13);

    }
}
