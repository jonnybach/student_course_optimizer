package edu.gatech.cs6310.agroup.data;

import edu.gatech.cs6310.agroup.model.Course;
import edu.gatech.cs6310.agroup.util.CSVLoader;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to load courses data
 *
 * <pre>CS6310 Software Architecture and Design, Spring 2016
 * Project 1: Integer programming for student-course assignment</pre>
 *
 * @author <a href="mailto:tcesposito@gatech.edu">Timothy Esposito</a>
 */
public class Courses extends CSVLoader {

    private Map<Integer, Course> courses = new HashMap<>();

    public Courses() {
        loadResource("/static/courses.csv");
    }

    public Courses(String objectsFile) {
        loadFile(objectsFile);
    }

    @Override
    public void addObject(String[] parts) {
        if (parts.length == 7) {
            int id = Integer.parseInt(parts[0]);
            Course c = new Course();
            c.setId(id);
            c.setName(parts[1].replaceAll("\"", "")); //Take the quotes out of the name too
            c.setNumber(parts[2]);
            c.setFall(parts[3].equals("1"));
            c.setSpring(parts[4].equals("1"));
            c.setSummer(parts[5].equals("1"));
            c.setAvailability(parts[6]);
            courses.put(id, c);
        }
    }

    public Map<Integer, Course> getCourses() {
        return courses;
    }
}
