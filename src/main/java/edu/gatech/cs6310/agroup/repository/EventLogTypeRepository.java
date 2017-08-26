package edu.gatech.cs6310.agroup.repository;

import edu.gatech.cs6310.agroup.model.EventLogType;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by mlarson on 4/1/16.
 */
public interface EventLogTypeRepository extends JpaRepository<EventLogType, Integer> {

    public EventLogType findByTypeName(String typeName);

}
