package edu.gatech.cs6310.agroup.exception;

/**
 * Created by matt.larson on 4/6/2016. Use this class for serialization exceptions
 */
public class EventSerializationException extends Exception {

    public EventSerializationException(String msg) {
        super(msg);
    }

    public EventSerializationException(Throwable cause) {
        super(cause);
    }

    public EventSerializationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
