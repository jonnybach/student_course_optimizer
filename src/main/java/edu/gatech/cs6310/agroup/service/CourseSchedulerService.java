package edu.gatech.cs6310.agroup.service;

import edu.gatech.cs6310.agroup.eventmodel.CourseEvent;
import edu.gatech.cs6310.agroup.eventmodel.StateContainer;
import edu.gatech.cs6310.agroup.eventmodel.StudentDemandCourse;
import edu.gatech.cs6310.agroup.exception.EventSerializationException;
import edu.gatech.cs6310.agroup.model.*;
import edu.gatech.cs6310.agroup.repository.*;
import gurobi.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Scheduler for computing student course assignments
 *
 * Assumptions/Future Improvements:
 * - All object IDs are in order starting from 1; Assume we can translate to array indices
 *
 * <pre>CS6310 Software Architecture and Design, Spring 2016
 * Project 3: Distributed optimized student-course assignment</pre>
 *
 * @author <a href="mailto:tcesposito@gatech.edu">Timothy Esposito</a>
 */
@Service
public class CourseSchedulerService {

    protected static final Logger logger = LoggerFactory.getLogger(CourseSchedulerService.class);
    private final static int MAX_COURSES_PER_SEMESTER = 2;

    private final static boolean verbose = true;
    private final static boolean debug = true;
    

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseDependencyRepository courseDependencyRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private SemesterRepository semesterRepository;

    @Autowired
    private StudentDemandRepository studentDemandRepository;

    @Autowired
    private StudentScheduleResultRepository studentScheduleResultRepository;

    @Autowired
    private EventLogRepository eventLogRepository;

    @Autowired
    private EventLogStateService eventLogStateService;

    @Autowired
    private JmsTemplate jms;

    @Autowired
    SystemSettings systemSettings;

    /**
     * Method to accept the JMS message for a schedule calculation request and start processing it
     * @param eventLogId
     * @return
     */
    @JmsListener(destination = "cs6310.project3.calculate.schedule")
    @Transactional
    public void calculateSchedule(long eventLogId) throws EventSerializationException {
        EventLog eventLog = eventLogRepository.findOne(eventLogId);
        Semester semester = eventLog.getSemester();

        StateContainer stateContainer = eventLogStateService.calculateState(semester, eventLogId);

        calculateSchedule(semester, eventLog, stateContainer);
    }

