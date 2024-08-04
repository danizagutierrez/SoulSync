package com.example.soulsync.models;

public class VideoItem {
    private String id; // The unique identifier for the video
    private String title; // The title of the video
    private String thumbnailUrl; // The URL of the video thumbnail
    private String videoUrl; // The URL of the video


    // Constructor
    public VideoItem(String id, String title, String thumbnailUrl, String videoUrl) {
        this.id = id;
        this.title = title;
        this.thumbnailUrl = thumbnailUrl;
        this.videoUrl = videoUrl;

    }

    public VideoItem() {
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }



}
