package edu.gatech.cs6310.agroup.repository;

import edu.gatech.cs6310.agroup.model.EventLog;
import edu.gatech.cs6310.agroup.model.Semester;
import edu.gatech.cs6310.agroup.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

/**
 * Created by mlarson on 4/1/16.
 */
public interface SemesterRepository extends JpaRepository<Semester, Integer> {

}