    @Transactional
    public List<StudentScheduleResult> calculateSchedule(Semester semester, EventLog eventLog, StateContainer state) {
        // y[i][j][k]
        // i = student
        // j = course (j0 = prerequisite course, j1 = demanded course)
        // k = semester; for project 3, only 1 semester of course schedules are being computed, so removing [k]

        //List<StudentDemand> demandList = studentDemandRepository.findAll();
        List<Student> students = studentRepository.findAll();
        int numStudents = students.size();
        Map<Integer, Integer> studentIndexMap = new HashMap<>();
        for (int s = 0; s < numStudents; s++) {
            studentIndexMap.put(students.get(s).getId(), s);
        }

        List<Course> courses = new ArrayList<>();
        for (Course c : courseRepository.findAll()) {
            if (c.availableInSemester(semester.getId())) { // no need for course availability constraint
                courses.add(c);
            }
        }

        int numCourses = courses.size();
        Map<Integer, Integer> courseIndexMap = new HashMap<>();
        for (int c = 0; c < numCourses; c++) {
            courseIndexMap.put(courses.get(c).getId(), c);
        }

        //List<CourseDependency> courseDependencyList = courseDependencyRepository.findAll();
        //List<Semester> semesters = semesterRepository.findAll();

        Map<Integer, Course> courseMap = new HashMap<>();
        for (Course c : courses) {
            courseMap.put(c.getId(), c);
        }

        /*Map<Integer, Semester> semesterMap = new HashMap<>();
        for (Semester s : semesters) {
            semesterMap.put(s.getId(), s);
        }*/

        /*logger.info("Semester count = " + semesterMap.size());
        for (Semester s : semesterMap.values()) {
            System.out.println("Semester => " + s);
        }*/

        /*Map<Integer, Role> roleMap = App.roles.getRoles();
        for (Role r : roleMap.values()) {
            System.out.println("Role => " + r);
        }*/

        /*for (Course c : courseMap.values()) {
            System.out.println("Course => " + c);
        }

        for (CourseDependency cd : courseDependencyList) {
            System.out.println("Course dependency => " + cd);
        }*/

        /*for (StudentDemand sd : demandList) {
            System.out.println(sd);
        }*/


        //int numSemesters = semesterMap.size();
        List<StudentScheduleResult> schedules = new ArrayList<>();

        try {
            GRBEnv env = new GRBEnv("project3.log");
            if (!verbose) env.set(GRB.IntParam.LogToConsole, 0);
            GRBModel model = new GRBModel(env);

            // setup variables
            //GRBVar X = model.addVar(0, numStudents, 0.0, GRB.BINARY, "X");
            //GRBVar X = model.addVar(0, numStudents, 0.0, GRB.INTEGER, "X"); // minimum class size
            GRBVar[][] y = new GRBVar[numStudents][numCourses];

            for (int i = 0; i < numStudents; i++) {
                for (int j = 0; j < numCourses; j++) {
                    y[i][j] = model.addVar(0, 1, 0.0, GRB.BINARY, yName(i,j));
                }
            }

            GRBVar[][] As = new GRBVar[numStudents][numCourses];
            Map<Integer, Set<StudentDemandCourse>> studentDemandsMap = state.getStudentDemand();
            //int totalWeight = 0;

            for (int sid : studentDemandsMap.keySet()) {
                if (studentIndexMap.containsKey(sid)) {
                    int i = studentIndexMap.get(sid);

                    for (StudentDemandCourse sdc : studentDemandsMap.get(sid)) {
                        if (courseIndexMap.containsKey(sdc.getCourseId())) {
                            int j = courseIndexMap.get(sdc.getCourseId());
                            As[i][j] = model.addVar(1, 1, 1.0, GRB.BINARY, String.format("As[%d][%d]", i, j));
                            //totalWeight += SystemSettings.NUM_CRS_PREF + 1 - sdc.getPriority(); // can include seniority and GPA too
                        } else {
                            logger.warn("Located a course for a student demand that is not in a course catalog => courseId={}, studentId={}", sdc.getCourseId(), sid);
                        }
                    }

                } else {
                    logger.warn("Located a student course demand for a student ID that does not exist => {}; Maybe they graduated or dropped out", sid);
                }
            }

            //GRBVar X = model.addVar(0, totalWeight, 0.0, GRB.INTEGER, "X"); // student demand priority weight

            for (int i = 0; i < numStudents; i++) {
                for (int j = 0; j < numCourses; j++) {
                    if (As[i][j] == null) {
                        As[i][j] = model.addVar(0, 0, 0.0, GRB.BINARY, String.format("As[%d][%d]", i, j));
                    }
                }
            }

            // course dependencies can be ignored because only a single semester is being considered

            /*GRBVar[][] Ap = new GRBVar[numCourses][numCourses];

            for (CourseDependency cd : courseDependencyList) {
                int i = cd.getPrereq() - 1;
                int j = cd.getDependent() - 1;
                Ap[i][j] = model.addVar(1, 1, 1.0, GRB.BINARY, String.format("Ap[%d][%d]", i, j));
            }

            for (int i = 0; i < numCourses; i++) {
                for (int j = 0; j < numCourses; j++) {
                    if (Ap[i][j] == null) {
                        Ap[i][j] = model.addVar(0, 0, 0.0, GRB.BINARY, String.format("Ap[%d][%d]", i, j));
                    }
                }
            }*/

            //model.set(GRB.IntAttr.ModelSense, 1);

            // integrate variables
            model.update();

            // set objective
            //GRBLinExpr expr;
            GRBLinExpr expr = new GRBLinExpr();
            //expr.addTerm(1.0, X);
            //model.setObjective(expr, GRB.MINIMIZE); // used to minimize class size
            //model.setObjective(expr, GRB.MAXIMIZE); // use to maximize priority weight

            // add constraints

            //
            // max courses taken in a semester
            //

            for (int i = 0; i < numStudents; i++) {
                // max courses a student can take in one semester
                expr = new GRBLinExpr();

                for (int j = 0; j < numCourses; j++) {
                    expr.addTerm(1.0, y[i][j]);
                }

                model.addConstr(expr, GRB.LESS_EQUAL, MAX_COURSES_PER_SEMESTER, String.format("maxCourses_%d", i));
            }

            //
            // student course demands
            //

            expr = new GRBLinExpr();

            for (int sid : studentDemandsMap.keySet()) {
                if (studentIndexMap.containsKey(sid)) {
                    int i = studentIndexMap.get(sid);

                    // students wants to take course
                    //expr = new GRBLinExpr();
                    //double prioritySum = 0;

                    for (StudentDemandCourse sdc : studentDemandsMap.get(sid)) {
                        if (courseIndexMap.containsKey(sdc.getCourseId())) {
                            int j = courseIndexMap.get(sdc.getCourseId());

                            // handle priority
                            int priority = systemSettings.getCoursePreferenceLimit() + 1 - sdc.getPriority();
                            //prioritySum += priority;
                            expr.addTerm((double) priority, y[i][j]);

                            /*expr = new GRBLinExpr();
                            expr.addTerm(1.0, y[i][j]);
                            model.addConstr(expr, GRB.EQUAL, 1.0, String.format("wantsCourse_%d_%d", i, j));*/
                        }
                    }

                    //model.addConstr(expr, GRB.LESS_EQUAL, prioritySum, String.format("wantsCourse_%d", i));
                    //model.addConstr(expr, GRB.EQUAL, 1.0, String.format("wantsCourse_%d", i));
                    //model.setObjective(expr, GRB.MAXIMIZE);
                }
            }

            //GRBLinExpr rhs = new GRBLinExpr();
            //rhs.addTerm(1.0, X);
            //model.addConstr(expr, GRB.LESS_EQUAL, totalWeight, "courseWeights");
            model.setObjective(expr, GRB.MAXIMIZE);

            /*for (StudentDemand sd : demandList) {
                int i = sd.getStudent().getId() - 1;
                int j = sd.getCourse().getId() - 1;

                // students must take course
                expr = new GRBLinExpr();

                for (int k = 0; k < numSemesters; k++) {
                    expr.addTerm(1.0, y[i][j][k]);
                }

                model.addConstr(expr, GRB.EQUAL, 1.0, String.format("mustTakeCourse_%d_%d", i, j));
            }*/

            //
            // course prerequisites
            // prerequisites occur before dependent courses
            //

            /*GRBLinExpr rhs;

            for (int i = 0; i < numStudents; i++) {
                for (CourseDependency cd : courseDependencyList) {
                    int j1 = cd.getDependency().getId() - 1; // Not 100% sure this is right, changed it to the new model from just the course number (Matt L)

                    // no dependent courses offered first semester
                    expr = new GRBLinExpr();
                    expr.addTerm(1.0, y[i][j1][0]);
                    model.addConstr(expr, GRB.EQUAL, 0.0, String.format("nodep_%d_%d", i, j1));
                }
            }

            for (int k1 = 0; k1 < numSemesters - 1; k1++) { // for all k1 between 1 and m - 1
                for (int i = 0; i < numStudents; i++) {
                    for (CourseDependency cd : courseDependencyList) {
                        int j0 = cd.getPrereq().getId() - 1;
                        int j1 = cd.getDependency().getId() - 1;

                        // prerequisite constraint from Aude Marzuoli
                        expr = new GRBLinExpr();
                        rhs = new GRBLinExpr();

                        for (int k = 0; k <= k1; k++) {
                            expr.addTerm(1.0, y[i][j1][k+1]);
                            rhs.addTerm(1.0, y[i][j0][k]);
                        }

                        model.addConstr(expr, GRB.LESS_EQUAL, rhs, String.format("prereq_%d_%d_%d", i, j1, k1));
                    }
                }
            }*/

            //
            // course availability / capacity; Not needed because courses not available already removed from list
            //

            /*for (Course c : courseMap.values()) {
                int j = courseIndexMap.get(c.getId());
                expr = new GRBLinExpr();

                for (int i = 0; i < numStudents; i++) {
                    expr.addTerm(1.0, y[i][j]);
                }

                if (c.availableInSemester(semester.getId())) {
                    model.addConstr(expr, GRB.LESS_EQUAL, numStudents, String.format("avail_%d", j));
                } else {
                    model.addConstr(expr, GRB.EQUAL, 0, String.format("avail_%d", j));
                }
            }*/

            //
            // constraint used to minimize class size X
            //

            /*for (int j = 0; j < numCourses; j++) {
                // number of students in each course <= X
                expr = new GRBLinExpr();

                for (int i = 0; i < numStudents; i++) {
                    expr.addTerm(1.0, y[i][j]);
                }

                model.addConstr(expr, GRB.LESS_EQUAL, X, String.format("classSize_%d", j));
                //model.setObjective(expr, GRB.MINIMIZE);
            }*/

            // optimize model
            model.optimize();

            // write and print solution
            model.write("project3.lp");
            model.write("project3.sol");

            //Object[] studentsArray = students.toArray();

            if (debug) {
                // print courses for referencing columns
                System.out.print("\nCourses ...\n");
                for (int j = 0; j < numCourses; j++) {
                    Course course = courses.get(j);
                    System.out.printf("%6d => [%d] %s %s\n", j, course.getId(), course.getNumber(), course.getName());
                }
                System.out.println();

                // print student demands
                System.out.println("Student demands (studentID => courseId:priority ) ...");
                for (int sid : studentDemandsMap.keySet()) {
                    if (studentIndexMap.containsKey(sid)) {
                        System.out.printf("%3d =>", sid);
                        for (StudentDemandCourse sdc : studentDemandsMap.get(sid)) {
                            System.out.printf(" %d:%d", sdc.getCourseId(), sdc.getPriority());
                        }
                    }
                    System.out.println();
                }
                System.out.println();

                // print schedule
                System.out.printf("Suggested course schedule for %s semester (rows=students, cols=courses) ...\n", semester.getName());
                System.out.print("      ");
                for (int j = 0; j < numCourses; j++) {
                    System.out.printf("%3d", j);
                }
                System.out.print("\n      ");
                for (int j = 0; j < numCourses; j++) {
                    System.out.printf("%3d", courses.get(j).getId());
                }
                System.out.println();

                for (int i = 0; i < numStudents; i++) {
                    Student student = students.get(i);
                    System.out.printf("%3d =>", student.getId());

                    for (int j = 0; j < numCourses; j++) {
                        //System.out.printf("%.0f", y[i][j][k].get(GRB.DoubleAttr.X));
                        char courseTaken = y[i][j].get(GRB.DoubleAttr.X) == 1 ? 'X' : '.';
                        System.out.printf("  %c", courseTaken);
                    }
                    System.out.println();
                }
                System.out.println();
            }

            int status = model.get(GRB.IntAttr.Status);

            if (status == GRB.OPTIMAL) {
                logger.info("The optimal solution was found");
            } // else GRB.INF_OR_UNBD, GRB.INFEASIBLE, GRB.UNBOUNDED

            // print minimum class size
            //logger.debug("GRB.DoubleAttr.ObjVal: X = " + model.get(GRB.DoubleAttr.ObjVal));
            //logger.debug("X = " + X.get(GRB.DoubleAttr.X));

            for (int i = 0; i < numStudents; i++) {
                Student student = students.get(i);

                for (int j = 0; j < numCourses; j++) {
                    if (y[i][j].get(GRB.DoubleAttr.X) == 1) {
                        StudentScheduleResult schedule = new StudentScheduleResult();
                        schedule.setEventLog(eventLog);
                        schedule.setStudent(student);
                        schedule.setCourse(courses.get(j));
                        schedule.setIsAssigned(true);
                        schedules.add(schedule);
                    }
                }

                studentScheduleResultRepository.save(schedules);
                eventLog.setResultCalculated(true);
                eventLogRepository.save(eventLog);

            }

            logger.debug("Done processing eventLog is [{}]", eventLog.getId());

            // send notification to all listeners waiting for schedule results
            jms.convertAndSend("cs6310.project3.scheduled", String.valueOf(eventLog.getId()));

            model.dispose();
            env.dispose();

        } catch (GRBException e) {
            logger.error("Gurobi exception occurred => " + e.toString());
            e.printStackTrace();
        }

        return schedules;
    }

    private String yName(int i, int j) {
        return String.format("y[%d][%d]", i, j);
    }
}
