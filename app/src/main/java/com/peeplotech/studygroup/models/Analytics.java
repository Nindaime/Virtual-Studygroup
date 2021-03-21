package com.peeplotech.studygroup.models;

public class Analytics {

    private String student_id;
    private int courses_registered;
    private int group_contributions;
    private int assignments_submitted;
    private int assignment_viewed;
    private int assessments_taken;
    private int modules_completed;

    public Analytics() {
    }

    public Analytics(String student_id, int courses_registered, int group_contributions, int assignments_submitted, int assignment_viewed, int assessments_taken, int modules_completed) {
        this.student_id = student_id;
        this.courses_registered = courses_registered;
        this.group_contributions = group_contributions;
        this.assignments_submitted = assignments_submitted;
        this.assignment_viewed = assignment_viewed;
        this.assessments_taken = assessments_taken;
        this.modules_completed = modules_completed;
    }

    public String getStudent_id() {
        return student_id;
    }

    public void setStudent_id(String student_id) {
        this.student_id = student_id;
    }

    public int getCourses_registered() {
        return courses_registered;
    }

    public void setCourses_registered(int courses_registered) {
        this.courses_registered = courses_registered;
    }

    public int getGroup_contributions() {
        return group_contributions;
    }

    public void setGroup_contributions(int group_contributions) {
        this.group_contributions = group_contributions;
    }

    public int getAssignments_submitted() {
        return assignments_submitted;
    }

    public void setAssignments_submitted(int assignments_submitted) {
        this.assignments_submitted = assignments_submitted;
    }

    public int getAssignment_viewed() {
        return assignment_viewed;
    }

    public void setAssignment_viewed(int assignment_viewed) {
        this.assignment_viewed = assignment_viewed;
    }

    public int getAssessments_taken() {
        return assessments_taken;
    }

    public void setAssessments_taken(int assessments_taken) {
        this.assessments_taken = assessments_taken;
    }

    public int getModules_completed() {
        return modules_completed;
    }

    public void setModules_completed(int modules_completed) {
        this.modules_completed = modules_completed;
    }
}
