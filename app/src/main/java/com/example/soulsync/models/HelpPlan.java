package com.example.soulsync.models;

public class HelpPlan {

    private String userId;
    private String emergencyContact;
    private String toDo;

    public HelpPlan() {
    }

    public HelpPlan(String userId, String emergencyContact, String toDo) {
        this.userId = userId;
        this.emergencyContact = emergencyContact;
        this.toDo = toDo;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public String getToDo() {
        return toDo;
    }

    public void setToDo(String toDo) {
        this.toDo = toDo;
    }
}
