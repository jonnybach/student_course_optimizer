package edu.gatech.cs6310.agroup.ui;


import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import edu.gatech.cs6310.agroup.exception.EventSerializationException;
import edu.gatech.cs6310.agroup.repository.CourseRepository;
import edu.gatech.cs6310.agroup.repository.SemesterRepository;
import edu.gatech.cs6310.agroup.service.*;
import edu.gatech.cs6310.agroup.uievent.LogoutEvent;
import edu.gatech.cs6310.agroup.uievent.SubmitForCalculationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import javax.annotation.PostConstruct;
import java.util.*;

@UIScope
@SpringView(name = StudentPreferencesView.NAME)
public class StudentPreferencesView extends GridLayout implements View,
        ApplicationEventPublisherAware, Property.ValueChangeListener {

    public static final String NAME = "student_prefs_view";
    private static final Logger log = LoggerFactory.getLogger(StudentPreferencesView.class);

    @Autowired
    private SystemSettings systemSettings;

    @Autowired
    private VaadinAdapter vaadinAdapter;

    private ApplicationEventPublisher publisher;
    private IndexedContainer semPrefsCnfg;
    private List<Component> cmpInpts;
    private Integer selectLevel;
    private Long activeEventId;

    @PostConstruct
    private void init() {

        //set grid configuration
        setColumns(2);
        setRows(6);
        setWidth("600px");
        setMargin(true);
        setSpacing(true);

        Button btnCrsCalc = new Button("Submit");
        btnCrsCalc.addClickListener(event -> handleCalcAssgnButtonClick(event));
        addComponent(btnCrsCalc,1,0);
        setComponentAlignment(btnCrsCalc, Alignment.MIDDLE_RIGHT);

        intializeComboBoxes();

    }

    private void initializeContainers() {
        //create the indexed containers for the given system semester
        // and default eventId
        semPrefsCnfg = vaadinAdapter.getStudentPrefsContainerFromState(CurrentUser.getId(), systemSettings.getSemester(), activeEventId);
    }

    private void intializeComboBoxes() {
        cmpInpts = new ArrayList<>(systemSettings.getCoursePreferenceLimit());
        for (Integer i = 0; i< systemSettings.getCoursePreferenceLimit(); i++) {
            List<Component> cmps = createCoursePrefInputRow(i);
            addComponent(cmps.get(0), 0, i+1);
            addComponent(cmps.get(1), 1, i+1);
            setComponentAlignment(cmps.get(0), Alignment.MIDDLE_CENTER);
            setComponentAlignment(cmps.get(1), Alignment.MIDDLE_CENTER);
            setColumnExpandRatio(0, 0.2f);
            setColumnExpandRatio(1, 0.8f);
            cmpInpts.add(cmps.get(1));
            if (i > 0) {
                cmps.get(1).setEnabled(false);
            }
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
        debugPrintActiveCourses();

        //we now have data so config the combo boxes
        selectLevel = 1;
        setComboBoxStates(); //update combo boxes based on any already selected course preferences from the event
        cmpInpts.forEach( cmb -> ((ComboBox)cmb).addValueChangeListener(this));
    }

    private void setComboBoxStates() {

        //reset comboboxes to initial state with nothing but null
        for(Integer icmb = 0; icmb < systemSettings.getCoursePreferenceLimit(); icmb++ ) {
            ComboBox cmb = (ComboBox) cmpInpts.get(icmb);
            cmb.removeValueChangeListener(this);
            cmb.setEnabled(false);
            cmb.removeAllItems();
            cmb.setNullSelectionItemId("-- NONE --");
        }

        //find all selected courses and get a list of the
        // item ids sorted by priority
        Container.Filter filter = new Compare.Equal("selected", true);
        semPrefsCnfg.sort(new Object[] {"prior"}, new boolean[] {true});
        semPrefsCnfg.addContainerFilter(filter);
        Map<Integer, Object> crsPrios = new TreeMap<>();
        for (Object iid : semPrefsCnfg.getItemIds()) {
            Integer prio = (Integer) semPrefsCnfg.getContainerProperty(iid, "priority").getValue();
            crsPrios.put(prio, iid);
        }
        semPrefsCnfg.removeAllContainerFilters();
        semPrefsCnfg.sort(new Object[] {"number"}, new boolean[] {true});

        if (crsPrios.size() > 0) {
            //at least one course is selected

            for (Map.Entry<Integer, ?> entry : crsPrios.entrySet()) {

                String lbl = (String) semPrefsCnfg.getContainerProperty(entry.getValue(), "label").getValue();
                Integer prio = (Integer) semPrefsCnfg.getContainerProperty(entry.getValue(), "priority").getValue();

                if (prio == selectLevel) {
                    selectLevel = prio;
                    ComboBox cmb = (ComboBox)cmpInpts.get(selectLevel-1);
                    cmb.addItems( getUnselectedCoursesAndPriorOrGreater(selectLevel) );
                    cmb.setEnabled(true);
                    cmb.select(lbl); //should fire combo selection changed
                    selectLevel++;
                } else {
                    log.error(String.format("Mismatch in priority level and course to select when trying to update combo boxes."));
                }

                debugPrintActiveCourses();

            }
        }

        //finally for the next open level if it exists, enable and populate with remaining classes and null selection
        if (selectLevel < systemSettings.getCoursePreferenceLimit()) {
            cmpInpts.get(selectLevel - 1).setEnabled(true);
            ((ComboBox) cmpInpts.get(selectLevel - 1)).addItems(getUnselectedCoursesAndPriorOrGreater(selectLevel));
            ((ComboBox) cmpInpts.get(selectLevel - 1)).select("-- NONE --");
        }

    }

    @Override
    public void setApplicationEventPublisher (ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    private List<Component> createCoursePrefInputRow(Integer rowNum) {

        ArrayList<Component> cmps = new ArrayList<>();

        Label lblPrio = new Label(String.format("Priority %d:", rowNum+1));
        cmps.add(lblPrio);

        ComboBox cmbBoxCors1 = new ComboBox();
        cmbBoxCors1.setNullSelectionAllowed(true);
        cmbBoxCors1.setNullSelectionItemId("-- NONE --");
        cmbBoxCors1.setNewItemsAllowed(false);
        cmbBoxCors1.setWidth("100%");
        cmps.add(cmbBoxCors1);

        return cmps;

    }

    private List<String> getUnselectedCoursesAndPriorOrGreater(Integer priority) {
        List<String> remLbls = new ArrayList<>();
        remLbls.add("-- NONE --");
        List<Object> item_ids = (List<Object>) semPrefsCnfg.getItemIds();
        for (Object i : item_ids ) {
            Item it = semPrefsCnfg.getItem(i);
            if ( (((Integer)it.getItemProperty("priority").getValue()) >= priority) ||
                 !((Boolean)it.getItemProperty("selected").getValue()) ) {
                remLbls.add( (String)it.getItemProperty("label").getValue() );
            }
        }
        return remLbls;
    }

    private List<String> getUnselectedCourses() {
        List<String> remLbls = new ArrayList<>();
        remLbls.add("-- NONE --");
        List<Object> item_ids = (List<Object>) semPrefsCnfg.getItemIds();
        for (Object i : item_ids ) {
            Item it = semPrefsCnfg.getItem(i);
            if ( !((Boolean)it.getItemProperty("selected").getValue()) ) {
                remLbls.add( (String)it.getItemProperty("label").getValue() );
            }
        }
        return remLbls;
    }

    @Override
    public void valueChange(Property.ValueChangeEvent event) {

        Object cmbBox = event.getProperty();
        Object item_id = event.getProperty().getValue();  //container item id number

        //get the active priority level based on the location of the combobox
        // not the cleanest but works for now
        Boolean shldHndlEvnt = false;
        for (Integer i=1; i<=systemSettings.getCoursePreferenceLimit(); i++) {
            if (cmpInpts.get(i-1).equals(cmbBox)) {
                selectLevel = i;
                shldHndlEvnt = true;
                break;
            }
        }

        if (shldHndlEvnt) {

            //it will always be the case that we need to reset config that occurs above the latest
            // changed priority level
            resetComboBoxesAndPrefsAboveLevel(selectLevel);
            debugPrintActiveCourses();

            //update the currently selected level based on the combo box selection
            if (item_id == null) {
                //selection is null.

                resetComboBoxAndPrefAtThisLevel(selectLevel);

                if (selectLevel == 1) {
                    //No levels to step down too, reset combobox and repopulate with avail courses.

                    cmpInpts.get(selectLevel - 1).setEnabled(true);
                    ((ComboBox) cmpInpts.get(selectLevel - 1)).addItems(getUnselectedCourses());

                } else {
                    //decrement to the level down, nothing else needed since
                    // already reset this levels combobox above
                    selectLevel--;
                }

            } else {
                //selection item is not null. update container item properties accordingly

                //reset any container items with this priority value set
                for (Object id : semPrefsCnfg.getItemIds() ) {
                    if ( semPrefsCnfg.getContainerProperty(id, "priority").getValue().equals( new Integer(selectLevel)) ) {
                        semPrefsCnfg.getContainerProperty(id, "selected").setValue(Boolean.FALSE);
                        semPrefsCnfg.getContainerProperty(id, "priority").setValue(0);
                    }
                }

                //update the item at the selected label
                for (Object id : semPrefsCnfg.getItemIds() ) {
                    if ( semPrefsCnfg.getContainerProperty(id, "label").getValue().equals(item_id) ) {
                        semPrefsCnfg.getContainerProperty(id, "selected").setValue(Boolean.TRUE);
                        semPrefsCnfg.getContainerProperty(id, "priority").setValue(selectLevel);
                    }
                }

                //increment the selection level to active the next level
                // combox box, update list with remaining courses
                if (selectLevel < systemSettings.getCoursePreferenceLimit()) {
                    selectLevel++;
                    cmpInpts.get(selectLevel - 1).setEnabled(true);
                    ((ComboBox) cmpInpts.get(selectLevel - 1)).addItems(getUnselectedCourses());
                }
            }

            debugPrintActiveCourses();

        }
    }
    private void resetComboBoxesAndPrefsAboveLevel(Integer selectedPrioLevel) {
        //clear out comboboxes and the container at all lower priority levels (higher numbers)
        for (Integer iPrio = systemSettings.getCoursePreferenceLimit(); iPrio>=selectedPrioLevel+1; iPrio--) {
            resetComboBoxAndPrefAtThisLevel(iPrio);
        }
    }
    private void resetComboBoxAndPrefAtThisLevel(Integer selectedPrioLevel) {
        //reset combobox for this priority level
        ComboBox cmb = (ComboBox) cmpInpts.get(selectedPrioLevel-1);
        cmb.removeValueChangeListener(this);
        cmb.setEnabled(false);
        cmb.removeAllItems();
        cmb.setNullSelectionItemId("-- NONE --");
        //cmb.select("-- NONE --");
        cmb.addValueChangeListener(this);

        //reset any container items with this priority value set
        for (Object id : semPrefsCnfg.getItemIds() ) {
            if ( semPrefsCnfg.getContainerProperty(id, "priority").getValue().equals( new Integer(selectedPrioLevel)) ) {
                semPrefsCnfg.getContainerProperty(id, "selected").setValue(Boolean.FALSE);
                semPrefsCnfg.getContainerProperty(id, "priority").setValue(0);
            }
        }
    }

    private void handleCalcAssgnButtonClick(Button.ClickEvent event) {

        //TODO -ensure that at least one course is selected by the student
        Container.Filter filter = new Compare.Equal("selected", true);
        semPrefsCnfg.addContainerFilter(filter);
        Integer numSelectCrs = semPrefsCnfg.size();
        semPrefsCnfg.removeAllContainerFilters();

        if (numSelectCrs <= 0 ) {
            Notification.show("Cannot Submit for Assignment", "You must select at least one course preference to submit a course assignment calculation.", Notification.Type.HUMANIZED_MESSAGE);
        } else {
            try {
                vaadinAdapter.saveStudentPrefs(semPrefsCnfg);
            } catch (EventSerializationException e) {
                String msg = String.format("Exception was thrown with message %s", e.getMessage());
                Notification.show("Application Error", "Fatal error occurred when attempting to save your course preference configuration.", Notification.Type.ERROR_MESSAGE);
                LogoutEvent loEvt = new LogoutEvent(this);
                this.publisher.publishEvent(loEvt);
            }

            SubmitForCalculationEvent sbmEvt = new SubmitForCalculationEvent(this);
            this.publisher.publishEvent(sbmEvt);
        }
    }

    private void debugPrintActiveCourses() {

        log.debug(String.format("%15s%15s%15s%15s", "**********", "********", "********", "********"));
        log.debug(String.format("%15s%15s%15s%15s", "Course Num", "Capacity", "Selected", "Priority"));
        log.debug(String.format("%15s%15s%15s%15s", "----------", "--------", "--------", "--------"));

        List<Object> item_ids = (List<Object>) semPrefsCnfg.getItemIds();
        for (Object i : item_ids ) {
            String num = (String) semPrefsCnfg.getContainerProperty(i, "number").getValue();
            Integer cap = (Integer) semPrefsCnfg.getContainerProperty(i, "capacity").getValue();
            String slct = semPrefsCnfg.getContainerProperty(i, "selected").getValue().toString();
            Integer prio = (Integer) semPrefsCnfg.getContainerProperty(i, "priority").getValue();
            log.debug(String.format("%15s%15d%15s%15d", num, cap, slct, prio));
        }

    }

}
