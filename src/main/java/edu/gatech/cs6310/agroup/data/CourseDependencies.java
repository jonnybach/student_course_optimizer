package edu.gatech.cs6310.agroup.data;

import edu.gatech.cs6310.agroup.model.Course;
import edu.gatech.cs6310.agroup.model.CourseDependency;
import edu.gatech.cs6310.agroup.util.CSVLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Utility class to load course dependency data
 * <p>
 * <pre>CS6310 Software Architecture and Design, Spring 2016
 * Project 1: Integer programming for student-course assignment</pre>
 *
 * @author <a href="mailto:tcesposito@gatech.edu">Timothy Esposito</a>
 */
public class CourseDependencies extends CSVLoader {

    private List<CourseDependency> courseDependencies = new ArrayList<>();

    Map<Integer, Course> coursesMap;

    public CourseDependencies(Map<Integer, Course> coursesMap) {
        this.coursesMap = coursesMap;

        loadResource("/static/course_dependencies.csv");
    }

    public CourseDependencies(String objectsFile) {
        loadFile(objectsFile);
    }

    @Override
    public void addObject(String[] parts) {
        if (parts.length == 2) {
            CourseDependency c = new CourseDependency();
            int prereqId = Integer.parseInt(parts[0]);
            int dependentId = Integer.parseInt(parts[1]);

            c.setPrereq(coursesMap.get(prereqId));
            c.setDependency(coursesMap.get(dependentId));
            courseDependencies.add(c);
        }
    }

    public List<CourseDependency> getCourseDependencies() {
        return courseDependencies;
    }
}
