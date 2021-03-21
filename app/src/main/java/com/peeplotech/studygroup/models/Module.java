package com.peeplotech.studygroup.models;

public class Module {

    private String module_id;
    private String course_id;
    private String module_title;
    private String module_desc;
    private String module_type;
    private String module_file;

    public Module() {
    }

    public Module(String module_id, String course_id, String module_title, String module_desc, String module_type, String module_file) {
        this.module_id = module_id;
        this.course_id = course_id;
        this.module_title = module_title;
        this.module_desc = module_desc;
        this.module_type = module_type;
        this.module_file = module_file;
    }

    public String getModule_id() {
        return module_id;
    }

    public void setModule_id(String module_id) {
        this.module_id = module_id;
    }

    public String getCourse_id() {
        return course_id;
    }

    public void setCourse_id(String course_id) {
        this.course_id = course_id;
    }

    public String getModule_title() {
        return module_title;
    }

    public void setModule_title(String module_title) {
        this.module_title = module_title;
    }

    public String getModule_desc() {
        return module_desc;
    }

    public void setModule_desc(String module_desc) {
        this.module_desc = module_desc;
    }

    public String getModule_type() {
        return module_type;
    }

    public void setModule_type(String module_type) {
        this.module_type = module_type;
    }

    public String getModule_file() {
        return module_file;
    }

    public void setModule_file(String module_file) {
        this.module_file = module_file;
    }
}
