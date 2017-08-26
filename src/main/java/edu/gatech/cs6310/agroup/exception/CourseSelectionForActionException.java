package edu.gatech.cs6310.agroup.exception;

import com.google.gwt.thirdparty.guava.common.util.concurrent.UncheckedExecutionException;

/**
 * Created by matt.larson on 4/6/2016. Use this class for serialization exceptions
 */
public class CourseSelectionForActionException extends RuntimeException  {

    public CourseSelectionForActionException(String msg) {
        super(msg);
    }

    public CourseSelectionForActionException(Throwable cause) {
        super(cause);
    }

    public CourseSelectionForActionException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
