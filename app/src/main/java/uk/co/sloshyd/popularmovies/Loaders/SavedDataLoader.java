package uk.co.sloshyd.popularmovies.Loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.util.Log;

import uk.co.sloshyd.popularmovies.data.MovieClass;
import uk.co.sloshyd.popularmovies.data.MovieContract;

/**
 * Created by Darren on 24/08/2017.
 */

public class SavedDataLoader extends AsyncTaskLoader<MovieClass[]> {

    public static final String TAG = SavedDataLoader.class.getSimpleName();

    public SavedDataLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public MovieClass[] loadInBackground() {

        Cursor cursor = getContext().getContentResolver().query(MovieContract.CONTENT_URI,null,null,null,null);

        MovieClass[] movieClasses = new MovieClass[cursor.getCount()];
        //turn cursor to MovieClass[]
        cursor.moveToFirst();

            for(int i = 0; i < cursor.getCount();i++) {
                cursor.moveToPosition(i);
                String title = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_NAME_MOVIE_TITLE));
                String overView = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_NAME_MOVIE_DESCRIPTION));
                String date = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_NAME_RELEASE_DATE));
                double vote = cursor.getDouble(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_NAME_MOVIE_RATING));
                byte[] poster = cursor.getBlob(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_NAME_MOVIE_POSTER));
                String movieID = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_NAME_MOVIE_ID));
                movieClasses[i]= new MovieClass(overView, date, title, vote, movieID, poster);

            }

        cursor.close();
        return movieClasses;
    }
}
