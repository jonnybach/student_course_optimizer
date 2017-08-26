package edu.gatech.cs6310.agroup.uievent;

import org.springframework.context.ApplicationEvent;

/**
 * Created by jonathan on 4/18/16.
 */
public class LogoutEvent extends ApplicationEvent {
    public LogoutEvent(Object source) {
        super(source);
    }
}
