package edu.gatech.cs6310.agroup.service;

import edu.gatech.cs6310.agroup.eventmodel.TopLevelSerializableEvent;
import edu.gatech.cs6310.agroup.exception.EventSerializationException;

/**
 * Created by matt.larson on 4/6/2016. Service to do the serialization to and from the DB.
 */
public interface SerializableEventService {
    String serialize(TopLevelSerializableEvent serializableEvent) throws EventSerializationException;
    <T extends TopLevelSerializableEvent> T deserialize(String objString, Class<T> clazz) throws EventSerializationException;
}
