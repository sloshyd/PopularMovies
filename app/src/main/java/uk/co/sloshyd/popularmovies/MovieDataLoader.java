package uk.co.sloshyd.popularmovies;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import uk.co.sloshyd.popularmovies.data.MovieClass;

/**
 * Created by Darren on 15/05/2017.
 */

public class MovieDataLoader extends AsyncTaskLoader<MovieClass[]> {

    URL mDataUri;

    public MovieDataLoader (Context context, String url){
        super(context);

        try {
            mDataUri = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public MovieClass[] loadInBackground() {

        Log.i("TAG", "LOAD IN BACKGROUND");
        String response = null;
        try {
            response = Utils.getResponseFromHttpUrl(mDataUri);
            Log.i("TAG", "response :" + response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        MovieClass[] data = Utils.parseJsonString(response);

        return data;
    }
}
