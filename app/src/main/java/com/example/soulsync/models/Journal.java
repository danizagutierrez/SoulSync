package com.example.soulsync.models;

public class Journal {

    private String userID;
    private String titleJournal;
    private String contentJournal;
    private String dateJournal;

    public Journal(String userID, String titleJournal, String contentJournal, String dateJournal) {
        this.userID = userID;
        this.titleJournal = titleJournal;
        this.contentJournal = contentJournal;
        this.dateJournal = dateJournal;
    }
    public Journal(){

    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getTitleJournal() {
        return titleJournal;
    }

    public void setTitleJournal(String titleJournal) {
        this.titleJournal = titleJournal;
    }

    public String getContentJournal() {
        return contentJournal;
    }

    public void setContentJournal(String contentJournal) {
        this.contentJournal = contentJournal;
    }

    public String getDateJournal() {
        return dateJournal;
    }

    public void setDateJournal(String dateJournal) {
        this.dateJournal = dateJournal;
    }
}
