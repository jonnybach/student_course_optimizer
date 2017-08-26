package edu.gatech.cs6310.agroup.ui;

import com.vaadin.annotations.Push;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.navigator.Navigator;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.*;
import edu.gatech.cs6310.agroup.eventmodel.StateContainer;
import edu.gatech.cs6310.agroup.eventmodel.StudentDemandCourse;
import edu.gatech.cs6310.agroup.eventmodel.StudentDemandEventContainer;
import edu.gatech.cs6310.agroup.exception.EventSerializationException;
import edu.gatech.cs6310.agroup.model.*;
import edu.gatech.cs6310.agroup.repository.*;
import edu.gatech.cs6310.agroup.service.*;
import edu.gatech.cs6310.agroup.uievent.LogoutEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@UIScope
@SpringComponent(MainView.NAME)
@Push
public class MainView extends VerticalLayout implements ApplicationEventPublisherAware, Broadcaster.BroadcastListener {

    public static final String NAME = "main_view";
    private static final Logger log = LoggerFactory.getLogger(MainView.class);

    @Autowired
    private SpringViewProvider viewProvider;

    @Autowired
    private SystemSettings systemSettings;

    @Autowired
    private VaadinAdapter vaadinAdapter;

    @Autowired
    private EventLogInsertService eventLogInsertService;

    @Autowired
    private EventLogRepository eventLogRepository;

    @Autowired
    private StudentScheduleResultRepository studentScheduleResultRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private SerializableEventService serializableEventService;

    @Autowired
    private EventLogStateService eventLogStateService;

    private IndexedContainer eventHstry;
    private Table tblHsty;

    private ApplicationEventPublisher publisher;
    private Navigator nav;
    private Panel mainPnl;
    private HorizontalSplitPanel split1;
    private Label userLab;
    private Label roleLab;
    private Label semesterLab;
    private TabSheet tabs;
    private TabSheet.Tab coursesTab;
    private List<TabSheet.Tab> resultTabs;
    private static final DateFormat dateFormatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

    public MainView() {
        //configure main view settings
        addMenuBar();

        VerticalLayout hstyLayout = getHistoryComponentsToLayout();
        hstyLayout.setSizeFull();

        mainPnl = new Panel();
        mainPnl.setSizeFull();

        tabs = new TabSheet();
        tabs.setSizeFull();
        coursesTab = tabs.addTab(mainPnl, "Courses");
        resultTabs = new ArrayList<>();

        split1 = new HorizontalSplitPanel();
        split1.setSizeFull();
        split1.setSplitPosition(40, Unit.PERCENTAGE);
        split1.setFirstComponent(hstyLayout);
        split1.setSecondComponent(tabs);
        addComponent(split1);
    }

    private void addMenuBar() {

        //create components to add to menu bar
        userLab = new Label();
        roleLab = new Label();
        semesterLab = new Label();
        Button btnLogout = new Button("Logout");
        btnLogout.addClickListener(event -> handleLogoutButtonClick(event));

        //create grid layout and add components
        GridLayout menuBar = new GridLayout(5,1);
        menuBar.addComponent(userLab, 0, 0);
        menuBar.addComponent(roleLab, 1, 0);
        menuBar.addComponent(semesterLab, 2, 0);
        menuBar.setColumnExpandRatio(0, 0.33f);
        menuBar.setColumnExpandRatio(1, 0.33f);
        menuBar.setColumnExpandRatio(2, 0.33f);
        menuBar.addComponent(btnLogout, 4, 0);
        menuBar.setSpacing(true);
        menuBar.setMargin(true);
        menuBar.setWidth("100%");
        addComponent(menuBar);
    }

