package com.example.soulsync.models;

public class WaterIntake {

    private String userId;

    private int cups;
    private String date;

    public WaterIntake() {
    }

    public WaterIntake(String userId, int cups, String date) {
        this.userId = userId;
        this.cups = cups;
        this.date = date;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getCups() {
        return cups;
    }

    public void setCups(int cups) {
        this.cups = cups;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
