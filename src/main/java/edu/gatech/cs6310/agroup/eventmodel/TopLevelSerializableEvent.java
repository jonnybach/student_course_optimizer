package edu.gatech.cs6310.agroup.eventmodel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import edu.gatech.cs6310.agroup.model.EventLogType;

/**
 * Created by ubuntu on 4/7/16. Interface just to use for typing which top level events can actually be serialized (any nested data structures will
 * then be automatically serialized as part of that)
 */
public interface TopLevelSerializableEvent {

    /**
     * @return The type that we should use for this event. We do not need this serialized because it is stored in an EventLog field already. Also, yes
     * this is direct leakage of the serialization implementation into its abstract definition.
     */
    @JsonIgnore
    public EventLogType.EVENT_LOG_TYPE getEventLogTypeEnum();

    public StateContainer addSerializableEventToStateContainer(StateContainer stateContainer);
}
