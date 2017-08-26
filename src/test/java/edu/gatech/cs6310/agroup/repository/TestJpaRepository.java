package edu.gatech.cs6310.agroup.repository;

import edu.gatech.cs6310.agroup.app.Project3Application;
import edu.gatech.cs6310.agroup.model.Course;
import edu.gatech.cs6310.agroup.model.Semester;
import edu.gatech.cs6310.agroup.model.Student;
import edu.gatech.cs6310.agroup.model.StudentDemand;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


/**
 * Note that this doesn't work at the moment due to problem applying the DB access properties
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Project3Application.class)
public class TestJpaRepository {

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    SemesterRepository semesterRepository;

    @Autowired
    StudentDemandRepository studentDemandRepository;

    @Before
    public void setup() {
        //Create a test course
        Course course = new Course();
        course.setId(9999);
        course.setName("Test Course Saving");
        course.setNumber("9999");
        course.setFall(true);
        course.setSpring(false);
        course.setSummer(false);
        course.setAvailability("Fall Only");
        courseRepository.save(course);

        //Create a test student
        Student student = new Student();
        student.setId(9999);
        student.setSeniority(4);
        student.setGpa(new BigDecimal(3));
        studentRepository.save(student);

        //Create a test semester
        Semester semester = new Semester();
        semester.setId(9999);
        semester.setName("Fall Year 1");
        semesterRepository.save(semester);

        //Create test student demand
        StudentDemand studentDemand = new StudentDemand();
        studentDemand.setCourse(course);
        studentDemand.setSemester(semester);
        studentDemand.setStudent(student);
        studentDemandRepository.save(studentDemand);

    }

	@Test
	public void testCourse() {

        //1,'Advanced Operating Systems',6210, true, false, false, 'Fall Only');

        Course retrievedCourse = courseRepository.findByNumber("9999");

        assertTrue("We should have retrieved the test course", retrievedCourse.getName().equals("Test Course Saving"));
        assertFalse("The course title should not be 'KBAI'", retrievedCourse.getName().equals("KBAI"));
	}

    @Test
    public void testStudentDemand() {
        studentDemandRepository.findAll();
        /*Student student = studentDemand.getStudent();

        assertTrue("Student should have seniority of 4", student.getSeniority() == 4);*/
    }

    @After
    public void cleanup() {
        Course retrievedCourse = courseRepository.findByNumber("9999");
        if (retrievedCourse != null)
            courseRepository.delete(retrievedCourse);

        Student retrievedStudent = studentRepository.findOne(9999);
        if (retrievedStudent != null)
            studentRepository.delete(retrievedStudent);

        Semester semester = semesterRepository.findOne(9999);
        if (semester != null)
            semesterRepository.delete(semester);

        /*StudentDemand studentDemand = studentDemandRepository.findOne(9999);
        if (studentDemand != null)
            studentDemandRepository.delete(studentDemand);*/

    }
}
