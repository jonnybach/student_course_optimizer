package edu.gatech.cs6310.agroup.uievent;

import org.springframework.context.ApplicationEvent;

/**
 * Created by jonathan on 4/18/16.
 */
public class SubmitForCalculationEvent extends ApplicationEvent {
    public SubmitForCalculationEvent(Object source) {
        super(source);
    }
}
