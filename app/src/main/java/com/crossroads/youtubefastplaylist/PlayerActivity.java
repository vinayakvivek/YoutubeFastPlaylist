package com.crossroads.youtubefastplaylist;

import android.content.Intent;
import android.os.Bundle;
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

public class PlayerActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    private YouTubePlayerView playerView;
    private List<String> videoIds;
    public static VideoListAdapter playlistAdapter;
    public static ListView playlist_listview;
    public static List<VideoItem> playlist;
    public static final String KEY = "AIzaSyCyACnYUzFXj0GCjClHDR1oGGh3MeN0JRo";

    private static int activeVideo;
    private static int activeTime;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_player);

        playlist_listview = (ListView) findViewById(R.id.playlist_listview);

        LayoutInflater inflater = getLayoutInflater();
        playlistAdapter = new VideoListAdapter(getApplicationContext(),
                R.layout.video_item,
                playlist,
                inflater,
                VideoListAdapter.PLAYLIST_ITEM_LAYOUT);
        playlist_listview.setAdapter(playlistAdapter);

        videoIds = new ArrayList<String>();
        for (int i = 0; i < playlist.size(); i++) {
            videoIds.add(playlist.get(i).getId());
        }

        playerView = (YouTubePlayerView)findViewById(R.id.player_view);
        playerView.initialize(KEY, this);
    }

    public void add(View v) {
        VideoItem itemToRemove = (VideoItem)v.getTag();
        playlistAdapter.remove(itemToRemove);
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        if(!b){
            youTubePlayer.cueVideos(videoIds);
            youTubePlayer.play();
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                        YouTubeInitializationResult result) {
        Toast.makeText(this, getString(R.string.failed), Toast.LENGTH_LONG).show();
    }


}
