package com.crossroads.youtubefastplaylist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by crossroads on 6/6/16.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    // All static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "playlist";

    // playlist table name
    private static final String TABLE_NAME = "playlist";

    // playlist table column names
    private static final String KEY_ID = "id";
    private static final String KEY_VIDEO_ID = "videoId";
    private static final String KEY_TITLE = "title";
    private static final String KEY_THUMBNAIL_URL = "thumbnailURL";
    private static final String KEY_DESCRIPTION = "description";

    private static final String[] PLAYLIST_COLUMNS = {
            KEY_ID,
            KEY_VIDEO_ID,
            KEY_TITLE,
            KEY_THUMBNAIL_URL,
            KEY_DESCRIPTION
    };
    private static final int ID_INDEX = 0;
    private static final int VIDEO_ID_INDEX = 1;
    private static final int TITLE_INDEX = 2;
    private static final int THUMBNAIL_INDEX = 3;
    private static final int DESCRIPTION_INDEX = 4;


    // constructor
    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // creating tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PLAYLIST_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_VIDEO_ID + " TEXT NOT NULL, "
                + KEY_TITLE + " TEXT NOT NULL, "
                + KEY_THUMBNAIL_URL + " TEXT, "
                + KEY_DESCRIPTION + " TEXT" + ")";

        Log.i("create table query", CREATE_PLAYLIST_TABLE);

        // execute the statement
        db.execSQL(CREATE_PLAYLIST_TABLE);
    }

    // upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        // create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) operations
     */

    // Adding new VideoItem
    void addVideo(VideoItem videoItem) {
        // get a writable database
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_VIDEO_ID, videoItem.getId());
        values.put(KEY_TITLE, videoItem.getTitle());
        values.put(KEY_THUMBNAIL_URL, videoItem.getThumbnailURL());
        values.put(KEY_DESCRIPTION, videoItem.getDescription());

        // inserting a row
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    // Getting a single videoItem
    VideoItem getVideo(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME,
                PLAYLIST_COLUMNS,
                KEY_ID + "=?",
                new String[] {String.valueOf(id)},
                null,
                null,
                null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        VideoItem videoItem = new VideoItem(cursor.getString(VIDEO_ID_INDEX),
                cursor.getString(TITLE_INDEX),
                cursor.getString(THUMBNAIL_INDEX),
                cursor.getString(DESCRIPTION_INDEX));

        return videoItem;
    }

    // Getting all videos
    public List<VideoItem> getAllVideos() {
        List<VideoItem> videoItemList = new ArrayList<VideoItem>();

        // SELECT ALL query
        String query = "SELECT * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                VideoItem videoItem = new VideoItem();
                videoItem.setId(cursor.getString(VIDEO_ID_INDEX));
                videoItem.setTitle(cursor.getString(TITLE_INDEX));
                videoItem.setThumbnailURL(cursor.getString(THUMBNAIL_INDEX));
                videoItem.setDescription(cursor.getString(DESCRIPTION_INDEX));
                // adding videoItem to list
                videoItemList.add(videoItem);
            } while (cursor.moveToNext());
        }

        return videoItemList;
    }

    // Getting all videoIds
    public List<String> getAllVideoIds() {
        List<String> videoIds = new ArrayList<String>();

        // SELECT ALL query
        String query = "SELECT * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                videoIds.add(cursor.getString(VIDEO_ID_INDEX));
            } while (cursor.moveToNext());
        }

        return videoIds;
    }

    // Updating a single videoItem
    public int updateVideo(VideoItem videoItem) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_VIDEO_ID, videoItem.getId());
        values.put(KEY_TITLE, videoItem.getTitle());
        values.put(KEY_THUMBNAIL_URL, videoItem.getThumbnailURL());
        values.put(KEY_DESCRIPTION, videoItem.getDescription());

        // updating row
        return db.update(TABLE_NAME,
                values,
                KEY_VIDEO_ID + "=?",
                new String[] {videoItem.getId()});
    }

    // Deleting a single contact
    public void deleteVideo(VideoItem videoItem) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME,
                KEY_VIDEO_ID + " = ?",
                new String[]{videoItem.getId()});
        db.close();
    }

    // getting videos count
    public int getCount() {
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        int count = cursor.getCount();
        cursor.close();

        // return count;
        return count;
    }

    // bulk insert
    public void bulkInsert(List<VideoItem> videoItemList) {
        SQLiteDatabase db = this.getWritableDatabase();
        int c = db.delete(TABLE_NAME, null, null);
        Log.i("no of rows deleted", String.valueOf(c));

        for (VideoItem item : videoItemList) {
            addVideo(item);
        }
    }

    // to check if a given VideoItem exists or not, return true if exists
    public boolean isThere(VideoItem videoItem) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME,
                new String[]{KEY_VIDEO_ID},
                KEY_VIDEO_ID + "=?",
                new String[]{videoItem.getId()},
                null,
                null,
                null);

        if (cursor.getCount() > 0) {
            return true;
        } else {
            return false;
        }
    }
}
