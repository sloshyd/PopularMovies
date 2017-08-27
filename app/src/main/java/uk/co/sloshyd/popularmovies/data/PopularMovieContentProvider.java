package uk.co.sloshyd.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Darren on 03/08/2017.
 */

public class PopularMovieContentProvider extends ContentProvider {

    //constants used by ContentResolver to determine if table or item level access is needed
    private static final int MOVIES_DIRECTORY = 100;
    private static final int MOVIES_ITEM = 101;
    private static final String TAG = PopularMovieContentProvider.class.getSimpleName();
    private MovieDatabaseHelper mDbHelper;

    @Override
    public boolean onCreate() {

        mDbHelper = new MovieDatabaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        int match = sUriMatcher.match(uri);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor;

        switch (match) {
            case MOVIES_DIRECTORY:

                cursor = db.query(
                        MovieContract.MovieEntry.MOVIES_TABLE_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        MovieContract.MovieEntry.COLUMN_NAME_MOVIE_TITLE
                );
                break;

            case MOVIES_ITEM:

                selection = MovieContract.MovieEntry.COLUMN_NAME_MOVIE_ID + "=?";//selection field
                String movieId = uri.getLastPathSegment();
                selectionArgs = new String[]{movieId};//selection arg

                cursor = db.query(
                        MovieContract.MovieEntry.MOVIES_TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null
                );
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI");
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = sUriMatcher.match(uri);

        String mimeType;
        switch (match) {
            case MOVIES_ITEM:
                mimeType = "vnd.android.cursor.dir/co.uk.sloshyd.provider.movies";
                break;
            case MOVIES_DIRECTORY:
                mimeType = "vnd.android.cursor.dir/co.uk.sloshyd.provider.movie";
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI");

        }
        return mimeType;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIES_DIRECTORY:
                SQLiteDatabase sqLiteDatabase = mDbHelper.getWritableDatabase();
                try {
                    long id = sqLiteDatabase.insert(MovieContract.MovieEntry.MOVIES_TABLE_NAME, null, values);
                    if (id == -1) {
                        return null; //IMPORTANT to let calling method know that the insertion failed
                    }
                    // must let loader know that something has changed
                    getContext().getContentResolver().notifyChange(uri, null);
                    break;
                } catch (IllegalArgumentException e) {
                    Log.i(TAG, "Error loading data");
                }

        default:
        throw new IllegalArgumentException("Invalid Insertion Uri");
    }

        return uri;

}


    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        SQLiteDatabase sqLiteDatabase = mDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        int rowsDeleted;
        switch (match) {
            case MOVIES_ITEM:

                selection = MovieContract.MovieEntry.COLUMN_NAME_MOVIE_ID + "=?";//selection field
                String movieId = uri.getLastPathSegment();
                selectionArgs = new String[]{movieId};//selection arg

                rowsDeleted = sqLiteDatabase.delete(
                        MovieContract.MovieEntry.MOVIES_TABLE_NAME,
                        selection,
                        selectionArgs
                );

                break;
            default:
                throw new IllegalArgumentException("Unable to delete URI is not supported");
        }
        return rowsDeleted;

    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
        //Not supported in current version
    }

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

// Static initializer. This is run the first time anything is called from this class.
static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        sUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY,MovieContract.MOVIES_PATH,MOVIES_DIRECTORY);
        sUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY,MovieContract.MOVIES_PATH+"/#",MOVIES_ITEM);// if string use * unless specific string
        }

        }
