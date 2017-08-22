package uk.co.sloshyd.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import uk.co.sloshyd.popularmovies.data.MovieContract.MovieEntry;

/**
 * Created by Darren on 03/08/2017.
 */

public class MovieDatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "movie.db";
    public static final int DATABASE_VERSION = 1;
    public static final String SQL_CREATE_MOVIES_TABLE =
            "CREATE TABLE " + MovieEntry.MOVIES_TABLE_NAME + " (" +
                    MovieEntry.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    MovieEntry.COLUMN_NAME_MOVIE_TITLE + " TEXT NOT NULL, " +
                    MovieEntry.COLUMN_NAME_RELEASE_DATE + " TEXT NOT NULL, " +
                    MovieEntry.COLUMN_NAME_MOVIE_RATING + " TEXT NOT NULL, " +
                    MovieEntry.COLUMN_NAME_MOVIE_DESCRIPTION + " TEXT NOT NULL, " +
                    MovieEntry.COLUMN_NAME_MOVIE_POSTER + " BLOB NOT NULL, " +
                    MovieEntry.COLUMN_NAME_MOVIE_ID + " TEXT NOT NULL UNIQUE" +
                    ");";

    public static final String SQL_DROP_MOVIE_TABLE = "DROP TABLE IF EXISTS " + MovieEntry.MOVIES_TABLE_NAME;


    public MovieDatabaseHelper(Context context){
        super(context,DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
       db.execSQL(SQL_CREATE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
       db.execSQL(SQL_DROP_MOVIE_TABLE);
    }
}
