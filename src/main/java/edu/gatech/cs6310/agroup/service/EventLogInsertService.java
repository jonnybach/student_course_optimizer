package edu.gatech.cs6310.agroup.service;

import antlr.collections.impl.IntRange;
import edu.gatech.cs6310.agroup.eventmodel.*;
import edu.gatech.cs6310.agroup.exception.EventSerializationException;
import edu.gatech.cs6310.agroup.model.*;
import edu.gatech.cs6310.agroup.repository.CourseRepository;
import edu.gatech.cs6310.agroup.repository.EventLogRepository;
import edu.gatech.cs6310.agroup.repository.EventLogTypeRepository;
import edu.gatech.cs6310.agroup.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by ubuntu on 4/6/16. This does translation from the action to the event log
 */
@Service
public class EventLogInsertService {

    @Autowired
    private EventLogRepository eventLogRepository;

    @Autowired
    private EventLogTypeRepository eventLogTypeRepository;

    @Autowired
    private SerializableEventService serializableEventService;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StudentRepository studentRepository;

    /**
     * Use this method to insert course events (a particular set of courses for a given semester)
     *
     * @param courseEventContainer
     * @param semester
     * @param isShadowMode
     * @return
     * @throws EventSerializationException
     */
    public EventLog insertCourseEvents(CourseEventContainer courseEventContainer, Semester semester, boolean isShadowMode) throws EventSerializationException {
        return createEventLog(courseEventContainer, semester, isShadowMode);
    }

    public EventLog insertCourseEvents(List<CourseEvent> courses, Semester semester, boolean isShadowMode) throws EventSerializationException {
        CourseEventContainer courseEventContainer = new CourseEventContainer();
        courses.stream().forEach(c -> courseEventContainer.addCourseEvent(c));

        return insertCourseEvents(courseEventContainer, semester, isShadowMode);
    }

    /**
     * Use this method when inserting a set of student demand for a particular group of courses
     *
     * @param studentDemandEventContainer
     * @param semester
     * @param isShadowMode
     * @return
     * @throws EventSerializationException
     */
    public EventLog insertStudentDemandEvent(StudentDemandEventContainer studentDemandEventContainer, Semester semester, boolean isShadowMode) throws EventSerializationException {

        return createEventLog(studentDemandEventContainer, semester, isShadowMode);
    }



    /**
     * This method will handle create events for all types
     * @param event
     * @param semester
     * @param isShadowMode
     * @return
     * @throws EventSerializationException
     */
    private EventLog createEventLog(TopLevelSerializableEvent event, Semester semester, boolean isShadowMode) throws EventSerializationException {
        EventLog eventLog = new EventLog();
        eventLog.setEventData(serializableEventService.serialize(event));
        eventLog.setCreatedDate(new Date());
        eventLog.setSemester(semester);
        eventLog.setIsShadowMode(isShadowMode);
        eventLog.setEventLogType(eventLogTypeRepository.findByTypeName(event.getEventLogTypeEnum().name()));
        eventLogRepository.save(eventLog);

        return eventLog;
    }

    public void generateRandomDataForSemester(Semester semester) throws EventSerializationException {
        /*List<Course> courses = courseRepository.findAll();

        //Generate random number to insert
        int totalRandomObjects = new Random().nextInt(courses.size()); // [0...10]

        //Get a course for each of those
        List<CourseEvent> courseEvents = new ArrayList<>();

        for (int i = 0; i < totalRandomObjects; i++) {
            courseEvents.add(new CourseEvent(new Random().nextInt(courses.size()) + 1, -1));
        }

        this.insertCourseEvents(courseEvents, semester, false);

        //Now generate some student demand for those courses
        List<Student> students = studentRepository.findAll();
        for (int i = 0; i < totalRandomObjects; i++) {

            //Get a random student ID
            int studentId = new Random().nextInt(students.size());

            //Insert a course for each total random objects for this student
            StudentDemandEventContainer studentDemandEventContainer = new StudentDemandEventContainer(studentId);
            for (int j = 0; j < totalRandomObjects; j++) {
                studentDemandEventContainer.addStudentDemandCourse(new StudentDemandCourse(new Random().nextInt(courses.size()) + 1, j + 1));
            }
            this.insertStudentDemandEvent(studentDemandEventContainer, semester, false);
        }*/




                /* //DO not know what the error was here
                IntStream.generate(() -> ThreadLocalRandom.current().nextInt(courses.size()))
                .limit(numCourses)
                .map(i -> new CourseEvent(1,-1))//new CourseEvent(new Random().nextInt(courses.size()), -1))
                .collect(Collectors.toList());
                */


        List<Course> courses = courseRepository.findAll();

        for (Student student : studentRepository.findAll()) {
            StudentDemandEventContainer studentDemandEventContainer = new StudentDemandEventContainer(student.getId());

            int courseCount = (int) (Math.random()*5 + 1);

            for (int c = 1; c <= courseCount; c++) {
                int randomCourse = (int) (Math.random()*courses.size());
                // TODO make sure the same course isn't randomly selected more than once
                studentDemandEventContainer.addStudentDemandCourse(new StudentDemandCourse(courses.get(randomCourse).getId(), c));
            }

            insertStudentDemandEvent(studentDemandEventContainer, semester, true);
        }

    }
}
