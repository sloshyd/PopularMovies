package uk.co.sloshyd.popularmovies.sync;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import uk.co.sloshyd.popularmovies.Utils;
import uk.co.sloshyd.popularmovies.data.MovieClass;
import uk.co.sloshyd.popularmovies.data.MovieContract;

import static android.content.ContentValues.TAG;

/**
 * Created by Darren on 23/08/2017.
 */

public class Tasks {

    public static final String ACTION_ADD_RECORD = "add_record";
    public static final String ACTION_DELETE_RECORD = "delete_record";
    //response codes
    public static final String INSERT_COMPLETE = "insert_complete";
    public static final String INSERT_FAIL = "insert_fail";
    public static final String DELETE_COMPLETE = "delete_complete";
    public static final String DELETE_FAIL ="delete_fail";


    public static void executeTask(Context context, Intent intent) {

        String action = intent.getAction();
        Log.i(TAG, "ACTION " + action);

        if (ACTION_ADD_RECORD.equals(action)) {
            addRecord(context, intent);
        } else if (ACTION_DELETE_RECORD.equals(action)) {
            deleteRecord(context, intent);
        } else{
            throw new IllegalArgumentException("Invalid task " + action);
        }

    }


    private static void addRecord(Context context, Intent intent){
        Uri uri = intent.getData();
        MovieClass movieClass = intent.getExtras().getParcelable("data");
        ContentValues contentValues = new ContentValues();

        contentValues.put(MovieContract.MovieEntry.COLUMN_NAME_MOVIE_ID,movieClass.getId());
        contentValues.put(MovieContract.MovieEntry.COLUMN_NAME_MOVIE_DESCRIPTION,movieClass.getmOverview());
        contentValues.put(MovieContract.MovieEntry.COLUMN_NAME_MOVIE_POSTER, movieClass.getmPoster());
        contentValues.put(MovieContract.MovieEntry.COLUMN_NAME_RELEASE_DATE, movieClass.getmReleaseDate());
        contentValues.put(MovieContract.MovieEntry.COLUMN_NAME_MOVIE_RATING, movieClass.getmAverageVote());
        contentValues.put(MovieContract.MovieEntry.COLUMN_NAME_MOVIE_TITLE, movieClass.getmTitle());

        Uri inserted = context.getContentResolver().insert(uri,contentValues);
        Intent responseIntent = new Intent();
        if(inserted != null ){
            responseIntent.setAction(Tasks.INSERT_COMPLETE);
        } else{
            responseIntent.setAction(Tasks.INSERT_FAIL);
        }
        LocalBroadcastManager.getInstance(context).sendBroadcast(responseIntent);

    }

    private static void deleteRecord(Context context, Intent intent){
        Uri uri = intent.getData();

        int rowsDeleted = context.getContentResolver().delete(uri,null,null);
        Intent responseIntent = new Intent();
        if(rowsDeleted != 0){
            responseIntent.setAction(Tasks.DELETE_COMPLETE);
        } else{
            responseIntent.setAction(Tasks.DELETE_FAIL);
        }
        LocalBroadcastManager.getInstance(context).sendBroadcast(responseIntent);
    }



}
