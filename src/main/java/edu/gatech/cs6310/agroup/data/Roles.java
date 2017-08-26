package edu.gatech.cs6310.agroup.data;

import edu.gatech.cs6310.agroup.model.Role;
import edu.gatech.cs6310.agroup.util.CSVLoader;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to load role data
 *
 * <pre>CS6310 Software Architecture and Design, Spring 2016
 * Project 1: Integer programming for student-course assignment</pre>
 *
 * @author <a href="mailto:tcesposito@gatech.edu">Timothy Esposito</a>
 */
public class Roles extends CSVLoader {

    private Map<Integer, Role> roles = new HashMap<>();

    public Roles() {
        loadResource("/static/roles.csv");
    }

    public Roles(String objectsFile) {
        loadFile(objectsFile);
    }

    @Override
    public void addObject(String[] parts) {
        if (parts.length == 3) {
            int id = Integer.parseInt(parts[0]);
            Role r = new Role();
            r.setID(id);
            r.setName(parts[1]);
            r.setDescription(parts[2]);
            roles.put(id, r);
        }
    }

    public Map<Integer, Role> getRoles() {
        return roles;
    }
}
