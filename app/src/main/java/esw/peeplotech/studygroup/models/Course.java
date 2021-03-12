package esw.peeplotech.studygroup.models;

public class Course {

    private String course_id;
    private String course_owner;
    private String course_title;
    private String course_sub_title;
    private String course_brief;
    private String course_img;

    public Course() {
    }

    public Course(String course_id, String course_owner, String course_title, String course_sub_title, String course_brief, String course_img) {
        this.course_id = course_id;
        this.course_owner = course_owner;
        this.course_title = course_title;
        this.course_sub_title = course_sub_title;
        this.course_brief = course_brief;
        this.course_img = course_img;
    }

    public String getCourse_id() {
        return course_id;
    }

    public void setCourse_id(String course_id) {
        this.course_id = course_id;
    }

    public String getCourse_owner() {
        return course_owner;
    }

    public void setCourse_owner(String course_owner) {
        this.course_owner = course_owner;
    }

    public String getCourse_title() {
        return course_title;
    }

    public void setCourse_title(String course_title) {
        this.course_title = course_title;
    }

    public String getCourse_sub_title() {
        return course_sub_title;
    }

    public void setCourse_sub_title(String course_sub_title) {
        this.course_sub_title = course_sub_title;
    }

    public String getCourse_brief() {
        return course_brief;
    }

    public void setCourse_brief(String course_brief) {
        this.course_brief = course_brief;
    }

    public String getCourse_img() {
        return course_img;
    }

    public void setCourse_img(String course_img) {
        this.course_img = course_img;
    }
}
