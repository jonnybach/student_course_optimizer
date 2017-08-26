package edu.gatech.cs6310.agroup.ui;

import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import edu.gatech.cs6310.agroup.eventmodel.StudentDemandCourse;
import edu.gatech.cs6310.agroup.model.Course;
import edu.gatech.cs6310.agroup.model.EventLog;
import edu.gatech.cs6310.agroup.model.StudentScheduleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by tim on 4/23/16.
 */
public class StudentResultForm extends FormLayout {

    //private static final Logger log = LoggerFactory.getLogger(StudentResultForm.class);
    private EventLog eventLog;
    private List<StudentScheduleResult> schedule;
    private Set<StudentDemandCourse> studentDemands;
    private List<Course> courses;

    public StudentResultForm(EventLog eventLog, List<StudentScheduleResult> schedule, Set<StudentDemandCourse> studentDemands, List<Course> courses) {
        super();
        this.eventLog = eventLog;
        this.schedule = schedule;
        this.studentDemands = studentDemands;
        this.courses = courses;
        buildForm();
    }

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

        Table requestedTable = new Table("Requested Courses");
        requestedTable.addContainerProperty("Priority", Integer.class, null);
        requestedTable.addContainerProperty("ID", Integer.class, null);
        requestedTable.addContainerProperty("Number", String.class, null);
        requestedTable.addContainerProperty("Name", String.class, null);

        if (studentDemands != null && courses != null) {
            Iterator<StudentDemandCourse> it = studentDemands.iterator();
            while (it.hasNext()) {
                StudentDemandCourse sdc = it.next();
                for (Course c : courses) {
                    if (sdc.getCourseId() == c.getId()) {
                        requestedTable.addItem(new Object[]{ sdc.getPriority(), c.getId(), "CS" + c.getNumber(), c.getName() }, sdc.getCourseId());
                        break;
                    }
                }
            }
        } else {
            Notification.show("No Student Course Demands Found",
                    "Student course demands were not found in the event log history",
                    Notification.Type.WARNING_MESSAGE);
        }

        requestedTable.setPageLength(requestedTable.size());
        addComponent(requestedTable);

        if (schedule == null) {
            Notification.show("Schedule null",
                    "Can not build form with a null suggested course schedule",
                    Notification.Type.ERROR_MESSAGE);
            return;
        }

        Table suggestedTable = new Table("Suggested Schedule");
        suggestedTable.addContainerProperty("ID", Integer.class, null);
        suggestedTable.addContainerProperty("Number", String.class, null);
        suggestedTable.addContainerProperty("Name", String.class, null);

        for (int s = 0; s < schedule.size(); s++) {
            StudentScheduleResult sr = schedule.get(s);
            Course c = sr.getCourse();
            suggestedTable.addItem(new Object[]{ c.getId(), "CS" + c.getNumber(), c.getName() }, sr.getId());
        }

        suggestedTable.setPageLength(suggestedTable.size());
        addComponent(suggestedTable);
    }
}
