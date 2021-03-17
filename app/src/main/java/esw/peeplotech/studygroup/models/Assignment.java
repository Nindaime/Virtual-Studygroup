package esw.peeplotech.studygroup.models;

public class Assignment {

    private String assignment_id;
    private String assignment_title;
    private String assignment_score;
    private String assignment_deadline;
    private String assignment_type;
    private String assignment_file;
    private String course_id;

    public Assignment() {
    }

    public Assignment(String assignment_id, String assignment_title, String assignment_score, String assignment_deadline, String assignment_type, String assignment_file, String course_id) {
        this.assignment_id = assignment_id;
        this.assignment_title = assignment_title;
        this.assignment_score = assignment_score;
        this.assignment_deadline = assignment_deadline;
        this.assignment_type = assignment_type;
        this.assignment_file = assignment_file;
        this.course_id = course_id;
    }

    public String getAssignment_id() {
        return assignment_id;
    }

    public void setAssignment_id(String assignment_id) {
        this.assignment_id = assignment_id;
    }

    public String getAssignment_title() {
        return assignment_title;
    }

    public void setAssignment_title(String assignment_title) {
        this.assignment_title = assignment_title;
    }

    public String getAssignment_score() {
        return assignment_score;
    }

    public void setAssignment_score(String assignment_score) {
        this.assignment_score = assignment_score;
    }

    public String getAssignment_deadline() {
        return assignment_deadline;
    }

    public void setAssignment_deadline(String assignment_deadline) {
        this.assignment_deadline = assignment_deadline;
    }

    public String getAssignment_type() {
        return assignment_type;
    }

    public void setAssignment_type(String assignment_type) {
        this.assignment_type = assignment_type;
    }

    public String getAssignment_file() {
        return assignment_file;
    }

    public void setAssignment_file(String assignment_file) {
        this.assignment_file = assignment_file;
    }

    public String getCourse_id() {
        return course_id;
    }

    public void setCourse_id(String course_id) {
        this.course_id = course_id;
    }
}
