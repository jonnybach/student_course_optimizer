package edu.gatech.cs6310.agroup.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.gatech.cs6310.agroup.eventmodel.TopLevelSerializableEvent;
import edu.gatech.cs6310.agroup.exception.EventSerializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Created by matt.larson on 4/6/2016.
 */
@Service
public class SerializableEventServiceJson implements SerializableEventService {

    //This will inject Spring's object mapper
    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Override
    public String serialize(TopLevelSerializableEvent serializableEvent) throws EventSerializationException {

        try {
            return jacksonObjectMapper.writeValueAsString(serializableEvent);
        } catch (JsonProcessingException e) {
            throw new EventSerializationException("Problem serializing object " + serializableEvent.getClass().getName(), e);
        }
    }

    /**
     * This method will deserialize anything that extends the TopLevelSerializableEvent interface
     *
     * @param objString
     * @param clazz
     * @param <T>
     * @return Whatever type is given in the Class<T> parameter
     * @throws EventSerializationException
     */
    @Override
    public <T extends TopLevelSerializableEvent> T deserialize(String objString, Class<T> clazz) throws EventSerializationException {

        try {
            return jacksonObjectMapper.readValue(objString, clazz);
        } catch (IOException e) {
            throw new EventSerializationException("Problem deserializing String [" + objString + "] from class [" + clazz.getName() + "]", e);
        }
    }
}
