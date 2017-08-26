package edu.gatech.cs6310.agroup.data;

import edu.gatech.cs6310.agroup.model.Course;
import edu.gatech.cs6310.agroup.model.Student;
import edu.gatech.cs6310.agroup.model.StudentDemand;
import edu.gatech.cs6310.agroup.util.CSVLoader;

import java.math.BigDecimal;
import java.util.*;

/**
 * Utility class to load student demand data
 *
 * <pre>CS6310 Software Architecture and Design, Spring 2016
 * Project 1: Integer programming for student-course assignment</pre>
 *
 * @author <a href="mailto:tcesposito@gatech.edu">Timothy Esposito</a>
 */
public class StudentRecords extends CSVLoader {

    private Map<Integer, Student> studentMap = new HashMap<>();

    public StudentRecords(String objectsResource) {
        loadResource(objectsResource);
    }

    @Override
    public void addObject(String[] parts) {

        if (parts.length == 3) {
            int id = Integer.parseInt(parts[0]);
            Student s = new Student();
            s.setId(id);
            s.setSeniority(Integer.parseInt(parts[1]));
            s.setGpa(new BigDecimal(parts[2]));

            studentMap.put(id, s);
        }
    }

    public Map<Integer, Student> getStudentRecords() {
        return studentMap;
    }
}
