package com.crossroads.youtubefastplaylist;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

    public static int activeIndex = 0;

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

    /**
     * invoked when the "REMOVE FROM PLAYLIST" button is pressed.
     * removes VideoItem from playlist and restarts PlayerActivity.
     * @param v : Button View.
     */
    public void add(View v) {
        // get a reference to the Object to be removed from playlist.
        final VideoItem itemToRemove = (VideoItem)v.getTag();

        final int position = playlist.indexOf(itemToRemove);

        Log.i("AppInfo", "position : " + position);

        View item = playlist_listview.getChildAt(position);

        item.animate()
                .alpha(0f)
                .setDuration(500)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        // remove it from playlist.
                        playlistAdapter.remove(itemToRemove);

                        // remove Id ArrayList videoIds.
                        videoIds.remove(itemToRemove.getId());

                        if (position < activeIndex) {
                            activeIndex--;
                            saveIndex();
                        }

                        restartActivity();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });

    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        // add listeners to YouTubePlayer instance
        youTubePlayer.setPlayerStateChangeListener(playerStateChangeListener);
        youTubePlayer.setPlaylistEventListener(playlistEventListener);

        activeIndex = getActiveIndex();

        if(!b && videoIds.size() != 0) {
            youTubePlayer.loadVideos(videoIds, activeIndex, 0);
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

    public void restartActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    public void reloadPlayer() {
        playerView.initialize(KEY, this);
    }

    private YouTubePlayer.PlaybackEventListener playbackEventListener = new YouTubePlayer.PlaybackEventListener() {

        @Override
        public void onPlaying() {

        }

        @Override
        public void onPaused() {

        }

        @Override
        public void onStopped() {

        }

        @Override
        public void onBuffering(boolean b) {

        }

        @Override
        public void onSeekTo(int i) {

        }
    };
    private YouTubePlayer.PlayerStateChangeListener playerStateChangeListener = new YouTubePlayer.PlayerStateChangeListener() {
        @Override
        public void onLoading() {

        }

        @Override
        public void onLoaded(String s) {

        }

        @Override
        public void onAdStarted() {

        }

        @Override
        public void onVideoStarted() {

        }

        @Override
        public void onVideoEnded() {

        }

        @Override
        public void onError(YouTubePlayer.ErrorReason errorReason) {

        }
    };
    private YouTubePlayer.PlaylistEventListener playlistEventListener = new YouTubePlayer.PlaylistEventListener() {
        @Override
        public void onPrevious() {
            Toast.makeText(getApplicationContext(),
                    "clicked previous",
                    Toast.LENGTH_SHORT).show();
            activeIndex--;
            saveIndex();
        }

        @Override
        public void onNext() {
            Toast.makeText(getApplicationContext(),
                    "clicked next",
                    Toast.LENGTH_SHORT).show();
            activeIndex++;
            saveIndex();
        }

        @Override
        public void onPlaylistEnded() {

        }
    };

    public void saveIndex() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        sharedPref.edit()
                .putInt(getString(R.string.pref_index), activeIndex)
                .commit();
        Log.i("AppInfo", "activeIndex : " + activeIndex);
    }

    public int getActiveIndex() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getInt(getString(R.string.pref_index), 0);
    }
}