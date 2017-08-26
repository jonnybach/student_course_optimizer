package edu.gatech.cs6310.agroup.repository;

import edu.gatech.cs6310.agroup.model.Course;
import edu.gatech.cs6310.agroup.model.StudentDemand;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by mlarson on 4/1/16.
 */
public interface StudentDemandRepository extends JpaRepository<StudentDemand, Integer> {

}
