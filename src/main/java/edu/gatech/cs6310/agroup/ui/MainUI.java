package edu.gatech.cs6310.agroup.ui;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import edu.gatech.cs6310.agroup.service.CurrentUser;
import edu.gatech.cs6310.agroup.uievent.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;

@Theme("valo")
@Title("Project 3 - Distributed Optimized Student Course Allocation")
@SpringUI(path = "/")
@Push
public class MainUI extends UI {

    private static final Logger log = LoggerFactory.getLogger(MainUI.class);

    @Autowired
    private MainView mainView;
    @Autowired
    private MainLoginView loginView;

    @Override
    protected void init(VaadinRequest request) {

        if (CurrentUser.isLoggedIn()) {
            setContent(mainView);
        } else {
            setContent(loginView);
        }

    }

    @EventListener
    public void onApplicationEvent(LoginEvent event) {
        //login was successful, set the panel content based on the type of user
        CurrentUser.set(event.getUserID(), event.getLoginName(), event.getLoginType());
        mainView.initialize();
        setContent(mainView);
    }

    @EventListener
    public void onApplicationEvent(LogoutEvent event) {
        VaadinSession.getCurrent().getSession().invalidate();
        VaadinSession.getCurrent().close();
        Page.getCurrent().reload();
    }

    @EventListener
    public void onApplicationEvent(NavigationEvent event) {
        getNavigator().navigateTo(event.getViewName());
    }

    @EventListener
    public void onApplicationEvent(SubmitForCalculationEvent event) {
        log.debug("Just heard the submit for calculation call.");
        Notification.show("Course Assignment Calculation Job Submitted.", Notification.Type.TRAY_NOTIFICATION);
    }

}