    private VerticalLayout getHistoryComponentsToLayout() {

        //create main vertical layout for the table and button
        VerticalLayout hstyLayout = new VerticalLayout();
        hstyLayout.setSpacing(true);
        hstyLayout.setMargin(true);
        hstyLayout.setHeight(100, Unit.PERCENTAGE);

        //create event history table
        tblHsty = new Table();
        tblHsty.setCaption("Suggested Course Assignment History");
        tblHsty.setPageLength(10);
        tblHsty.setSelectable(true);
        tblHsty.setEditable(false);
        tblHsty.setSizeFull();
        tblHsty.setImmediate(true);
        hstyLayout.addComponent(tblHsty);

        Button btnApplyEvent = new Button("View Request");
        btnApplyEvent.addClickListener(event -> handleApplyButtonClick(event));

        Button viewResults = new Button("View Results");
        viewResults.addClickListener(event -> handleViewResultsButtonClient(event));

        Button genRandomDemand = new Button("Generate Test Events");
        genRandomDemand.addClickListener(event -> handleGenerateRandomButtonClick(event));

        Button clearResultTabs = new Button("Clear Results Tabs");
        clearResultTabs.addClickListener(event -> clearResultTabsButtonClient(event));

        HorizontalLayout btnLyt = new HorizontalLayout(btnApplyEvent, viewResults);
        hstyLayout.addComponent(btnLyt);
        hstyLayout.setComponentAlignment(btnLyt, Alignment.MIDDLE_CENTER);

        HorizontalLayout btnLyt2 = new HorizontalLayout(clearResultTabs, genRandomDemand);
        hstyLayout.addComponent(btnLyt2);
        hstyLayout.setComponentAlignment(btnLyt2, Alignment.MIDDLE_CENTER);

        return hstyLayout;
    }

    public void initialize() {
        initializeContainers();
        userLab.setValue( String.format("User: %s", CurrentUser.getName()) );
        roleLab.setValue( String.format("Role: %s", CurrentUser.getType().toString()) );
        semesterLab.setValue( String.format("Semester: %s", systemSettings.getSemesterName()) );
        initializeEventTable();
        setupNavigator();

        Broadcaster.register(this);
    }

    // Must also unregister the boradcaster when the UI expires
    @Override
    public void detach() {
        Broadcaster.unregister(this);
        super.detach();
    }

    private void initializeContainers() {
        eventHstry = vaadinAdapter.getEventHistoryContainer(systemSettings.getSemester());
    }

    private void initializeEventTable() {
        tblHsty.setContainerDataSource(eventHstry);
        tblHsty.setVisibleColumns("event_id", "date", "num_students", "num_classes");
        //set the table column names
        for (String k : vaadinAdapter.EVENT_HIST_HEADERS.keySet() ) {
            tblHsty.setColumnHeader(k, vaadinAdapter.EVENT_HIST_HEADERS.get(k));
        }
        //tblHsty.select(tblHsty.firstItemId());
        tblHsty.addItemClickListener(event -> handleHistoryItemClicked(event));
    }

    private void setupNavigator() {
        nav = new Navigator(MainUI.getCurrent(), mainPnl);
        nav.addProvider(viewProvider); //adding a spring view provider registers any @SpringViews to be used to navigate to

        //load latest event
        long activeEventId = -1L;
        if (eventHstry.size() > 0) {
            activeEventId = (Long) eventHstry.getContainerProperty(eventHstry.firstItemId(), "event_id").getValue();
        }

        navigateToMainView(activeEventId);
    }

