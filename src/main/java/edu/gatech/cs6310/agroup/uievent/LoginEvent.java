package edu.gatech.cs6310.agroup.uievent;

import edu.gatech.cs6310.agroup.service.CurrentUser;
import org.springframework.context.ApplicationEvent;

/**
 * Created by jonathan on 4/18/16.
 */
public class LoginEvent extends ApplicationEvent {

    private String loginName;

    private int userID;
    CurrentUser.UserType loginType;

    public LoginEvent(Object source, String userName, CurrentUser.UserType type, int userId) {
        super(source);
        this.userID = userId;
        loginName = userName;
        loginType = type;
    }


    public String getLoginName() { return loginName; }
    public CurrentUser.UserType getLoginType() { return loginType; }

    public int getUserID() {
        return userID;
    }

    public String toString(){
        String usrTyp = loginType.toString();
        return String.format("Login event, user: %s, type: %s", loginName, usrTyp);
    }

}
