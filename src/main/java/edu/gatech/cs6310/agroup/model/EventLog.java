package edu.gatech.cs6310.agroup.model;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by ubuntu on 4/6/16.
 */
@Entity
public class EventLog {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    long id;

    String eventData;
    boolean isShadowMode;

    @Temporal(TemporalType.TIMESTAMP)
    Date createdDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "semesterId", nullable = false)
    private Semester semester;

    //Fetch type is eager because we're going to want this for most EventLog(s)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "eventLogTypeId", nullable = false)

    EventLogType eventLogType;

    boolean resultCalculated;

    public EventLogType getEventLogType() {
        return eventLogType;
    }

    public void setEventLogType(EventLogType eventLogType) {
        this.eventLogType = eventLogType;
    }

    public String getEventData() {
        return eventData;
    }

    public void setEventData(String eventData) {
        this.eventData = eventData;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Semester getSemester() {
        return semester;
    }

    public void setSemester(Semester semester) {
        this.semester = semester;
    }

    public long getId() {

        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isShadowMode() {
        return isShadowMode;
    }

    public void setIsShadowMode(boolean shadowMode) {
        isShadowMode = shadowMode;
    }

    public boolean isResultCalculated() {
        return resultCalculated;
    }

    public void setResultCalculated(boolean resultCalculated) {
        this.resultCalculated = resultCalculated;
    }

    @Override
    public String toString() {
        return "EventLog{" +
                "id=" + id +
                ", eventData='" + eventData + '\'' +
                ", isShadowMode=" + isShadowMode +
                ", createdDate=" + createdDate +
                ", semester=" + semester +
                ", eventLogType=" + eventLogType +
                '}';
    }
}
