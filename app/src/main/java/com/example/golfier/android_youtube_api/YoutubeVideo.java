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
}