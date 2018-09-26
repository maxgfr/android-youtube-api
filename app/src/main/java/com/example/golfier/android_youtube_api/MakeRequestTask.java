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

/**
 * An asynchronous task that handles the YouTube Data API call.
 * Placing the API calls in their own task ensures the UI stays responsive.
 */
public class MakeRequestTask extends AsyncTask<Void, Void, YoutubeUser> {

    private com.google.api.services.youtube.YouTube mService = null;
    private Exception mLastError = null;
    private ProgressDialog mProgress;
    private YoutubeUser youtubeUser;
    private RequestInfo ri = RequestInfo.getInstance();

    private static final int REQUEST_AUTHORIZATION = 1001;

    MakeRequestTask(GoogleAccountCredential credential, ProgressDialog pd) {
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new com.google.api.services.youtube.YouTube.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("DataApi")
                .build();
        this.mProgress = pd;
        youtubeUser = new YoutubeUser();
    }

    /**
     * Background task to call YouTube Data API.
     * @param params no parameters needed for this task.
     */
    @Override
    protected YoutubeUser doInBackground(Void... params) {
        getDataFromApi();
        return null;
    }

    /**
     * Fetch information about the "GoogleDevelopers" YouTube channel.
     * @return List of Strings containing information about the channel.
     * @throws IOException
     */
    private List<String> getPrimaryData() throws IOException {
        // Get a list of up to 10 files.
        List<String> channelInfo = new ArrayList<String>();
        ChannelListResponse result = mService.channels().list("snippet,contentDetails,statistics")
                .setMine(true)
                .execute();
        List<Channel> channels = result.getItems();
        if (channels != null) {
            Channel channel = channels.get(0);
            youtubeUser.addInformation(channel.getId(), channel.getSnippet().getTitle(), channel.getStatistics().getViewCount(), channel.getContentDetails().getRelatedPlaylists().getUploads());
            channelInfo.add("This channel's ID is " + channel.getId() + ". " +
                    "Its title is '" + channel.getSnippet().getTitle() + ", " +
                    "and it has " + channel.getStatistics().getViewCount() + " views." +
                    "playslist upload : " + channel.getContentDetails().getRelatedPlaylists().getUploads());
        }
        return channelInfo;
    }

    private void getDataFromApi() {
        try{
            ChannelListResponse channelResult = mService.channels().list("snippet,contentDetails,statistics")
                    .setMine(true)
                    .setFields("items/contentDetails,nextPageToken,pageInfo")
                    .execute();

            System.out.println(channelResult);

            List<Channel> channelsList = channelResult.getItems();

            if (channelsList != null) {
                System.out.println(channelsList.get(0));
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
                processPrint(playlistItemList.size(), playlistItemList.iterator());
            }

        } catch (GoogleJsonResponseException e) {
            e.printStackTrace();
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : " + e.getDetails().getMessage());

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private void getDataFromApiWithName(String name) {
        try{
            ChannelListResponse channelResult = mService.channels().list("snippet,contentDetails,statistics")
                    .setForUsername(name)
                    .setFields("items/contentDetails,nextPageToken,pageInfo")
                    .execute();

            System.out.println(channelResult);

            List<Channel> channelsList = channelResult.getItems();

            if (channelsList != null) {
                System.out.println(channelsList.get(0));
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
                processPrint(playlistItemList.size(), playlistItemList.iterator());
            }

        } catch (GoogleJsonResponseException e) {
            e.printStackTrace();
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : " + e.getDetails().getMessage());

        } catch (Throwable t) {
            t.printStackTrace();
        }
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
    }

    @Override
    protected void onCancelled() {
        mProgress.hide();
        if (mLastError != null) {
            if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                int connectionStatusCode = ((GooglePlayServicesAvailabilityIOException) mLastError).getConnectionStatusCode();
                ri.addStatus(connectionStatusCode);
            } else if (mLastError instanceof UserRecoverableAuthIOException) {
                ri.addStatus(this.REQUEST_AUTHORIZATION);
            } else {
                ri.addInfo("The following error occurred:\n" +mLastError.getMessage());
            }
        } else {
            ri.addInfo("Request cancelled.");
        }
    }


    /*
     * Print information about all of the items in the playlist.
     *
     * @param size size of list
     *
     * @param iterator of Playlist Items from uploaded Playlist
     */
    private void processPrint(int size, Iterator<PlaylistItem> playlistEntries) {

        List<YoutubeVideo> myVideo = new ArrayList<>();

        System.out.println("=============================================================");
        System.out.println("\t\tTotal Videos Uploaded: " + size);
        System.out.println("=============================================================\n");

        while (playlistEntries.hasNext()) {

            PlaylistItem playlistItem = playlistEntries.next();

            System.out.println(" video name  = " + playlistItem.getSnippet().getTitle());
            System.out.println(" video id    = " + playlistItem.getContentDetails().getVideoId());
            System.out.println(" upload date = " + playlistItem.getSnippet().getPublishedAt());
            System.out.println("\n-------------------------------------------------------------\n");

            YoutubeVideo vid = new YoutubeVideo(playlistItem.getSnippet().getTitle(),
                    playlistItem.getContentDetails().getVideoId(),
                    playlistItem.getSnippet().getPublishedAt().toString());
            myVideo.add(vid);

        }
        youtubeUser.addVideoContent(myVideo);
    }
}