package edu.gatech.cs6310.agroup.repository;

import edu.gatech.cs6310.agroup.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

import java.util.List;

/**
 * Created by mlarson on 4/1/16.
 */
public interface CourseRepository extends JpaRepository<Course, Integer> {

    Course findByNumber(String courseNumber);
    List<Course> getAllByFall(Boolean offered);
    List<Course> getAllBySpring(Boolean offered);
    List<Course> getAllBySummer(Boolean offered);

}
