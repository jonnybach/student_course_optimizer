package edu.gatech.cs6310.agroup.data;

import edu.gatech.cs6310.agroup.model.Department;
import edu.gatech.cs6310.agroup.util.CSVLoader;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to load department data
 *
 * <pre>CS6310 Software Architecture and Design, Spring 2016
 * Project 1: Integer programming for student-course assignment</pre>
 *
 * @author <a href="mailto:tcesposito@gatech.edu">Timothy Esposito</a>
 */
public class Departments extends CSVLoader {

    private Map<Integer, Department> departments = new HashMap<>();

    public Departments() {
        loadResource("/static/departments.csv");
    }

    public Departments(String objectsFile) {
        loadFile(objectsFile);
    }

    @Override
    public void addObject(String[] parts) {
        if (parts.length == 3) {
            int id = Integer.parseInt(parts[0]);
            Department d = new Department();
            d.setID(id);
            d.setCode(parts[1]);
            d.setName(parts[2]);
            departments.put(id, d);
        }
    }

    public Map<Integer, Department> getDepartments() {
        return departments;
    }
}
