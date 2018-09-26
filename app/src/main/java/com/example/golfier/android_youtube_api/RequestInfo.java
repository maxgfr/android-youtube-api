package com.example.golfier.android_youtube_api;

import java.util.ArrayList;
import java.util.List;

public class RequestInfo {

    private List<String> listInfo;
    private List<Integer> statusInfo;
    private static RequestInfo instance = null;

    protected RequestInfo() {
        listInfo = new ArrayList<>();
        statusInfo = new ArrayList<>();
    }
    public static RequestInfo getInstance() {
        if(instance == null) {
            instance = new RequestInfo();
        }
        return instance;
    }

    public List<String> getInfo () {
        return listInfo;
    }

    public void setInfo (List<String> list) {
        this.listInfo = list;
    }

    public void addInfo(String info) {
        this.listInfo.add(info);
    }

    public List<Integer> getStatus () {
        return statusInfo;
    }

    public void setStatus (List<Integer> list) {
        this.statusInfo = list;
    }

    public void addStatus (Integer status) {
        this.statusInfo.add(status);
    }
}
