package edu.gatech.cs6310.agroup.repository;

import edu.gatech.cs6310.agroup.model.EventLog;
import edu.gatech.cs6310.agroup.model.Semester;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by mlarson on 4/1/16.
 */
@Repository
public interface EventLogRepository extends PagingAndSortingRepository<EventLog, Long> {

    /**
     * This method returns all the relevant EventLog(s) that will be used to calculate state
     * @param semester
     * @param eventLogId
     * @return
     */
    //@Cacheable("eventLogs")
    @Query("SELECT e FROM EventLog e WHERE e.semester = ? AND e.id <= ? ORDER BY e.id")
    public List<EventLog> getEventLogsBySemesterUpToEventLogId(Semester semester, long eventLogId);

    //@Cacheable("eventLogs")
    @Query("SELECT e FROM EventLog e WHERE e.semester = ? AND e.eventLogType.typeName = 'STUDENT_DEMAND_ADDED' ORDER BY e.id")
    public List<EventLog> getStudentDemandEventLogsBySemester(Semester semester);

    @Query("SELECT MAX(e.id) FROM EventLog e")
    public long getMaxId();

    @Query("SELECT e FROM EventLog e WHERE e.semester = ? AND resultCalculated = true ORDER BY e.createdDate DESC")
    public List<EventLog> getEventLogsBySemesterWithResultsCalculated(Semester semester, Pageable pageable);

    default List<EventLog> getTopEventsLogsBySemesterWithResultsCalculated(Semester semester) {
        return getEventLogsBySemesterWithResultsCalculated(semester, new PageRequest(0,15));
    }

}
