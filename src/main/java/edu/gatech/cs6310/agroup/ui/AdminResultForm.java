package edu.gatech.cs6310.agroup.ui;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import edu.gatech.cs6310.agroup.eventmodel.StateContainer;
import edu.gatech.cs6310.agroup.eventmodel.StudentDemandCourse;
import edu.gatech.cs6310.agroup.model.Course;
import edu.gatech.cs6310.agroup.model.EventLog;
import edu.gatech.cs6310.agroup.model.StudentScheduleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by tim on 4/23/16.
 */
public class AdminResultForm extends FormLayout {

    private static final Logger log = LoggerFactory.getLogger(AdminResultForm.class);
    private EventLog eventLog;
    private StateContainer state;
    private List<StudentScheduleResult> schedule;
    private List<Course> courses;

    public AdminResultForm(EventLog eventLog, StateContainer state, List<StudentScheduleResult> schedule, List<Course> courses) {
        super();
        this.eventLog = eventLog;
        this.state = state;
        this.schedule = schedule;
        this.courses = courses;
        buildForm();
    }

    //@PostConstruct
    public void buildForm() {
        //setSizeFull();
        setSpacing(true);
        setMargin(true);

        if (eventLog == null) {
            Notification.show("Event Log null",
                    "Can not build form with a null event log object",
                    Notification.Type.ERROR_MESSAGE);
            return;
        }

        final TextField tf1 = new TextField("Event ID");
        tf1.setValue(String.valueOf(eventLog.getId()));
        addComponent(tf1);

        final TextField tf2 = new TextField("Created Date");
        tf2.setValue(eventLog.getCreatedDate().toString());
        addComponent(tf2);

        final TextField tf3 = new TextField("Semester");
        tf3.setValue(eventLog.getSemester().getName());
        addComponent(tf3);

        final TextField tf4 = new TextField("Semester Start Date");
        tf4.setValue(eventLog.getSemester().getStartDate().toString());
        addComponent(tf4);

        final TextField tf5 = new TextField("Semester End Date");
        tf5.setValue(eventLog.getSemester().getStartDate().toString());
        addComponent(tf5);

        Table demandTable = new Table("Student Demands");
        demandTable.addContainerProperty("Student ID", Integer.class, null);
        demandTable.addContainerProperty("Course Demands (Priority. Course Number, ...)", String.class, null);
        Map<Integer, Course> courseMap;

        if (state == null) {
            Notification.show("State container is null",
                    "Can't list student demands",
                    Notification.Type.WARNING_MESSAGE);
            return;
        }

        courseMap = new HashMap<>();
        for (Course c : courses) {
            courseMap.put(c.getId(), c);
        }

        Map<Integer, Set<StudentDemandCourse>> demands = state.getStudentDemand();
        int numStudents = demands.size();
        Map<Integer, Integer> studentIndex = new HashMap<>();
        int i = -1;

        for (Integer studentId : demands.keySet()) {
            ++i;
            studentIndex.put(studentId, i);
            StringBuffer demandString = new StringBuffer();

            for (StudentDemandCourse demand : demands.get(studentId)) {
                if (demandString.length() > 0) {
                    demandString.append(", ");
                }
                demandString.append(String.format("%d. CS%s", demand.getPriority(), courseMap.get(demand.getCourseId()).getNumber()));
            }

            demandTable.addItem(new Object[]{studentId, demandString.toString()}, studentId);
        }

        int pageLength = demandTable.size() < 15 ? demandTable.size() : 15;
        demandTable.setPageLength(pageLength);
        //demandTable.setPageLength(numStudents);
        addComponent(demandTable);

        if (schedule == null) {
            Notification.show("Schedule null",
                    "Can not build form with a null suggested course schedule",
                    Notification.Type.ERROR_MESSAGE);
            return;
        }

        Table suggestedTable = new Table("Suggested Schedule");
        suggestedTable.addContainerProperty("Student ID", Integer.class, null);
        //suggestedTable.setColumnAlignments(Table.Align.CENTER);
        Map<Integer, Integer> courseIndex = new HashMap<>();
        i = 0; // start index at 1, student ID is index 0
        for (Course c : courses) {
            if (c.availableInSemester(eventLog.getSemester().getId())) {
                ++i;
                courseIndex.put(c.getId(), i);
                //suggestedTable.addContainerProperty("CS" + c.getNumber(), Image.class, null);
                suggestedTable.addContainerProperty("CS" + c.getNumber(), Label.class, null);
            }
        }

        /*Map<Integer, Boolean> uniqueCourses = new HashMap<>();
        for (StudentScheduleResult ssr : schedule) {
            uniqueCourses.put(ssr.getCourse().getId(), true);
        }*/

        int numCols = i + 1;
        //log.debug("Creating suggested schedule table => numStudents={}, numCols={}", numStudents, numCols);
        final Object[][] tableContents = new Object[numStudents][numCols];

        for (StudentScheduleResult sr : schedule) {
            int r = studentIndex.get(sr.getStudent().getId());
            int c = courseIndex.get(sr.getCourse().getId());
            //log.debug("Scheduled course => studentId={}, courseId={}, r={}, c={}", sr.getStudent().getId(), sr.getCourse().getId(), r, c);
            //tableContents[r][c] = new Image("", new ClassResource("/images/agt_action_success_22.png"));
            tableContents[r][c] = new Label("&#x2714;", ContentMode.HTML);
        }

        int row = -1;
        for (Integer studentId : demands.keySet()) {
            ++row;
            tableContents[row][0] = studentId;
            for (int c = 1; c < numCols; c++) {
                if (tableContents[row][c] == null) {
                    tableContents[row][c] = new Label("");
                }
                //tableContents[row][c] = new Image("", new ClassResource("/images/agt_action_fail_22.png"));
                //tableContents[row][c] = "";
            }
        }

        for (int r = 0; r < numStudents; r++) {
            //log.debug("Adding table item => r={}, studentId={}", r, tableContents[r][0]);

            /*final Object[] rowData = new Object[numCols];
            for (int x = 0; x < numCols; x++) {
                rowData[x] = tableContents[r][x];
            }
            suggestedTable.addItem(rowData, rowData[r]);*/

            suggestedTable.addItem(tableContents[r], tableContents[r][0]);

            //log.debug("Sleeping for 3 seconds! Checking for thread safety issues");
            //try { Thread.sleep(3000); } catch (Exception e) { }
        }

        //log.debug("Table size => {}", suggestedTable.size());

        pageLength = suggestedTable.size() < 15 ? suggestedTable.size() : 15;
        suggestedTable.setPageLength(pageLength);
        //suggestedTable.setPageLength(numStudents);
        addComponent(suggestedTable);
    }
}

