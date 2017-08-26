package edu.gatech.cs6310.agroup.ui;

/**
 * Created by jonathan on 4/17/16.
 */

import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import com.vaadin.navigator.View;
import edu.gatech.cs6310.agroup.uievent.CourseSelectedEvent;
import edu.gatech.cs6310.agroup.uievent.NavigationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import javax.annotation.PostConstruct;

@SpringView(name = CourseSelectionView.NAME)
public class CourseSelectionView extends VerticalLayout implements View,
        ApplicationEventPublisherAware {

    public static final String NAME = "add_course_view";
    private static final Logger log = LoggerFactory.getLogger(CourseSelectionView.class);

    private ApplicationEventPublisher publisher;
    private Label lbl;
    private ListSelect crsList;
    private View previousView;
    private CourseSelectedEvent.SelectionAction selectedAction;

    @PostConstruct
    public void init() {

        setSpacing(true);
        setMargin(true);

        lbl = new Label();
        lbl.setWidth("100%");
        addComponent(lbl);
        setComponentAlignment(lbl, Alignment.MIDDLE_CENTER);

        //create list of courses to choose from
        crsList = new ListSelect();
        crsList.setRows(10);
        crsList.setHeight("100%");
        crsList.setWidth("100%");
        crsList.setMultiSelect(false);
        crsList.setNullSelectionAllowed(false);
        addComponent(crsList);

        //create an add button
        Button okBtn = new Button("OK");
        okBtn.addClickListener(clkevent -> handleOkClick(clkevent));
        //create a cancel button
        Button cancelBtn = new Button("Cancel");
        cancelBtn.addClickListener(clkevent -> handleCancelClick(clkevent));
        HorizontalLayout btnLyt = new HorizontalLayout();
        btnLyt.addComponent(cancelBtn);
        btnLyt.addComponent(okBtn);
        btnLyt.setSpacing(true);
        btnLyt.setMargin(true);
        addComponent(btnLyt);
        setComponentAlignment(btnLyt, Alignment.MIDDLE_RIGHT);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

        previousView = event.getOldView();

        final String DELIMITER = ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)";

        //parse parameters coming from the navigation event
        String params = event.getParameters();
        String[] parts = params.split(DELIMITER);
        for (Integer i = 0; i < parts.length; i++) {
            if (i == 0) {
                //first entry is always the action, to be communicated back
                // in the selection event
                selectedAction = CourseSelectedEvent.SelectionAction.valueOf(parts[0]);
            } else {
                if ( !parts[i].equals("") ) {
                    crsList.addItem(parts[i]);
                }
            }
        }

        switch (selectedAction) {
            case ADD:
                lbl.setCaption("Please select a course to add");
                break;
            case REMOVE:
                lbl.setCaption("Please select a course to remove");
                break;
        }

    }

    public View getPreviousView() { return previousView; }

    @Override
    public void setApplicationEventPublisher (ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void handleOkClick(Button.ClickEvent event) {

        Object selectedCourse = crsList.getValue();

        CourseSelectedEvent crsEvt = new CourseSelectedEvent(this, selectedCourse, selectedAction);
        this.publisher.publishEvent(crsEvt);

        NavigationEvent navEvt = new NavigationEvent(this, AdminSemesterConfigView.NAME);
        this.publisher.publishEvent(navEvt);
    }

    public void handleCancelClick(Button.ClickEvent event) {
        NavigationEvent navEvt = new NavigationEvent(this, AdminSemesterConfigView.NAME);
        this.publisher.publishEvent(navEvt);
    }

}
