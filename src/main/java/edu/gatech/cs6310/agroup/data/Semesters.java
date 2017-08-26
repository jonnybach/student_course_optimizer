package edu.gatech.cs6310.agroup.data;

import edu.gatech.cs6310.agroup.model.Semester;
import edu.gatech.cs6310.agroup.util.CSVLoader;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to load semester data
 *
 * <pre>CS6310 Software Architecture and Design, Spring 2016
 * Project 1: Integer programming for student-course assignment</pre>
 *
 * @author <a href="mailto:tcesposito@gatech.edu">Timothy Esposito</a>
 */
public class Semesters extends CSVLoader {

    private Map<Integer, Semester> semesters = new HashMap<>();

    public Semesters() {
        loadResource("/static/semesters.csv");
    }

    public Semesters(String objectsFile) {
        loadFile(objectsFile);
    }

    @Override
    public void addObject(String[] parts) {
        if (parts.length == 4) {
            int id = Integer.parseInt(parts[0]);
            Semester s = new Semester();
            s.setId(id);
            s.setName(parts[1]);
            s.setStartDate(parts[2]);
            s.setEndDate(parts[3]);
            semesters.put(id, s);
        }
    }

    public Map<Integer, Semester> getSemesters() {
        return semesters;
    }
}
