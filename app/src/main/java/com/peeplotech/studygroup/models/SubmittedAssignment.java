package com.peeplotech.studygroup.models;

public class SubmittedAssignment {

    private String submission_id;
    private String student_id;
    private String assignment_score;
    private String course_id;
    private String assignment_file;

    public SubmittedAssignment() {
    }

    public SubmittedAssignment(String submission_id, String student_id, String assignment_score, String course_id, String assignment_file) {
        this.submission_id = submission_id;
        this.student_id = student_id;
        this.assignment_score = assignment_score;
        this.course_id = course_id;
        this.assignment_file = assignment_file;
    }

    public String getSubmission_id() {
        return submission_id;
    }

    public void setSubmission_id(String submission_id) {
        this.submission_id = submission_id;
    }

    public String getStudent_id() {
        return student_id;
    }

    public void setStudent_id(String student_id) {
        this.student_id = student_id;
    }

    public String getAssignment_score() {
        return assignment_score;
    }

    public void setAssignment_score(String assignment_score) {
        this.assignment_score = assignment_score;
    }

    public String getCourse_id() {
        return course_id;
    }

    public void setCourse_id(String course_id) {
        this.course_id = course_id;
    }

    public String getAssignment_file() {
        return assignment_file;
    }

    public void setAssignment_file(String assignment_file) {
        this.assignment_file = assignment_file;
    }
}
