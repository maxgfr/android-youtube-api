package com.example.golfier.android_youtube_api;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemListResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class MakeRequestTask extends AsyncTask<Void, Void, YoutubeUser> {

    private com.google.api.services.youtube.YouTube mService = null;
    private GoogleAccountCredential credential;
    private ProgressDialog mProgress;
    private YoutubeUser youtubeUser;
    private RequestInfo ri;

    MakeRequestTask(GoogleAccountCredential credential, ProgressDialog pd) {
        this.credential =credential;
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new com.google.api.services.youtube.YouTube.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("DataApi")
                .build();
        this.ri = RequestInfo.getInstance();
        this.mProgress = pd;
        this.youtubeUser = new YoutubeUser();
        this.youtubeUser.setAccountMail(this.credential.getSelectedAccountName());
    }

    @Override
    protected YoutubeUser doInBackground(Void... params) {
        getDataFromApi();
        return youtubeUser;
    }

    private void getDataFromApi() {
        ChannelListResponse channelResult = null;
        try {
            channelResult = mService.channels().list("snippet,contentDetails,statistics")
                    .setMine(true)
                    .setFields("items/contentDetails,nextPageToken,pageInfo")
                    .execute();

            System.out.println(channelResult);


            List<Channel> channelsList = channelResult.getItems();

            if (channelsList != null) {

                System.out.println(channelsList.get(0));

                youtubeUser.addUpload(channelsList.get(0).getContentDetails().getRelatedPlaylists().getUploads());

                // The user's default channel is the first item in the list.
                // Extract the playlist ID for the channel's videos from the
                // API response.
                String uploadPlaylistId =
                        channelsList.get(0).getContentDetails().getRelatedPlaylists().getUploads();

                // Define a list to store items in the list of uploaded videos.
                List<PlaylistItem> playlistItemList = new ArrayList<PlaylistItem>();

                // Retrieve the playlist of the channel's uploaded videos.
                YouTube.PlaylistItems.List playlistItemRequest =
                        mService.playlistItems().list("id,contentDetails,snippet");
                playlistItemRequest.setPlaylistId(uploadPlaylistId);

                // Only retrieve data used in this application, thereby making
                // the application more efficient. See:
                // https://developers.google.com/youtube/v3/getting-started#partial
                playlistItemRequest.setFields("items(contentDetails/videoId,snippet/title,snippet/publishedAt),nextPageToken,pageInfo");

                String nextToken = "";

                // Call the API one or more times to retrieve all items in the
                // list. As long as the API response returns a nextPageToken,
                // there are still more items to retrieve.
                do {
                    playlistItemRequest.setPageToken(nextToken);
                    PlaylistItemListResponse playlistItemResult = playlistItemRequest.execute();

                    playlistItemList.addAll(playlistItemResult.getItems());

                    nextToken = playlistItemResult.getNextPageToken();
                } while (nextToken != null);

                // Prints information about the results.
                processInfo(playlistItemList.iterator());
            }

        } catch (GoogleJsonResponseException e) {
            e.printStackTrace();
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : " + e.getDetails().getMessage());

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private void processInfo(Iterator<PlaylistItem> playlistEntries) {

        List<YoutubeVideo> myVideo = new ArrayList<>();

        while (playlistEntries.hasNext()) {
            PlaylistItem playlistItem = playlistEntries.next();
            YoutubeVideo vid = new YoutubeVideo(playlistItem.getSnippet().getTitle(),
                    playlistItem.getContentDetails().getVideoId(),
                    playlistItem.getSnippet().getPublishedAt().toString());
            myVideo.add(vid);
        }
        youtubeUser.addVideoContent(myVideo);
    }

    @Override
    protected void onPreExecute() {
        ri.addInfo("");
        mProgress.show();
    }

    @Override
    protected void onPostExecute(YoutubeUser output) {
        mProgress.hide();
        if (output == null) {
            ri.addInfo("No results returned.");
        } else {
            ri.addInfo("Data retrieved using the YouTube Data API:");
        }
        System.out.println(output.toString());
        if (output.getNbVideo() == 0) {
            System.out.println("Launch the second asynchronous task");
            new MakeRequestTaskName(this.credential,this.mProgress, youtubeUser.getPossibleUserName()).execute();
        }
    }

    @Override
    protected void onCancelled() {
        mProgress.hide();
        ri.addInfo("Request cancelled.");
    }

}