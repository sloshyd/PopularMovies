package uk.co.sloshyd.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Darren on 03/08/2017.
 */

public final class MovieContract {

    public static final String CONTENT_AUTHORITY = "uk.co.sloshyd.popularmovies";
    //To get usable URI we parse string
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String MOVIES_PATH = "movie";
    //Uri for table level content
    public static Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, MOVIES_PATH);

    private MovieContract(){
        throw new AssertionError("Cannot Instantiate MovieContract");
    }

    public static final class MovieEntry implements BaseColumns{
        public static final String MOVIES_TABLE_NAME = "movies";
        //PRIMARY KEY AUTOINCREMENT
        public static final String COLUMN_NAME_ID = BaseColumns._ID;
        //TEXT NOT NULL
        public static final String COLUMN_NAME_MOVIE_TITLE = "title";
        //TEXT
        public static final String COLUMN_NAME_RELEASE_DATE ="release";
        //LONG
        public static final String COLUMN_NAME_MOVIE_RATING = "rating";
        //TEXT NOT NULL
        public static final String COLUMN_NAME_MOVIE_DESCRIPTION ="description";
        //BLOB NOT NULL
        public static final String COLUMN_NAME_MOVIE_POSTER = "poster";
        //TEXT UNIQUE NOT NULL refers to the TMDB movie reference made uniqe to only have one version of a film in db
        public static final String COLUMN_NAME_MOVIE_ID ="movie_id";
    }
}
