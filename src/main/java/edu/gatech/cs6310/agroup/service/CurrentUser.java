package edu.gatech.cs6310.agroup.service;

import com.vaadin.server.VaadinSession;

/**
 * Convenience wrapper to track the currently logged in user
 * Borrowed from https://github.com/vaadin-marcus/vaadin-tips
 * Created by jonathan on 4/18/16.
 */
public class CurrentUser {

    public enum UserType {
        NONE,
        ADMIN,
        STUDENT
    }

    public static final String ID = "current_user_id";
    public static final String NAME = "current_user_name";
    public static final String TYPE = "current_user_type";

    public static void set(Integer id, String userName, UserType usertype) {
        VaadinSession.getCurrent().setAttribute(ID, id);
        VaadinSession.getCurrent().setAttribute(NAME, userName);
        VaadinSession.getCurrent().setAttribute(TYPE, usertype);
    }

    public static Integer getId() { return (Integer) VaadinSession.getCurrent().getAttribute(ID); }

    public static String getName() {
        return (String) VaadinSession.getCurrent().getAttribute(NAME);
    }

    public static UserType getType() {
        return (UserType)VaadinSession.getCurrent().getAttribute(TYPE);
    }

    public static boolean isLoggedIn() {
        return getName() != null;
    }

}
