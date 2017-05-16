package uk.co.sloshyd.popularmovies;


import android.app.LoaderManager;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import uk.co.sloshyd.popularmovies.data.MovieClass;

public class ListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<MovieClass[]> {

    public static final int LOADER_ID = 1;
    private LoaderManager mLoadManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        mLoadManager = getLoaderManager();
        mLoadManager.initLoader(LOADER_ID, null, this);
        Log.i("TAG", "LOADING LOADER");

    }

    @Override
    public Loader<MovieClass[]> onCreateLoader(int i, Bundle bundle) {
        return new MovieDataLoader(this, Utils.POPULAR_MOVIE_URL);
    }

    @Override
    public void onLoadFinished(Loader<MovieClass[]> loader, MovieClass[] movieClasses) {

        MovieClass[] data = movieClasses;
        Log.i("TAG", " loaded data " + data.length);
    }

    @Override
    public void onLoaderReset(Loader<MovieClass[]> loader) {

    }
}
