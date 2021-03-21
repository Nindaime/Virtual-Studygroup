package com.peeplotech.studygroup.models;

public class AssessmentResult {

    private String result_id;
    private String course_id;
    private String student_id;
    private String attempted;
    private String passed;
    private String failed;
    private String total;

    public AssessmentResult() {
    }

    public AssessmentResult(String result_id, String course_id, String student_id, String attempted, String passed, String failed, String total) {
        this.result_id = result_id;
        this.course_id = course_id;
        this.student_id = student_id;
        this.attempted = attempted;
        this.passed = passed;
        this.failed = failed;
        this.total = total;
    }

    public String getResult_id() {
        return result_id;
    }

    public void setResult_id(String result_id) {
        this.result_id = result_id;
    }

    public String getCourse_id() {
        return course_id;
    }

    public void setCourse_id(String course_id) {
        this.course_id = course_id;
    }

    public String getStudent_id() {
        return student_id;
    }

    public void setStudent_id(String student_id) {
        this.student_id = student_id;
    }

    public String getAttempted() {
        return attempted;
    }

    public void setAttempted(String attempted) {
        this.attempted = attempted;
    }

    public String getPassed() {
        return passed;
    }

    public void setPassed(String passed) {
        this.passed = passed;
    }

    public String getFailed() {
        return failed;
    }

    public void setFailed(String failed) {
        this.failed = failed;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}
