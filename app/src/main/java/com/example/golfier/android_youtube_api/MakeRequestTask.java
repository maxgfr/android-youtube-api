package com.example.golfier.android_youtube_api;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * An asynchronous task that handles the YouTube Data API call.
 * Placing the API calls in their own task ensures the UI stays responsive.
 */
public class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {

    private com.google.api.services.youtube.YouTube mService = null;
    private Exception mLastError = null;
    private String result;
    private int status;
    ProgressDialog mProgress;

    private static final int REQUEST_AUTHORIZATION = 1001;

    MakeRequestTask(GoogleAccountCredential credential, ProgressDialog pd) {
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new com.google.api.services.youtube.YouTube.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("DataApi")
                .build();
        this.mProgress = pd;
    }

    /**
     * Background task to call YouTube Data API.
     * @param params no parameters needed for this task.
     */
    @Override
    protected List<String> doInBackground(Void... params) {
        try {
            return getDataFromApi();
        } catch (Exception e) {
            mLastError = e;
            cancel(true);
            return null;
        }
    }

    /**
     * Fetch information about the "GoogleDevelopers" YouTube channel.
     * @return List of Strings containing information about the channel.
     * @throws IOException
     */
    private List<String> getDataFromApi() throws IOException {
        // Get a list of up to 10 files.
        List<String> channelInfo = new ArrayList<String>();
        ChannelListResponse result = mService.channels().list("snippet,contentDetails,statistics")
                .setForUsername("GoogleDevelopers")
                .execute();
        List<Channel> channels = result.getItems();
        if (channels != null) {
            Channel channel = channels.get(0);
            channelInfo.add("This channel's ID is " + channel.getId() + ". " +
                    "Its title is '" + channel.getSnippet().getTitle() + ", " +
                    "and it has " + channel.getStatistics().getViewCount() + " views.");
        }
        return channelInfo;
    }


    @Override
    protected void onPreExecute() {
        result="";
        mProgress.show();
    }

    @Override
    protected void onPostExecute(List<String> output) {
        mProgress.hide();
        if (output == null || output.size() == 0) {
            result="No results returned.";
        } else {
            output.add(0, "Data retrieved using the YouTube Data API:");
            result=TextUtils.join("\n", output);
        }
    }

    @Override
    protected void onCancelled() {
        mProgress.hide();
        if (mLastError != null) {
            if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                int connectionStatusCode = ((GooglePlayServicesAvailabilityIOException) mLastError).getConnectionStatusCode();
                setStatusErr(connectionStatusCode);
            } else if (mLastError instanceof UserRecoverableAuthIOException) {
                setStatusErr(this.REQUEST_AUTHORIZATION);
            } else {
                result="The following error occurred:\n" +mLastError.getMessage();
            }
        } else {
            result="Request cancelled.";
        }
    }

    public String getResult () {
        return result;
    }

    public int getStatusErr () {
        return status;
    }

    public void setStatusErr (int nb) {
        this.status = nb;
    }
}