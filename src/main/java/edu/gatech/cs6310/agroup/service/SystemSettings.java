package edu.gatech.cs6310.agroup.service;


import edu.gatech.cs6310.agroup.model.Semester;
import edu.gatech.cs6310.agroup.repository.SemesterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Date;


/**
 * Convenience wrapper to track the currently logged in user
 * Borrowed from https://github.com/vaadin-marcus/vaadin-tips
 * Created by jonathan on 4/18/16.
 */

@Service
public class SystemSettings {

    private static final Integer ver_maj = 0;
    private static final Integer ver_min = 1;
    private static final Integer ver_bld = 0;
    private static final String name = "Godac";

    @Autowired
    SemesterRepository semesterRepository;

    //These are set in the application.properties file but can be overriden via system properties
    @Value("${active.semester.id}")
    private Integer actvSemesId;

    @Value("${course.preference.limit}")
    private int NUM_CRS_PREF;

    public int getCoursePreferenceLimit() {
        return NUM_CRS_PREF;
    }

    public Integer getActiveSemesterId() {
        return actvSemesId;
    }

    @PostConstruct
    public void init() {

        //Date rightNow = Date();
        //semesterRepository.getSemesterThatBoundsDate(rightNow);

    }

    public String getName() {
        return name;
    }

    public String getVersion() { return String.format("%s.%s.%s", ver_maj, ver_min, ver_bld); }

    public Semester getSemester() { return semesterRepository.findOne(actvSemesId); }

    public String getSemesterName() { return semesterRepository.findOne(actvSemesId).getName(); }



}