    private void navigateToMainView(long eventId) {
        if (CurrentUser.getType() == CurrentUser.UserType.ADMIN) {
            String url = AdminSemesterConfigView.NAME + "/" + String.format("%s", eventId);
            nav.navigateTo(url);
        } else if (CurrentUser.getType() == CurrentUser.UserType.STUDENT) {
            String url = StudentPreferencesView.NAME + "/" + String.format("%s", eventId);
            nav.navigateTo(url);
        }
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    private void handleApplyButtonClick(Button.ClickEvent event) {

        Object hsty = tblHsty.getValue();
        if (hsty != null) {
            long activeEventId = (Long) eventHstry.getContainerProperty(hsty, "event_id").getValue();
            navigateToMainView(activeEventId);
        } else {
            Notification.show("No event selected.",
                    "Please select an item from the assignment history table to apply that event's configuration settings.",
                    Notification.Type.HUMANIZED_MESSAGE);
        }
    }

    private void handleLogoutButtonClick(Button.ClickEvent event) {
        LogoutEvent loEvt = new LogoutEvent(this);
        publisher.publishEvent(loEvt);
    }

    private void handleGenerateRandomButtonClick(Button.ClickEvent event) {
        log.debug("Handling generate random button click.");

        //Generate some random course events
        Semester semester = systemSettings.getSemester();
        try {
            eventLogInsertService.generateRandomDataForSemester(semester);
        } catch (EventSerializationException e) {
            log.error("Could not insert random student/course data", e);
        }

    }

    private void clearResultTabsButtonClient(Button.ClickEvent event) {
        for (int t = 0; t < resultTabs.size(); t++) {
            TabSheet.Tab tab = resultTabs.get(t);
            if (tab != null) {
                tabs.removeTab(tab);
            }
        }

        resultTabs.clear();
    }

    private Item selectedHistoryItem = null;

    private void handleHistoryItemClicked(ItemClickEvent event) {
        selectedHistoryItem = event.getItem();
    }
    private void handleViewResultsButtonClient(Button.ClickEvent event) {
        if (selectedHistoryItem == null) {
            return;
        }

        Item item = selectedHistoryItem;
        long eventId = (Long) item.getItemProperty("event_id").getValue();
        Date date = (Date) item.getItemProperty("date").getValue();
        int numStudents = (Integer) item.getItemProperty("num_students").getValue();
        CurrentUser.UserType userType = CurrentUser.getType();

        log.info("Showing results for suggested course schedule history => id={}, date={}, numStudents={}, userType={}", eventId, date, numStudents, userType.name());

        AbstractComponent resultContents = null;
        List<Course> courses = courseRepository.findAll();

        if (CurrentUser.getType() == CurrentUser.UserType.ADMIN) {
            /*resultContents = new Panel();
            resultContents.setSizeFull();
            Label resultsLabel = new Label(String.format("Got results => id=%d, date=%s, numStudents=%d, userType=%s", eventId, date, numStudents, userType.name()));
            ((Panel) resultContents).setContent(resultsLabel);*/

            EventLog eventLog = eventLogRepository.findOne(eventId);
            List<StudentScheduleResult> schedule = studentScheduleResultRepository.getAllStudentScheduleResultsForEventLog(eventLog);
            StateContainer state = null;
            try {
                state = eventLogStateService.calculateState(eventLog.getSemester(), eventLog.getId());
            } catch (EventSerializationException e) {
                log.error("Failed to obtain state for event with ID => {}", eventId);
            }
            resultContents = new AdminResultForm(eventLog, state, schedule, courses);

        } else if (CurrentUser.getType() == CurrentUser.UserType.STUDENT) {
            EventLog eventLog = eventLogRepository.findOne(eventId);
            Student student = studentRepository.findOne(CurrentUser.getId());
            //System.out.println(eventLog.getEventData());

            // get student demands, which will be in an latest event log up to the current event log for this student
            List<EventLog> logHistory =  eventLogRepository.getEventLogsBySemesterUpToEventLogId(eventLog.getSemester(), eventId);
            Set<StudentDemandCourse> studentDemands = null;

            for (int h = logHistory.size() - 1; h >= 0; h--) {
                EventLog el = logHistory.get(h);
                if (el.getEventLogType().getId() == EventLogType.EVENT_LOG_TYPE.STUDENT_DEMAND_ADDED.getEventLogTypeId()) {
                    try {
                        StudentDemandEventContainer demandEvent = serializableEventService.deserialize(el.getEventData(), StudentDemandEventContainer.class);

                        if (demandEvent.getStudentId() == CurrentUser.getId()) {
                            studentDemands = demandEvent.getStudentDemandCourseEvents();
                            break;
                        }

                    } catch (EventSerializationException e) {
                        log.error("Failed to deserialize student demand event container => {}", el.getEventData());
                    }
                }
            }

            List<StudentScheduleResult> schedule = studentScheduleResultRepository.getStudentScheduleResultsForEventLog(eventLog, student);
            resultContents = new StudentResultForm(eventLog, schedule, studentDemands, courses);
        }

        TabSheet.Tab resultTab = tabs.addTab(resultContents, String.format("[%d] %s", eventId, dateFormatter.format(date)));
        resultTab.setClosable(true);

        resultTabs.add(resultTab);
        tabs.setSelectedTab(resultTab);
    }

    @Override
    public void receiveBroadcast(long eventLogId) {
        log.debug("Received broadcast of update for eventLogId [{}]", eventLogId);

        //Update the table results
        getUI().access(() -> {
            Notification.show("Course Assignment Calculation Job Received!", Notification.Type.TRAY_NOTIFICATION);

            log.debug("Updating container to add eventLogId [{}]", eventLogId);
            IndexedContainer container = (IndexedContainer) tblHsty.getContainerDataSource();
            try {
                vaadinAdapter.updateEventHistoryContainer(container, eventLogId);
            } catch (EventSerializationException e) {
                log.error("Could not update event history container", e);
            }

            container.sort(new Object[]{"date"}, new boolean[]{false});
            //tblHsty.select(tblHsty.firstItemId());

        });
    }
}