/*
Courses ...
     0 => [2] 6250 Computer Networks
     1 => [3] 6300 Software Development Process
     2 => [4] 7641 Machine Learning
     3 => [5] 6290 High Performance Computer Architecture
     4 => [6] 6310 Software Architecture and Design
     5 => [8] 6505 Computability, Complexity and Algorithms
     6 => [9] 7637 Knowledge-Based Artificial Intelligence, Cognitive Systems
     7 => [10] 4495 Computer Vision
     8 => [12] 8803-002 Introduction to Operating Systems
     9 => [13] 8803-001 Artificial Intelligence for Robotics
    10 => [14] 6035 Introduction to Information Security
    11 => [16] 7646 Machine Learning for Trading
    12 => [18] 8803 Special Topics: Big Data

Student demands (studentID => courseId:priority ) ...
  1 => 18:3 5:1 9:2
  2 => 7:3 13:2 15:1
  3 => 17:2 9:1 10:4 11:3
  4 => 9:1
  5 => 3:3 6:1 8:4 10:5 15:2
  6 => 1:3 6:2 15:1
  7 => 5:4 7:2 10:1 11:5 15:3
  8 => 11:1
  9 => 17:3 5:2 15:1
 10 => 18:1

Suggested course schedule for Spring Year 1 semester (rows=students, cols=courses) ...
        0  1  2  3  4  5  6  7  8  9 10 11 12
        2  3  4  5  6  8  9 10 12 13 14 16 18
  1 =>  .  .  .  X  .  .  X  .  .  .  .  .  .
  2 =>  .  .  .  .  .  .  .  .  .  X  .  .  .
  3 =>  .  .  .  .  .  .  X  X  .  .  .  .  .
  4 =>  .  .  .  .  .  .  X  .  .  .  .  .  .
  5 =>  .  X  .  .  X  .  .  .  .  .  .  .  .
  6 =>  .  .  .  .  X  .  .  .  .  .  .  .  .
  7 =>  .  .  .  X  .  .  .  X  .  .  .  .  .
  8 =>  .  .  .  .  .  .  .  .  .  .  .  .  .
  9 =>  .  .  .  X  .  .  .  .  .  .  .  .  .
 10 =>  .  .  .  .  .  .  .  .  .  .  .  .  X
 */
