package com.peeplotech.studygroup.models;

public class Assessment {

    private String assessment_id;
    private String course_id;
    private String lecturer_id;
    private String question;
    private String option_a;
    private String option_b;
    private String option_c;
    private String option_d;
    private String option_e;
    private String answer;

    public Assessment() {
    }

    public Assessment(String assessment_id, String course_id, String lecturer_id, String question, String option_a, String option_b, String option_c, String option_d, String option_e, String answer) {
        this.assessment_id = assessment_id;
        this.course_id = course_id;
        this.lecturer_id = lecturer_id;
        this.question = question;
        this.option_a = option_a;
        this.option_b = option_b;
        this.option_c = option_c;
        this.option_d = option_d;
        this.option_e = option_e;
        this.answer = answer;
    }

    public String getAssessment_id() {
        return assessment_id;
    }

    public void setAssessment_id(String assessment_id) {
        this.assessment_id = assessment_id;
    }

    public String getCourse_id() {
        return course_id;
    }

    public void setCourse_id(String course_id) {
        this.course_id = course_id;
    }

    public String getLecturer_id() {
        return lecturer_id;
    }

    public void setLecturer_id(String lecturer_id) {
        this.lecturer_id = lecturer_id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getOption_a() {
        return option_a;
    }

    public void setOption_a(String option_a) {
        this.option_a = option_a;
    }

    public String getOption_b() {
        return option_b;
    }

    public void setOption_b(String option_b) {
        this.option_b = option_b;
    }

    public String getOption_c() {
        return option_c;
    }

    public void setOption_c(String option_c) {
        this.option_c = option_c;
    }

    public String getOption_d() {
        return option_d;
    }

    public void setOption_d(String option_d) {
        this.option_d = option_d;
    }

    public String getOption_e() {
        return option_e;
    }

    public void setOption_e(String option_e) {
        this.option_e = option_e;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
