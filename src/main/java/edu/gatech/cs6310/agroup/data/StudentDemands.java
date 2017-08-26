package edu.gatech.cs6310.agroup.data;

import edu.gatech.cs6310.agroup.model.Course;
import edu.gatech.cs6310.agroup.model.Semester;
import edu.gatech.cs6310.agroup.model.Student;
import edu.gatech.cs6310.agroup.model.StudentDemand;
import edu.gatech.cs6310.agroup.repository.CourseRepository;
import edu.gatech.cs6310.agroup.repository.SemesterRepository;
import edu.gatech.cs6310.agroup.repository.StudentRepository;
import edu.gatech.cs6310.agroup.util.CSVLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Utility class to load student demand data
 *
 * <pre>CS6310 Software Architecture and Design, Spring 2016
 * Project 1: Integer programming for student-course assignment</pre>
 *
 * @author <a href="mailto:tcesposito@gatech.edu">Timothy Esposito</a>
 */
public class StudentDemands extends CSVLoader {

    private List<StudentDemand> demands = new ArrayList<>();

    //Set these in the constructor and
    private Map<Integer, Student> students;
    private Map<Integer, Course> courses;
    private Map<Integer, Semester> semesters;

    public StudentDemands(String objectsResource, Map<Integer, Student> students, Map<Integer, Course> courses, Map<Integer, Semester> semesters) {

        this.students = students;
        this.courses = courses;
        this.semesters = semesters;

        loadResource(objectsResource);
    }

    /**
     * Note that the student, course, and semester files have to be loaded before this for it to work
     *
     * @param parts
     */
    @Override
    public void addObject(String[] parts) {
        if (parts.length == 3) {
            int studentId = Integer.parseInt(parts[0]);
            int courseId = Integer.parseInt(parts[1]);
            int semesterId = Integer.parseInt(parts[2]);

            Student student = students.get(studentId);
            Course course = courses.get(courseId);
            Semester semester = semesters.get(semesterId);

            StudentDemand sd = new StudentDemand();
            sd.setStudent(student);
            sd.setCourse(course);
            sd.setSemester(semester);
            demands.add(sd);
        }
    }

    public List<StudentDemand> getDemands() {
        return demands;
    }
}
