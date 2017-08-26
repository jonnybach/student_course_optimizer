package edu.gatech.cs6310.agroup.service;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.TextField;
import edu.gatech.cs6310.agroup.eventmodel.*;
import edu.gatech.cs6310.agroup.exception.EventSerializationException;
import edu.gatech.cs6310.agroup.model.Course;
import edu.gatech.cs6310.agroup.model.EventLog;
import edu.gatech.cs6310.agroup.model.Semester;
import edu.gatech.cs6310.agroup.repository.CourseRepository;
import edu.gatech.cs6310.agroup.repository.EventLogRepository;
import edu.gatech.cs6310.agroup.ui.Broadcaster;
import edu.gatech.cs6310.agroup.uievent.LogoutEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * Created by jonathan on 4/13/16.
 *
 * Service Class for creating various Vaadin containers to support presenting information to the user
 * via Vaadin UI elements.  Extracts the event source based data from StateContainer and
 * creates the desired Vaadin containers.
 *
 */
@Service
public class VaadinAdapter implements ApplicationEventPublisherAware {

    private static final Logger log = LoggerFactory.getLogger(VaadinAdapter.class);

    public Map<String, String> EVENT_HIST_HEADERS;
    public Map<String, String> COURSE_CONFIG_HEADERS;
    public Map<String, String> STUDENT_PREFS_HEADERS;

    @Autowired
    private SystemSettings systemSettings;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EventLogRepository eventLogRepository;

    @Autowired
    private EventLogInsertService eventLogInsertService;

    @Autowired
    private EventLogStateService eventLogStateService;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Value("${default.class.cap.size}")
    private Integer defaultCap;

    private ApplicationEventPublisher publisher;

    //This is for debug purposes when not on a machine with Gurobi on it
    @Value("${call.gurobi.scheduler}")
    private boolean callGurobiScheduler;

    @PostConstruct
    public void init() {
        EVENT_HIST_HEADERS = new HashMap<>();
        COURSE_CONFIG_HEADERS = new HashMap<>();
        STUDENT_PREFS_HEADERS = new HashMap<>();
    }

