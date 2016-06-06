package com.crossroads.youtubefastplaylist;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import java.util.ArrayList;
import java.util.List;

public class PlayerActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener{

    private YouTubePlayerView playerView;

    private List<String> videoIds;
    private List<String> videoTitles;
    private List<String> videoThumbnailURLs;
    private int no_of_videos;

    public static VideoListAdapter playlistAdapter;
    public static ListView playlist_listview;
    public static List<VideoItem> playlist;

    public static final String KEY
            = "AIzaSyCyACnYUzFXj0GCjClHDR1oGGh3MeN0JRo";

    // key names for shared preferences.
    public static final String Id = "videoId";
    public static final String Title = "videoTitle";
    public static final String ThumbnailURL = "videoThumbnail";

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_player);

        playlist_listview = (ListView) findViewById(R.id.playlist_listview);

        // initialising playlistAdapter
        LayoutInflater inflater = getLayoutInflater();
        playlistAdapter = new VideoListAdapter(getApplicationContext(),
                R.layout.video_item,
                playlist,
                inflater,
                VideoListAdapter.PLAYLIST_ITEM_LAYOUT);
        playlist_listview.setAdapter(playlistAdapter);

        videoIds = new ArrayList<String>();

        loadPlaylist();
        no_of_videos = playlist.size();

        playerView = (YouTubePlayerView)findViewById(R.id.player_view);
        playerView.initialize(KEY, this);
    }

    public void loadPlaylist() {
        Log.i("info", "start - loadPlaylist()");

        playlistAdapter.clear();
        Log.i("playlist size", Integer.toString(playlist.size()));

        List<VideoItem> temp_playlist = SearchActivity.db.getAllVideos();
        for (VideoItem videoItem : temp_playlist) {
            playlistAdapter.add(videoItem);
        }

        Log.i("playlist size", Integer.toString(playlist.size()));
        videoIds.clear();
        for (int i = 0; i < playlist.size(); ++i) {
            videoIds.add(playlist.get(i).getId());
        }

        Log.i("playlistList - in load", videoIds.toString());
        Log.i("info", "end - loadPlaylist()");
    }

    // inserts current playlist into database.
    public void savePlaylist() {
        Log.i("info", "start - savePlaylist()");

        SearchActivity.db.bulkInsert(playlist);

        // just for debugging.
        videoIds.clear();
        for (int i = 0; i < playlist.size(); ++i) {
            videoIds.add(playlist.get(i).getId());
        }

        Log.i("playlistList-in save", videoIds.toString());
        Log.i("info", "end - savePlaylist()");
    }

    public void add(View v) {
        // get a reference to the Object to be removed from playlist.
        VideoItem itemToRemove = (VideoItem)v.getTag();

        // remove it from playlist.
        playlistAdapter.remove(itemToRemove);

        // remove Id ArrayList videoIds.
        videoIds.remove(itemToRemove.getId());

        // restart the activity.
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        if(!b && videoIds.size() != 0) {
            youTubePlayer.cueVideos(videoIds);
            youTubePlayer.play();
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                        YouTubeInitializationResult result) {
        Toast.makeText(this, getString(R.string.failed), Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        savePlaylist();
    }

}