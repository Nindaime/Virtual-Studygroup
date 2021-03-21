package com.peeplotech.studygroup.models;

public class Chat {

    private int id;
    private String sender;
    private String message;
    private String is_approved;

    public Chat() {
    }

    public Chat(int id, String sender, String message, String is_approved) {
        this.id = id;
        this.sender = sender;
        this.message = message;
        this.is_approved = is_approved;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getIs_approved() {
        return is_approved;
    }

    public void setIs_approved(String is_approved) {
        this.is_approved = is_approved;
    }
}