    @Override
    public void setApplicationEventPublisher (ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public IndexedContainer getEventHistoryContainer(Semester semes) {

        //for now state does not contain anything so will create the indexed container
        // from the course repository for prototyping
        List<EventLog> logs = eventLogStateService.getSemesterEventLogsWithLimit(semes);

        //create container to hold the course configuration
        // setting for this semester
        IndexedContainer logCntr = new IndexedContainer();

        //add properties to describe course attributes
        logCntr.addContainerProperty("event_id", Long.class, 0);
        logCntr.addContainerProperty("date", Date.class, null);
        logCntr.addContainerProperty("num_students", Integer.class, 0);
        //logCntr.addContainerProperty("semester", String.class, "");
        logCntr.addContainerProperty("num_classes", Integer.class, 0);

        EVENT_HIST_HEADERS.put("event_id", "ID");
        EVENT_HIST_HEADERS.put("date", "Request Date");
        EVENT_HIST_HEADERS.put("num_students", "Student Count");
        EVENT_HIST_HEADERS.put("num_classes", "Class Count");

        for (EventLog el : logs) {
            Semester sem = el.getSemester();
            Object item_id = logCntr.addItem();
            Item item = logCntr.getItem(item_id);
            item.getItemProperty("event_id").setValue(el.getId());
            item.getItemProperty("date").setValue(el.getCreatedDate());

            try {
                StateContainer stateContainer = eventLogStateService.calculateState(sem, el.getId());
                item.getItemProperty("num_students").setValue(stateContainer.getStudentDemand().size());
                item.getItemProperty("num_classes").setValue(stateContainer.getCourses().size());
            } catch (EventSerializationException e) {
                log.error("Error getting number of students requesting courses", e);
            }
        }
        logCntr.sort(new Object[] {"date"}, new boolean[] {false} );

        return logCntr;
    }

    public IndexedContainer getAdminConfigContainerForEventId(Semester semes, Long eventId) {

        //create container to hold the course configuration
        // setting for this semester
        IndexedContainer crsCfgCntr = new IndexedContainer();

        //add properties to describe course attributes
        crsCfgCntr.addContainerProperty("course_catalog_id", Integer.class, 0);
        crsCfgCntr.addContainerProperty("name", String.class, "");
        crsCfgCntr.addContainerProperty("number", String.class, "");
        crsCfgCntr.addContainerProperty("label", String.class, "");
        crsCfgCntr.addContainerProperty("assgn_professor", String.class, "");
        crsCfgCntr.addContainerProperty("capacity", TextField.class, null);

        COURSE_CONFIG_HEADERS.put("course_catalog_id", "Catalog Id");
        COURSE_CONFIG_HEADERS.put("name", "Name");
        COURSE_CONFIG_HEADERS.put("number", "Number");
        COURSE_CONFIG_HEADERS.put("label", "Course Name");
        COURSE_CONFIG_HEADERS.put("assgn_professor", "Professor");
        COURSE_CONFIG_HEADERS.put("capacity", "Class Cap");

        // get this students preferences for the given event history id
        // and get the list of courses that were set to be available
        // for this event
        Set<CourseEvent> crsEvnts = null;
        if (eventId >= 0) {
            StateContainer thisState = null;
            try {
                thisState = eventLogStateService.calculateState(semes, eventId);
            } catch (EventSerializationException e) {
                log.error("Error getting state for event id.", e);
                LogoutEvent loEvt = new LogoutEvent(this);
                this.publisher.publishEvent(loEvt);
            }
            crsEvnts = thisState.getCourses();
        }

        //as a fail safe if an event does not have even a listing of courses
        // to populate then use the full catalog for this semester
        if ((crsEvnts == null) || crsEvnts.isEmpty()) {

            log.warn("There was an issue with getting courses events from the event log. Thus a list of courses" +
                    "available for the admin to choose from a prior calc does not exist.  Creating a default list of" +
                    "available courses from the course catalog for the active semester.  Either a course assignment has not" +
                    "yet been submitted from admin mode or this is a bug.");

            List<Course> crs = null;
            switch (systemSettings.getSemester().getTerm()) {
                case FALL:
                    crs = courseRepository.getAllByFall(true);
                    break;
                case SPRING:
                    crs = courseRepository.getAllBySpring(true);
                    break;
                case SUMMER:
                    crs = courseRepository.getAllBySummer(true);
                    break;
            }

            for (Course c : crs) {
                addNewCourseSemesterItem(crsCfgCntr, c, defaultCap);
            }

        } else {
            //compile list of courses to add from the courses
            // that were set to be available for this event
            for (CourseEvent ce : crsEvnts) {
                Course c = courseRepository.findOne(ce.getCourseId());
                addNewCourseSemesterItem(crsCfgCntr, c, ce.getMaxSize());
            }
        }
        crsCfgCntr.sort(new Object[] {"number"}, new boolean[] {true} );

        return crsCfgCntr;

    }

    public IndexedContainer getStudentPrefsContainerFromState(Integer studentId, Semester semes, Long eventId) {

        IndexedContainer prefsCntr = new IndexedContainer();

        //add properties to describe course attributes
        //crsCfgCntr.addContainerProperty("id", Integer.class, "");
        prefsCntr.addContainerProperty("course_catalog_id", Integer.class, "");
        prefsCntr.addContainerProperty("name", String.class, "");
        prefsCntr.addContainerProperty("number", String.class, "");
        prefsCntr.addContainerProperty("label", String.class, "");
        prefsCntr.addContainerProperty("selected", Boolean.class, Boolean.FALSE);
        prefsCntr.addContainerProperty("priority", Integer.class, "0");
        prefsCntr.addContainerProperty("capacity", Integer.class, "0");

        STUDENT_PREFS_HEADERS.put("course_catalog_id", "Catalog Id");
        STUDENT_PREFS_HEADERS.put("name", "Name");
        STUDENT_PREFS_HEADERS.put("number", "Number");
        STUDENT_PREFS_HEADERS.put("label", "Course Name");
        STUDENT_PREFS_HEADERS.put("selected", "Course Selected");
        STUDENT_PREFS_HEADERS.put("priority", "Priority");
        STUDENT_PREFS_HEADERS.put("capacity", "Capacity");

        // get this students preferences for the given event history id
        // and get the list of courses that were set to be available
        // for this event
        Set<StudentDemandCourse> studPrefs = null;
        Set<CourseEvent> crsEvnts = null;
        if (eventId >= 0) {
            StateContainer thisState = null;
            try {
                thisState = eventLogStateService.calculateState(semes, eventId);
            } catch (EventSerializationException e) {
                log.error("Error getting state for event id.", e);
                LogoutEvent loEvt = new LogoutEvent(this);
                this.publisher.publishEvent(loEvt);
            }
            Map<Integer, Set<StudentDemandCourse>> obj = thisState.getStudentDemand();
            studPrefs = thisState.getStudentDemand().get(studentId);
            crsEvnts = thisState.getCourses();
        }

        //as a fail safe if an event does not have even a listing of courses
        // to populate then use the full catalog for this semester
        if ((crsEvnts == null) || crsEvnts.isEmpty()) {

            log.warn("There was an issue with getting courses events from the event log. Thus a list of courses" +
                    "available for the student to choose from a prior calc does not exist.  Creating a default list of" +
                    "available courses from the course catalog for the active semester.  Either a course assignment has not" +
                    "yet been submitted from admin mode or this is a bug.");

            List<Course> crs = null;
            switch (systemSettings.getSemester().getTerm()) {
                case FALL:
                    crs = courseRepository.getAllByFall(true);
                    break;
                case SPRING:
                    crs = courseRepository.getAllBySpring(true);
                    break;
                case SUMMER:
                    crs = courseRepository.getAllBySummer(true);
                    break;
            }

            for (Course c : crs) {
                addNewStudentPrefItem(prefsCntr, c, 0, -1);
            }

        } else {
            //compile list of courses to add from the courses
            // that were set to be available for this event
            for (CourseEvent ce : crsEvnts) {

                Course c = courseRepository.findOne(ce.getCourseId());

                Integer prior = 0;
                if (studPrefs != null) {
                    for (StudentDemandCourse sdc : studPrefs) {
                        if (sdc.getCourseId() == c.getId()) {
                            prior = sdc.getPriority();
                        }
                    }
                }
                addNewStudentPrefItem(prefsCntr, c, prior, ce.getMaxSize());
            }
        }
        prefsCntr.sort(new Object[] {"number"}, new boolean[] {true});

        return prefsCntr;

    }

    public IndexedContainer getCourseBasedResultsContainerForEventId(Semester semes, Long eventId) {

        StateContainer thisState = null;
        try {
            thisState = eventLogStateService.calculateState(semes, eventId);
        } catch (EventSerializationException e) {

        }
        Set<CourseEvent> crsEnvts = thisState.getCourses();

        //create container to hold the course configuration
        // setting for this semester
        IndexedContainer crsRslts = new IndexedContainer();

        //add properties to describe course attributes
        crsRslts.addContainerProperty("course_catalog_id", Integer.class, 0);
        crsRslts.addContainerProperty("name", String.class, "");
        crsRslts.addContainerProperty("number", String.class, "");
        crsRslts.addContainerProperty("label", String.class, "");
        crsRslts.addContainerProperty("assgn_professor", String.class, "");
        crsRslts.addContainerProperty("capacity", Integer.class, null);
        crsRslts.addContainerProperty("demand", Integer.class, null);
        for (Integer i = 0; i < systemSettings.getCoursePreferenceLimit(); i++) {
            crsRslts.addContainerProperty(String.format("demand_prior%d",i), Integer.class, null);
        }
        crsRslts.addContainerProperty("stud_assigned", Integer.class, null);

        COURSE_CONFIG_HEADERS.put("course_catalog_id", "Catalog Id");
        COURSE_CONFIG_HEADERS.put("name", "Name");
        COURSE_CONFIG_HEADERS.put("number", "Number");
        COURSE_CONFIG_HEADERS.put("label", "Course Name");
        COURSE_CONFIG_HEADERS.put("assgn_professor", "Professor");
        COURSE_CONFIG_HEADERS.put("capacity", "Class Cap");
        COURSE_CONFIG_HEADERS.put("demand", "Student Demand");
        for (Integer i = 0; i < systemSettings.getCoursePreferenceLimit(); i++) {
            COURSE_CONFIG_HEADERS.put(String.format("demand_prior%d",i), String.format("Demand Prior %d", i));
        }
        COURSE_CONFIG_HEADERS.put("stud_assigned", "Students Assigned");

        //TODO - update this for loop not to loop over course catalog but only the available course for this semester
//        for (Course c : crsClg) {
//            addNewCourseSemesterItem(crsRslts, c);
//        }
//        crsRslts.sort(new Object[] {"number"}, new boolean[] {true} );

        return crsRslts;

    }

    public void saveStudentPrefs(IndexedContainer semesConfig) throws EventSerializationException {

        //create container instance
        //search course semester config indexed container and find all entries with selected
        // for each create a new StudentDemandCourse instance, set the priority, add to container
        // call insertStudentDemandEvent
        //set up message queue to run calculation

        //check that at least one course has been selected

        StudentDemandEventContainer stdDemCntr = new StudentDemandEventContainer(CurrentUser.getId());
        log.debug("Saving student preferences for student ID [{}]", CurrentUser.getId());

        for (Iterator i = semesConfig.getItemIds().iterator(); i.hasNext();) {
            // Get the current item identifier, which is an integer.
            int iid = (Integer) i.next();

            // get the actual item from the table.
            Item item = semesConfig.getItem(iid);
            Boolean selected = (Boolean) item.getItemProperty("selected").getValue();
            if (selected) {
                //create new student demand course instance
                Integer crsId = (Integer) item.getItemProperty("course_catalog_id").getValue();
                Integer prio = (Integer) item.getItemProperty("priority").getValue();
                StudentDemandCourse stdDem = new StudentDemandCourse(crsId, prio);
                stdDemCntr.addStudentDemandCourse(stdDem);
            }
        }

        EventLog eventLog = eventLogInsertService.insertStudentDemandEvent(stdDemCntr, systemSettings.getSemester(), false);
        requestScheduleCalculation(eventLog);

    }

    /**
     * Method to call the correct message queue to request a schedule calculation
     *
     * @param eventLog
     */
    private void requestScheduleCalculation(EventLog eventLog) {

        //Put message on queue for event schedule calculation if we are using Gurobi
        if (callGurobiScheduler) { //Normal case, get schedule working
            jmsTemplate.convertAndSend("cs6310.project3.calculate.schedule", String.valueOf(eventLog.getId()));
        } else { //Just send to the result queue directly for test purposes (make it look like there was a result calculated)
            eventLog.setResultCalculated(true);
            eventLogRepository.save(eventLog);
            jmsTemplate.convertAndSend("cs6310.project3.scheduled", String.valueOf(eventLog.getId()));
        }
    }

    public void saveAdminSemesterConfig(IndexedContainer semesConfig) throws EventSerializationException{

        //create container instance
        //for all items in container
        // for each create a new CourseEvent instance, set the max class size, add to container
        // call insertCoursEvent
        //set up message queue to run calculation

        CourseEventContainer crsCntr = new CourseEventContainer();

        for (Iterator i = semesConfig.getItemIds().iterator(); i.hasNext();) {
            // Get the current item identifier, which is an integer.
            int iid = (Integer) i.next();

            // Now get the actual item from the table.
            Item item = semesConfig.getItem(iid);
            Integer crsId = (Integer) item.getItemProperty("course_catalog_id").getValue();
            Object capObj = item.getItemProperty("capacity").getValue();
            String cap = ((TextField)capObj).getValue();

            //create new student demand course instance
            CourseEvent crsEvt = new CourseEvent(crsId, new Integer(cap) );
            crsCntr.addCourseEvent(crsEvt);
        }


        EventLog eventLog = eventLogInsertService.insertCourseEvents(crsCntr, systemSettings.getSemester(), false);
        requestScheduleCalculation(eventLog);
    }

    public static void addNewStudentPrefItem(IndexedContainer contr, Course c, Integer prior, Integer cap) {

        Boolean selected = (prior > 0);

        Object item_id = contr.addItem();
        Item item = contr.getItem(item_id);
        item.getItemProperty("course_catalog_id").setValue(c.getId());
        item.getItemProperty("name").setValue(c.getName());
        item.getItemProperty("number").setValue(c.getNumber());
        item.getItemProperty("label").setValue( createCourseLabel(c) );
        item.getItemProperty("priority").setValue(prior);
        item.getItemProperty("selected").setValue(selected);
        item.getItemProperty("capacity").setValue(cap);
        contr.sort(new Object[] {"number"}, new boolean[] {true});
    }

    public static void addNewCourseSemesterItem(IndexedContainer contr, Course c, Integer capacity) {

        //setup the text field for capacity input
        TextField cap = new TextField();
        cap.setValue(String.valueOf(capacity));

        Object item_id = contr.addItem();
        Item item = contr.getItem(item_id);
        item.getItemProperty("course_catalog_id").setValue(c.getId());
        item.getItemProperty("name").setValue(c.getName());
        item.getItemProperty("number").setValue(c.getNumber());
        item.getItemProperty("label").setValue( createCourseLabel(c) );
        item.getItemProperty("assgn_professor").setValue( String.format("Rambler Wreck") );
        item.getItemProperty("capacity").setValue( cap );
        contr.sort(new Object[] {"number"}, new boolean[] {true});
    }

    public static String createCourseLabel(Course c) {
        return String.format("CS%s %s", c.getNumber(), c.getName());
    }

    @JmsListener(destination = "cs6310.project3.scheduled")
    public void receiveScheduleCalculationResult(String eventLogIdMsg) {

        log.debug("Received a schedule result calculation completed JMS message with eventLogId [{}]", eventLogIdMsg);

        long eventLogId = Long.parseLong(eventLogIdMsg);

        Broadcaster.broadcast(eventLogId);
    }

    public void updateEventHistoryContainer(IndexedContainer indexedContainer, long eventLogId) throws EventSerializationException {
        EventLog eventLog = eventLogRepository.findOne(eventLogId);

        Object itemId = indexedContainer.addItem();
        Item item = indexedContainer.getItem(itemId);
        item.getItemProperty("event_id").setValue(eventLogId);
        item.getItemProperty("date").setValue(eventLog.getCreatedDate());

        StateContainer stateContainer = eventLogStateService.calculateState(systemSettings.getSemester(), eventLogId);
        item.getItemProperty("num_students").setValue(stateContainer.getStudentDemand().size());
        item.getItemProperty("num_classes").setValue(stateContainer.getCourses().size());
    }

}
