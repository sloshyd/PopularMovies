package uk.co.sloshyd.popularmovies.Loaders;

import android.content.AsyncTaskLoader;
import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import uk.co.sloshyd.popularmovies.Utils;

import static android.content.ContentValues.TAG;

/**
 * Created by Darren on 15/08/2017.
 */

public class ReviewsLoader extends AsyncTaskLoader<ContentValues[]> {

    private URL mDataUri;

    public ReviewsLoader(Context context, String url) {
        super(context);

        try {

            mDataUri = new URL(Utils.getMovieReviewsURL(url));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public ContentValues[] loadInBackground() {
        String response = null;

        try {

            response = Utils.getResponseFromHttpUrl(mDataUri);

        }catch (IOException e){
            Log.e(TAG, "Error loading trailer data " + e);
        }
        ContentValues[] contentValues = Utils.parseMovieReviews(response);


        return contentValues;
    }

}
