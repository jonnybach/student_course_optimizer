package edu.gatech.cs6310.agroup.model;

import edu.gatech.cs6310.agroup.eventmodel.CourseEventContainer;
import edu.gatech.cs6310.agroup.eventmodel.StudentDemandEventContainer;
import edu.gatech.cs6310.agroup.eventmodel.TopLevelSerializableEvent;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by ubuntu on 4/6/16.
 */
@Entity
public class EventLogType {

    /**
     * Used for convenience to track the different event log types. Each type should be represented in the database by a type of the same name.
     *
     * Need to make sure the IDs match with what's in the database. The class here is the top level container that will be serialized to and from the DB.
     *
     * Also make sure the names (e.g. COURSES_ADDED) match what's in the database for the type name.
     *
     */
    public enum EVENT_LOG_TYPE {
        COURSES_ADDED(1, CourseEventContainer.class),
        STUDENT_DEMAND_ADDED(2, StudentDemandEventContainer.class);

        private int eventLogTypeId;
        private Class<? extends TopLevelSerializableEvent> topLevelSerializableEventClass;

        EVENT_LOG_TYPE(int eventLogTypeId, Class<? extends TopLevelSerializableEvent> topLevelSerializableEvent) {
            this.eventLogTypeId = eventLogTypeId;
            this.topLevelSerializableEventClass = topLevelSerializableEvent;
        }

        public int getEventLogTypeId() {
            return eventLogTypeId;
        }

        public Class<? extends TopLevelSerializableEvent> getTopLevelSerializableEventClass() {
            return topLevelSerializableEventClass;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String typeName;
    private String description;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
