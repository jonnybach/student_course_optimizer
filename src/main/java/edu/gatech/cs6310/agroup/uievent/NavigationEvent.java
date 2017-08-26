package edu.gatech.cs6310.agroup.uievent;

import org.springframework.context.ApplicationEvent;

/**
 * Created by jonathan on 4/18/16.
 */
public class NavigationEvent extends ApplicationEvent {

    private String viewName;

    public NavigationEvent(Object source, String viewNameToShow) {
        super(source);
        viewName = viewNameToShow;
    }

    public String getViewName() { return viewName; }

    public String toString(){
        return String.format("Navigation event, view name: %s", viewName);
    }

}

