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
 * Created by Darren on 07/08/2017.
 */

public class TrailersLoader extends AsyncTaskLoader<ContentValues[]> {

    private URL mDataUri;

    public TrailersLoader(Context context, String url) {
        super(context);

        try {

        mDataUri = new URL(Utils.getTrailersURL(url));
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
        ContentValues[] contentValues = Utils.parseMovieTrailers(response);


        return contentValues;
    }
}
