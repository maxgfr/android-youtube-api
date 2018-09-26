package com.example.golfier.android_youtube_api;

public class YoutubeVideo {
    private String videoId;
    private String dateUpload;
    private String title;

    YoutubeVideo(String id, String upload, String title){
        this.videoId = id;
        this.dateUpload = upload;
        this.title = title;
    }
}
