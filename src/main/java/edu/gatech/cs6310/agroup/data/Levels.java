package edu.gatech.cs6310.agroup.data;

import edu.gatech.cs6310.agroup.model.Level;
import edu.gatech.cs6310.agroup.util.CSVLoader;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to load level data
 *
 * <pre>CS6310 Software Architecture and Design, Spring 2016
 * Project 1: Integer programming for student-course assignment</pre>
 *
 * @author <a href="mailto:tcesposito@gatech.edu">Timothy Esposito</a>
 */
public class Levels extends CSVLoader {

    private Map<Integer, Level> levels = new HashMap<>();

    public Levels() {
        loadResource("/static/levels.csv");
    }

    public Levels(String objectsFile) {
        loadFile(objectsFile);
    }

    @Override
    public void addObject(String[] parts) {
        if (parts.length == 2) {
            int id = Integer.parseInt(parts[0]);
            Level l = new Level();
            l.setID(id);
            l.setName(parts[1]);
            levels.put(id, l);
        }
    }

    public Map<Integer, Level> getLevels() {
        return levels;
    }
}
