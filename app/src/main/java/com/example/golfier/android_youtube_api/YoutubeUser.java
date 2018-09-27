package com.example.golfier.android_youtube_api;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class YoutubeUser {

    private String accountMail;
    private String possibleUserName;
    private List<String> listId;
    private List<String> listTitle;
    private List<String> listChannelIdUpload;
    private List<YoutubeVideo> listUpload;
    private List<BigInteger> listViewCount;
    private Integer nbSubscriber;

    public YoutubeUser() {
        accountMail="";
        possibleUserName="";
        listId = new ArrayList<>();
        listTitle = new ArrayList<>();
        listChannelIdUpload = new ArrayList<>();
        listViewCount = new ArrayList<>();
        listUpload = new ArrayList<>();
    }

    public String getPossibleUserName() {
        return possibleUserName;
    }

    public List<String> getListTitle() {
        return listTitle;
    }

    public String getAccountMail() {
        return accountMail;
    }

    public List<String> getListId() {
        return listId;
    }

    public List<String> getListChannelIdUpload() {
        return listChannelIdUpload;
    }

    public List<BigInteger> getListViewCount() {
        return listViewCount;
    }

    public List<YoutubeVideo> getListUpload() {

        return listUpload;
    }

    public Integer getNbSubscriber() {
        return nbSubscriber;
    }

    public void setNbSubscriber(Integer nbSubscriber) {
        this.nbSubscriber = nbSubscriber;
    }

    public void setListUpload(List<YoutubeVideo> listUpload) {
        this.listUpload = listUpload;
    }

    public void setAccountMail(String accountMail) {
        this.accountMail = accountMail;
        //Set the possible username
        int index = accountMail.indexOf("@");
        this.possibleUserName = accountMail.substring(0, index);
        System.out.println("POSSIBLE USERNAME is : "+this.possibleUserName);
    }

    public void setListId(List<String> listId) {
        this.listId = listId;
    }

    public void setListTitle(List<String> listTitle) {
        this.listTitle = listTitle;
    }

    public void setListChannelIdUpload(List<String> listChannelIdUpload) {
        this.listChannelIdUpload = listChannelIdUpload;
    }

    public void setListViewCount(List<BigInteger> listViewCount) {
        this.listViewCount = listViewCount;
    }

    public void addInformation (String id, String title, BigInteger viewCount, String listChannelIdUpload) {
        this.listId.add(id);
        this.listTitle.add(title);
        this.listChannelIdUpload.add(listChannelIdUpload);
        this.listViewCount.add(viewCount);
    }

    public void addVideoContent(List<YoutubeVideo> videoId) {
        this.listUpload.addAll(videoId);
    }

    @Override
    public String toString() {
        String str = "\n";
        int i = 1;
        for(YoutubeVideo yv : listUpload) {
            str += "VIDEO ID num."+i+" "+yv.getVideoId()+"\n";
        }
        return "YoutubeUser{" +
                "accountMail='" + accountMail + '\'' +
                ", possibleUserName='" + possibleUserName + '\'' +
                ", nbSubscriber='" + nbSubscriber + '\'' +
                ", listId=" + listId +
                ", listTitle=" + listTitle +
                ", listChannelIdUpload=" + listChannelIdUpload +
                ", listUpload=" + str +
                ", listViewCount=" + listViewCount +
                '}';
    }

    public void addUpload(String uploads) {
        this.listChannelIdUpload.add(uploads);
    }

    public int getNbVideo() {
        return this.listUpload.size();
    }
}