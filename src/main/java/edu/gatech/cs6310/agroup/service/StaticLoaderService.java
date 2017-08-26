package edu.gatech.cs6310.agroup.service;

import edu.gatech.cs6310.agroup.data.*;
import edu.gatech.cs6310.agroup.eventmodel.CourseEvent;
import edu.gatech.cs6310.agroup.eventmodel.CourseEventContainer;
import edu.gatech.cs6310.agroup.exception.EventSerializationException;
import edu.gatech.cs6310.agroup.model.Course;
import edu.gatech.cs6310.agroup.model.Semester;
import edu.gatech.cs6310.agroup.model.Student;
import edu.gatech.cs6310.agroup.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by ubuntu on 4/11/16. Use this class to load the various static resources.
 */
@Service
public class StaticLoaderService {

    Logger logger = LoggerFactory.getLogger(StaticLoaderService.class);

    @Autowired
    private SemesterRepository semesterRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StudentDemandRepository studentDemandRepository;

    @Autowired
    private CourseDependencyRepository courseDependencyRepository;

    @Autowired
    private EventLogInsertService eventLogInsertService;

    @Autowired
    private EventLogRepository eventLogRepository;

    @Value("${static.file.dir}")
    String staticFileDirName;

    @Value("${default.class.cap.size}")
    int defaultClassCapSize;

    /**
     * Load static semester resources (same for all situations)
     */
    @Transactional
    public void loadSemesters() {

        new Semesters().getSemesters().forEach((k, v) -> semesterRepository.save(v));
    }

    /**
     * Load static course resources (same for all situations)
     */
    @Transactional
    public void loadCourses() {

        new Courses().getCourses().forEach((k, v) -> courseRepository.save(v));
    }

    public void loadStudents() {

        new StudentRecords("/" + staticFileDirName + "/student_records.csv").getStudentRecords().forEach((k, v) -> studentRepository.save(v));
    }

    public void loadCourseDependencies() {
        //Get the course map that we need
        Map<Integer, Course> courses = courseRepository.findAll().stream().collect(Collectors.toMap(Course::getId, c -> c));

        new CourseDependencies(courses).getCourseDependencies().forEach(cd -> courseDependencyRepository.save(cd));
    }

    /**
     * This has to be loaded last
     */
    public void loadStudentDemand() {

        Map<Integer, Student> students = studentRepository.findAll().stream().collect(Collectors.toMap(Student::getId, s -> s));
        Map<Integer, Course> courses = courseRepository.findAll().stream().collect(Collectors.toMap(Course::getId, c -> c));
        Map<Integer, Semester> semesters = semesterRepository.findAll().stream().collect(Collectors.toMap(Semester::getId, s -> s));

        new StudentDemands("/" + staticFileDirName + "/student_demand.csv", students, courses, semesters).getDemands().forEach(v -> studentDemandRepository.save(v));
    }

    /**
     * Loads everything in the right order, if the tables are empty
     */
    public void loadResourcesIfEmpty() {

        if (semesterRepository.count() == 0) {
            logger.debug("Loading semesters...");
            loadSemesters();
        }

        if (courseRepository.count() == 0) {
            logger.debug("Loading courses...");
            loadCourses();
        }

        if (courseDependencyRepository.count() == 0) {
            logger.debug("Loading course dependencies...");
            loadCourseDependencies();
        }

        if (studentRepository.count() == 0) {
            logger.debug("Loading students...");
            loadStudents();
        }

        if (studentDemandRepository.count() == 0) {
            logger.debug("Loading student demand...");
            loadStudentDemand();
        }
    }

    /**
     * This method is designed to prepopulate the event log based on the data in the static tables.
     * It would normally be called after the static resources are loaded.  NOTE THAT THIS IS NOT USED
     * BECAUSE THE student demand file does not include priorities
     */
    public void prePopulateEventLogCoursesIfEmpty() throws EventSerializationException {

        long eventLogTableSize = eventLogRepository.count();

        if (eventLogTableSize == 0) {
            logger.debug("Pre-populating EventLog with courses");

            //First put courses offered in the particular various semesters
            for (Semester semester : semesterRepository.findAll()) {

                //Determine which courses are offered in this semester - first figure out whether it is fall/spring/summer
                List<Course> courses = new ArrayList<>();
                if (semester.getName().startsWith("Fall ")) {
                    logger.debug("Found fall course: [{}]");
                    courses.addAll(courseRepository.getAllByFall(true));
                } else if (semester.getName().startsWith("Spring ")) {
                    logger.debug("Found spring course: [{}]");
                    courses.addAll(courseRepository.getAllBySpring(true));
                } else if (semester.getName().startsWith(("Summer "))) {
                    logger.debug("Found summer course: [{}]");
                    courses.addAll(courseRepository.getAllBySummer(true));
                } else {
                    logger.error("Could not determine fall/spring/summer schedule for semester [{}]", semester);
                }

                if (courses.size() > 0) { //Meaning that you found something to save
                    CourseEventContainer courseEventContainer = new CourseEventContainer();
                    courses.stream().forEach(c -> courseEventContainer.addCourseEvent(new CourseEvent(c.getId(), defaultClassCapSize)));

                    eventLogInsertService.insertCourseEvents(courseEventContainer, semester, false);
                }

            }
        } else {
            logger.debug("EventLog already has data ([{} rows], not pre-populating", eventLogTableSize);
        }
    }
}
