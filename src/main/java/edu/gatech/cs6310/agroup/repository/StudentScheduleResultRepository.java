package edu.gatech.cs6310.agroup.repository;

import edu.gatech.cs6310.agroup.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by mlarson on 4/1/16.
 */
public interface StudentScheduleResultRepository extends JpaRepository<StudentScheduleResult, Long> {

    @Query("SELECT s FROM StudentScheduleResult s WHERE s.eventLog = ? AND s.student = ?")
    public List<StudentScheduleResult> getStudentScheduleResultsForEventLog(EventLog eventLog, Student student);

    @Query("SELECT s FROM StudentScheduleResult s WHERE s.eventLog = ?")
    public List<StudentScheduleResult> getAllStudentScheduleResultsForEventLog(EventLog eventLog);

}
