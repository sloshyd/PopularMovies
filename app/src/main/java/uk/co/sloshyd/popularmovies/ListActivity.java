package uk.co.sloshyd.popularmovies;


import android.app.LoaderManager;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import uk.co.sloshyd.popularmovies.data.MovieClass;

public class ListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<MovieClass[]> {

    public static final int LOADER_ID = 1;
    private LoaderManager mLoadManager;
    private MovieAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private MovieClass[] mMovieClasses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        //set up loadManager
        mLoadManager = getLoaderManager();
        mLoadManager.initLoader(LOADER_ID, null, this);
        Log.i("TAG", "LOADING LOADER");
        //get reference to RecyclerView
        mRecyclerView = (RecyclerView) findViewById(R.id.recyler_view);
        //set up layout manager
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new MovieAdapter();
        mRecyclerView.setAdapter(mAdapter);


    }

    @Override
    public Loader<MovieClass[]> onCreateLoader(int i, Bundle bundle) {
        return new MovieDataLoader(this, Utils.POPULAR_MOVIE_URL);
    }

    @Override
    public void onLoadFinished(Loader<MovieClass[]> loader, MovieClass[] movieClasses) {

        mMovieClasses = movieClasses;
        mAdapter.setData(mMovieClasses);
        Log.i("TAG", " loaded data " + movieClasses.length);
    }

    @Override
    public void onLoaderReset(Loader<MovieClass[]> loader) {
        mAdapter.setData(null);
    }
}
