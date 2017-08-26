package edu.gatech.cs6310.agroup.ui;

import com.vaadin.data.util.IndexedContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import edu.gatech.cs6310.agroup.exception.CourseSelectionForActionException;
import edu.gatech.cs6310.agroup.exception.EventSerializationException;
import edu.gatech.cs6310.agroup.model.Course;
import edu.gatech.cs6310.agroup.repository.CourseRepository;
import edu.gatech.cs6310.agroup.service.SystemSettings;
import edu.gatech.cs6310.agroup.service.VaadinAdapter;
import edu.gatech.cs6310.agroup.uievent.CourseSelectedEvent;
import edu.gatech.cs6310.agroup.uievent.LogoutEvent;
import edu.gatech.cs6310.agroup.uievent.NavigationEvent;
import edu.gatech.cs6310.agroup.uievent.SubmitForCalculationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.event.EventListener;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@UIScope
@SpringView(name = AdminSemesterConfigView.NAME)
public class AdminSemesterConfigView extends GridLayout implements View,
        ApplicationEventPublisherAware {

    public static final String NAME = "semester_config_view";
    private static final Logger log = LoggerFactory.getLogger(AdminSemesterConfigView.class);

    @Autowired
    private SystemSettings systemSettings;

    @Autowired
    private VaadinAdapter vaadinAdapter;

    @Autowired
    private CourseRepository courseRepository;

    private ApplicationEventPublisher publisher;
    private IndexedContainer semesCnfg;
    private Table tblCorsCnfg;
    private Long activeEventId;

    @Value("${default.class.cap.size}")
    private Integer defaultCap;

    @PostConstruct
    private void init() {

        //set grid configuration
        setColumns(3);
        setRows(3);
        setWidth("100%");
        setMargin(true);
        setSpacing(true);

        Button btnCrsCalc = new Button("Submit");
        btnCrsCalc.addClickListener(event -> handleCalcAssgnButtonClick(event));
        addComponent(btnCrsCalc,2,0);
        setComponentAlignment(btnCrsCalc, Alignment.MIDDLE_RIGHT);

        //table to show information of current courses available
        tblCorsCnfg = new Table("Courses Available");
        tblCorsCnfg.setPageLength(10);
        tblCorsCnfg.setSelectable(true);
        tblCorsCnfg.setEditable(false);
        tblCorsCnfg.setHeight("100%");
        tblCorsCnfg.setWidth("100%");
        addComponent(tblCorsCnfg,0,1,2,1);

        Button btnAdd = new Button("Add Course", event -> handleAddCourseClick(event));
        Button btnRem = new Button("Remove Course", event -> handleRemoveCourseClick(event));
        addComponent(btnRem,1,2);
        addComponent(btnAdd,2,2);
        setComponentAlignment(tblCorsCnfg, Alignment.MIDDLE_CENTER);
        setComponentAlignment(btnAdd, Alignment.MIDDLE_CENTER);
        setComponentAlignment(btnRem, Alignment.MIDDLE_RIGHT);

        //initializeContainers();
        //initializeConfigTable();

    }

    private void initializeContainers() {
        //create the indexed containers for the given system semester
        // and default eventId
        semesCnfg = vaadinAdapter.getAdminConfigContainerForEventId(systemSettings.getSemester(), activeEventId);
    }

    private void initializeConfigTable() {
        tblCorsCnfg.setContainerDataSource(semesCnfg);
        tblCorsCnfg.setVisibleColumns("label", "capacity");
        //set the table column names
        for (String k : vaadinAdapter.COURSE_CONFIG_HEADERS.keySet() ) {
            tblCorsCnfg.setColumnHeader(k, vaadinAdapter.COURSE_CONFIG_HEADERS.get(k));
        }
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        //get the event id to load
        final String DELIMITER = ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)";

        //parse parameters coming from the navigation event
        String params = event.getParameters();
        String[] parts = params.split(DELIMITER);
        activeEventId = Long.parseLong(parts[0]);

        initializeContainers();
        initializeConfigTable();

    }

    @Override
    public void setApplicationEventPublisher (ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    private void handleAddCourseClick(Button.ClickEvent event) {

        Map<String, Course> courseChoices = getUnselectedCourses();

        //if no choices have been added do not show the course selection view
        // show notification that all courses available from the catalog have been added
        if (courseChoices.isEmpty()) {
            Notification.show("All Courses Added", "All courses available for this semester have been added.", Notification.Type.HUMANIZED_MESSAGE);
        } else {
            //open ui view to show list of remaining courses that are not
            // already added to the semester
            String crsList = "";
            for (String c : courseChoices.keySet()) {
                crsList += String.format("%s,", c);
            }
            String url = CourseSelectionView.NAME + "/" + CourseSelectedEvent.SelectionAction.ADD.toString() + "," + crsList;
            NavigationEvent navEvt = new NavigationEvent(this, url);
            this.publisher.publishEvent(navEvt);
        }
    }

    private void handleRemoveCourseClick(Button.ClickEvent event) {

        //get the currently selected table item if any
        Object selected_item = tblCorsCnfg.getValue();
        if ( selected_item == null ) {
            Notification.show("No Course Selected", "Please select a course to remove.", Notification.Type.HUMANIZED_MESSAGE);
        } else {
            tblCorsCnfg.removeItem(tblCorsCnfg.getValue());
        }

    }

    private void handleCalcAssgnButtonClick(Button.ClickEvent event) {
        try {
            vaadinAdapter.saveAdminSemesterConfig(semesCnfg);
        } catch(EventSerializationException e) {
            String msg = String.format("Exception was thrown with message %s", e.getMessage());
            Notification.show("Application Error", "Fatal error occurred when attempting to save the semester configuration.", Notification.Type.ERROR_MESSAGE);
            LogoutEvent loEvt = new LogoutEvent(this);
            this.publisher.publishEvent(loEvt);
        }

        SubmitForCalculationEvent sbmEvt = new SubmitForCalculationEvent(this);
        this.publisher.publishEvent(sbmEvt);

    }

    private Map<String, Course> getUnselectedCourses() {

        Map<String, Course> unselectedCourses = new HashMap<>();

        List<Integer> selectedCourses = new ArrayList<>();
        for (Object item_id : semesCnfg.getItemIds()) {
            selectedCourses.add( (Integer) semesCnfg.getContainerProperty(item_id, "course_catalog_id").getValue() );
        }

        //create list of course labels from full catalog that are already not
        // added to the semester configuration
        List<String> courseChoices = new ArrayList<>();
        List<Course> crsCtlg = courseRepository.getAllByFall(true);
        for(Course c : crsCtlg) {
            if ( !selectedCourses.contains(c.getId()) ) {
                String lbl = VaadinAdapter.createCourseLabel(c);
                unselectedCourses.put(lbl, c);
            }
        }

        return unselectedCourses;

    }

    @EventListener
    public void onApplicationEvent(CourseSelectedEvent event) {
        if ( ((CourseSelectionView)event.getSource()).getPreviousView().equals(this) ) {

            //get event info
            String selectedCourse = event.getCourse().toString();
            CourseSelectedEvent.SelectionAction action = event.getAction();

            //perform action on selected course
            switch (action) {
                case ADD:

                    //get all courses that can be added to the current semester configuration
                    Map<String, Course> availableCourses = getUnselectedCourses();

                    if (availableCourses.isEmpty()) {
                        String msg = String.format("List of courses available to be selected is empty, but the selected coures should exist.  selection: %s", selectedCourse);
                        throw new CourseSelectionForActionException(msg);
                    }

                    VaadinAdapter.addNewCourseSemesterItem(semesCnfg, availableCourses.get(selectedCourse), defaultCap);
                    break;

                case REMOVE:
                    //find course id with corresponding name in current semester configuration
                    Object course_id = null;
                    for (Object item_id : semesCnfg.getItemIds()) {
                        if (semesCnfg.getContainerProperty(item_id, "label").getValue().equals(selectedCourse)) {
                            course_id = semesCnfg.getItem(item_id);
                        }
                    }
                    if (course_id == null) {
                        String msg = String.format("Selected course to " +
                                "remove cannot be found in the semester configuration container. selection: %s", selectedCourse);
                        throw new CourseSelectionForActionException(msg);
                    }
                    semesCnfg.removeItem(course_id);
                    break;
            }



        }
    }

}
