package com.example.golfier.android_youtube_api;

public class YoutubeVideo {
    private String videoId;
    private String title;
    private String dateUpload;

    YoutubeVideo(String title, String id, String upload){
        this.videoId = id;
        this.dateUpload = upload;
        this.title = title;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDateUpload() {
        return dateUpload;
    }

    public void setDateUpload(String dateUpload) {
        this.dateUpload = dateUpload;
    }

    public void dispData() {
        System.out.println("=============================================================\n");
        System.out.println(" video name  = " + this.title);
        System.out.println(" video id    = " + this.videoId);
        System.out.println(" upload date = " + this.dateUpload);
        System.out.println("=============================================================\n");
    }
}
