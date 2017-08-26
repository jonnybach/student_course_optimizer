package edu.gatech.cs6310.agroup.service;

import edu.gatech.cs6310.agroup.app.Project3Application;
import edu.gatech.cs6310.agroup.eventmodel.StateContainer;
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
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * Created by tim on 4/17/16.
 */
@SuppressWarnings("SpringJavaAutowiringInspection")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Project3Application.class)
public class CourseSchedulerServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(CourseSchedulerServiceTest.class);

    @Autowired
    EventLogStateService eventLogStateService;

    @Autowired
    CourseSchedulerService courseSchedulerService;

    @Autowired
    SemesterRepository semesterRepo;

    @Autowired
    EventLogRepository eventLogRepo;

    @Autowired
    StudentRepository studentRepo;

    @Autowired
    EventLogInsertService eventLogInsertService;

    @Autowired
    CourseRepository courseRepo;

    @Ignore
    @Test
    public void testScheduler() throws EventSerializationException {
        // select a random semester
        List<Semester> semesters = semesterRepo.findAll();
        int random = (int) (Math.random() * semesters.size());
        Semester semester = semesters.get(random);
        logger.info("Choosing semester for testing Gurobi schedule => {}", semester.getName());

        // select the last event log ID for the selected semester
        //List<EventLog> eventLogs = eventLogRepo.getAllBySemester(semester);
        List<EventLog> eventLogs = eventLogRepo.getStudentDemandEventLogsBySemester(semester);
        EventLog eventLog = null;

        if (eventLogs.isEmpty()) {
            // generate some sample preferences for each student
            List<Course> courses = courseRepo.findAll();

            for (Student student : studentRepo.findAll()) {
                StudentDemandEventContainer studentDemandEventContainer = new StudentDemandEventContainer(student.getId());

                int courseCount = (int) (Math.random()*5 + 1);

                for (int c = 1; c <= courseCount; c++) {
                    int randomCourse = (int) (Math.random()*courses.size());
                    // TODO make sure the same course isn't randomly selected more than once
                    studentDemandEventContainer.addStudentDemandCourse(new StudentDemandCourse(courses.get(randomCourse).getId(), c));
                }

                eventLog = eventLogInsertService.insertStudentDemandEvent(studentDemandEventContainer, semester, true);
            }

        } else {
            eventLog = eventLogs.get(eventLogs.size()-1);
        }

        if (eventLog != null) {
            logger.info("Using event log ID for testing Gurobi schedule => {}", eventLog.getId());
            StateContainer state = eventLogStateService.calculateState(semester, eventLog.getId());
            courseSchedulerService.calculateSchedule(semester, eventLog, state);

        } else {
            logger.warn("There are no event logs to test Gurobi scheduler with");
        }
    }
}
