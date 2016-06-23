package com.crossroads.youtubefastplaylist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class SearchActivity extends AppCompatActivity {

    private EditText searchInput;
    private ListView videosFound;

    List<VideoItem> searchResults;
    static VideoListAdapter adapter;

    public static int activeVideoIndex = 0;

    public static DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        db = new DatabaseHandler(this);

        searchInput = (EditText) findViewById(R.id.search_input);
        videosFound = (ListView) findViewById(R.id.videos_found);


        searchResults = new ArrayList<VideoItem>();
        PlayerActivity.playlist = new ArrayList<VideoItem>();

        VideoItem videoItem = new VideoItem();
        videoItem.setId("1G4isv_Fylg");
        videoItem.setThumbnailURL("https://i.ytimg.com/vi/1G4isv_Fylg/default.jpg");
//        videoItem.setDescription("Get A Head Full Of Dreams now: – iTunes http://cldp.ly/cpitunes – Amazon http://smarturl.it/AHFODamazon – Google Play http://smarturl.it/AHFODgplay – CD ...");
        videoItem.setTitle("Coldplay - Paradise (Official Video)");

//        searchResults.add(videoItem);
//        searchResults.add(videoItem);
//        searchResults.add(videoItem);
//        searchResults.add(videoItem);
//        searchResults.add(videoItem);

        LayoutInflater inflater = getLayoutInflater();
        adapter = new VideoListAdapter(getApplicationContext(),
                R.layout.video_item, searchResults, inflater,
                VideoListAdapter.SEARCH_ITEM_LAYOUT, -1);
        videosFound.setAdapter(adapter);

        searchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    search();
                    Toast.makeText(getApplicationContext(), "searching..", Toast.LENGTH_LONG).show();
                    return false;
                }
                return true;
            }
        });

    }

    public void play(View view) {
        savePlaylist();
        Intent intent = new Intent(getApplicationContext(), PlayerActivity.class);
        startActivity(intent);
    }
    public void savePlaylist() {
        for (VideoItem videoItem: PlayerActivity.playlist) {
            db.addVideo(videoItem);
        }
    }

    public void add(View v) {
        VideoItem itemToAdd = (VideoItem)v.getTag();
        //adapter.remove(itemToRemove);
        String id = itemToAdd.getId();

        if (!db.isThere(itemToAdd) && !PlayerActivity.playlist.contains(itemToAdd)) {
            PlayerActivity.playlist.add(itemToAdd);
            Toast.makeText(getApplicationContext(), "added to playlist", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "video already exists in playlist", Toast.LENGTH_SHORT).show();
        }

        /*List<String> ids = db.getAllVideoIds();
        if (ids.contains(id)) {
            Toast.makeText(getApplicationContext(), "video already exists in playlist", Toast.LENGTH_SHORT).show();
        } else {
            PlayerActivity.playlist.add(itemToAdd);
            Toast.makeText(getApplicationContext(), "added to playlist", Toast.LENGTH_SHORT).show();
        }*/
    }


    public void search() {
        String keyword = searchInput.getText().toString();
        Log.i("keyword", keyword);
        SearchVideoTask searchVideoTask = new SearchVideoTask(getApplicationContext());
        try {
            adapter.clear();
            searchResults = searchVideoTask.execute(keyword).get();
            for (int i = 0; i < searchResults.size(); ++i) {
                adapter.add(searchResults.get(i));
            }
            Log.i("result", Integer.toString(searchResults.size()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        Log.i("info", "ending search");
    }

    private void addClickListener(){
        videosFound.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos,
                                    long id) {
                Intent intent = new Intent(getApplicationContext(), PlayerActivity.class);
                intent.putExtra("VIDEO_ID", searchResults.get(pos).getId());
                startActivity(intent);
            }

        });
    }

}
