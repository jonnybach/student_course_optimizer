package edu.gatech.cs6310.agroup.ui;

import com.vaadin.data.validator.AbstractValidator;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import edu.gatech.cs6310.agroup.repository.StudentRepository;
import edu.gatech.cs6310.agroup.service.CurrentUser;
import edu.gatech.cs6310.agroup.uievent.LoginEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jonathan on 4/5/16.
 */

@UIScope
@SpringComponent(MainLoginView.NAME)
public class MainLoginView extends CustomComponent implements Button.ClickListener,
        ApplicationEventPublisherAware {

    public static final String NAME = "login_view";
    private static final Logger log = LoggerFactory.getLogger(MainLoginView.class);

    private ApplicationEventPublisher publisher;
    private final OptionGroup userType;
    private final TextField userFld;
    private final PasswordField passwordField;
    private final Button loginButton;

    @Autowired
    private StudentRepository studentRepository;

    public MainLoginView() {

        setSizeFull();

        //Create option control to select between student or admin
        userType = new OptionGroup("Select user type:");
        userType.addItem(CurrentUser.UserType.ADMIN.toString());
        userType.addItem(CurrentUser.UserType.STUDENT.toString());

        // Create the user input field
        userFld = new TextField("User:");
        userFld.setWidth("300px");
        userFld.setRequired(true);
        userFld.setInputPrompt("Your username (eg. username@gatech.edu)");
        userFld.setValue("user1@gatech.edu");
        userFld.addValidator(new EmailValidator(
                "Username must be an email address"));
        userFld.setInvalidAllowed(false);

        // Create the passwordField input field
        passwordField = new PasswordField("Password:");
        passwordField.setWidth("300px");
        passwordField.setRequired(true);
        passwordField.setValue("passw0rd");
        passwordField.addValidator(new PasswordValidator());
        passwordField.setNullRepresentation("");

        // Create login button
        loginButton = new Button("Login", this);

        // Add both to a panel
        VerticalLayout fields = new VerticalLayout(userType, userFld, passwordField, loginButton);
        fields.setCaption("GODAC System Login (HINT: As a student, login with user<USER ID>@gatech.edu/passw0rd. As an admin, login with any email address and \"passw0rd\". )");
        fields.setSpacing(true);
        fields.setMargin(new MarginInfo(true, true, true, false));
        fields.setSizeUndefined();

        // The view root layout
        VerticalLayout viewLayout = new VerticalLayout(fields);
        viewLayout.setSizeFull();
        viewLayout.setComponentAlignment(fields, Alignment.MIDDLE_CENTER);
        setCompositionRoot(viewLayout);
    }

    @Override
    public void setApplicationEventPublisher (ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    // Validator for validating the passwords
    private static final class PasswordValidator extends
            AbstractValidator<String> {

        public PasswordValidator() {
            super("The password must contain at least 8 characters and contain at least one number");
        }

        @Override
        protected boolean isValidValue(String value) {
            // Password must be at least 8 characters long and contain at least
            // one number
            if (value != null
                    && (value.length() < 8 || !value.matches(".*\\d.*"))) {
                return false;
            }
            return true;
        }

        @Override
        public Class<String> getType() {
            return String.class;
        }
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {

        //set values of UI elements
        Object usertype = this.userType.getValue();
        String username = this.userFld.getValue();
        String password = this.passwordField.getValue();

        // Validate the fields using the navigator. By using validors for the
        // fields we reduce the amount of queries we have to use to the database
        // for wrongly entered passwords
        if (!userFld.isValid() || !passwordField.isValid()) {
            return;
        } else if (usertype == null) {
            Notification.show("Error logging in",
                    "Please choose the type of user to log in as.",
                    Notification.Type.ERROR_MESSAGE);
            return;
        }

        // Validate username and passwordField with database here. For examples sake
        // for now use a dummy username and passwordField.
        boolean isValid = false;
        int userId = 0;

        if (password.equals("passw0rd")) { //Password is good
            //Now check the login name and get the student ID from that
            Pattern usernamePattern = Pattern.compile("user(\\d+)@gatech.edu");
            Matcher matcher = usernamePattern.matcher(username);

            //Make sure we can get the user ID from the username if this is a student
            if (matcher.matches() && CurrentUser.UserType.STUDENT.name().equalsIgnoreCase((String) userType.getValue())) {
                String studentIdRaw = matcher.group(1);

                int studentId = Integer.parseInt(studentIdRaw);

                //Make sure this is a valid student ID
                if (studentId >= 1 && studentId <= studentRepository.getMaxId()) {
                    log.debug("Logging in user with student ID [{}]", studentId); //Note that the user ID is set into the session below
                    //VaadinSession.getCurrent().setAttribute(CurrentUser.ID, studentId);
                    userId = studentId;
                    isValid = true;
                }
            } else { //For admins just take any username
                isValid = true;
            }

        }

        //log.debug("Got to button click got user, passwordField. isValid:  %d", isValid);

        if (isValid) {

            //notify the main ui that the login was successful
            LoginEvent loginEvent = new LoginEvent(this, username, CurrentUser.UserType.valueOf((String)usertype), userId);
            publisher.publishEvent(loginEvent);

            // Store the current user in the service session
            //CurrentUser.set(username, CurrentUser.UserType.valueOf((String)usertype));

            // Navigate to main view
            //getUI().getNavigator().navigateTo(MainView.NAME);//

        } else {
            Notification.show("Error logging in",
                    "Invalid login credentials provided.  HINT: take a better look at the label above. ;)",
                    Notification.Type.ERROR_MESSAGE);
            // Wrong passwordField clear the passwordField field and refocuses it
            this.passwordField.setValue(null);
            this.passwordField.focus();

        }
    }
}
