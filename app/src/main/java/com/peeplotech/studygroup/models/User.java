package com.peeplotech.studygroup.models;

public class User {

    private String user_id;
    private String first_name;
    private String last_name;
    private String password;
    private String user_type;
    private String user_avatar;

    public User() {
    }

    public User(String user_id, String first_name, String last_name, String password, String user_type, String user_avatar) {
        this.user_id = user_id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.password = password;
        this.user_type = user_type;
        this.user_avatar = user_avatar;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public String getUser_avatar() {
        return user_avatar;
    }

    public void setUser_avatar(String user_avatar) {
        this.user_avatar = user_avatar;
    }
}
