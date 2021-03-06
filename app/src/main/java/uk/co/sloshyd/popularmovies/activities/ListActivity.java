package uk.co.sloshyd.popularmovies.activities;


import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import uk.co.sloshyd.popularmovies.Loaders.MovieDataLoader;
import uk.co.sloshyd.popularmovies.Loaders.SavedDataLoader;
import uk.co.sloshyd.popularmovies.R;
import uk.co.sloshyd.popularmovies.Utils;
import uk.co.sloshyd.popularmovies.adapters.MovieAdapter;
import uk.co.sloshyd.popularmovies.data.MovieClass;
import uk.co.sloshyd.popularmovies.data.MovieContract;

public class ListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<MovieClass[]>,
        MovieAdapter.MovieAdapterOnClickHandler {

    public static final int INTERNET_DATA_LOADER_ID = 1;
    private static final String TAG = ListActivity.class.getSimpleName();
    private LoaderManager mLoadManager;
    private MovieAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private TextView mErrorTextView;
    private ProgressBar mProgressBar;
    private MovieClass[] mMovieClasses;
    private TextView mNoData;//view to display when no data is added to myMovies but list is selected
    //sharedPreference
    private String mSortOrder;
    SharedPreferences mSharedPreferences;
    private static final String SORT_BY_DEFAULT = Utils.POPULAR_MOVIE_URL;//sets default option to Popular movie
    public static final String ORDER_BY = "order";
    //save state - save position in the RecyleView
    public static final String SAVED_POSITION = "savedPosition";
    private int mSavedPosition = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        if(savedInstanceState !=null){
            mSavedPosition = savedInstanceState.getInt(SAVED_POSITION);
        }
        //setup preference
        mSharedPreferences = getPreferences(MODE_PRIVATE);

        setCustomTitle();
        mNoData = (TextView) findViewById(R.id.tv_no_data_message_display);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        //set up Error message for no returned data
        mErrorTextView = (TextView) findViewById(R.id.tv_error_message_display);
        //get reference to RecyclerView
        mRecyclerView = (RecyclerView) findViewById(R.id.receyler_view);
        //set up layout manager
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new MovieAdapter(this, this);
        mRecyclerView.setAdapter(mAdapter);
        setUpLoader();

    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mSavedPosition = ((LinearLayoutManager)mRecyclerView.getLayoutManager()).findFirstVisibleItemPosition();
        outState.putInt(SAVED_POSITION, mSavedPosition);
    }

    private void setCustomTitle() {

        if (mSharedPreferences.getString(ORDER_BY, SORT_BY_DEFAULT).equals(Utils.TOP_RATED_MOVIE_URL)) {
            setTitle(R.string.top_rated_movies_menu_title);
        } else if (mSharedPreferences.getString(ORDER_BY, SORT_BY_DEFAULT).equals(Utils.POPULAR_MOVIE_URL)) {
            setTitle(R.string.popular_movies_menu_title);
        } else {
            setTitle(R.string.my_favorite_movies_menu_title);
        }
    }

    public void setUpLoader() {
        //set up loadManager
        mSortOrder = mSharedPreferences.getString(ORDER_BY, SORT_BY_DEFAULT); //the default is to sort by popular if there is no entry or an error then the default will be selected
        mLoadManager = getLoaderManager();
        mLoadManager.initLoader(INTERNET_DATA_LOADER_ID, null, this);
        mProgressBar.setVisibility(View.VISIBLE);
        //restore position in list after change to state - will be default 0
        mRecyclerView.getLayoutManager().scrollToPosition(mSavedPosition);
    }

    public void showLoadingErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);

        if(mSortOrder.equals(MovieContract.CONTENT_URI.toString())){
            mErrorTextView.setVisibility(View.INVISIBLE);
            mNoData.setVisibility(View.VISIBLE);
        }else{
            mErrorTextView.setVisibility(View.VISIBLE);
        }

    }

    public void hideLoadingErrorMessage() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mErrorTextView.setVisibility(View.INVISIBLE);
        mNoData.setVisibility(View.INVISIBLE);
    }


    @Override
    public Loader<MovieClass[]> onCreateLoader(int i, Bundle bundle) {

        if (mSortOrder.equals(Utils.TOP_RATED_MOVIE_URL)) {
            return new MovieDataLoader(this, Utils.TOP_RATED_MOVIE_URL);
        } else if (mSortOrder.equals(Utils.POPULAR_MOVIE_URL)) {
            return new MovieDataLoader(this, Utils.POPULAR_MOVIE_URL);
        } else {
            return new SavedDataLoader(this);
        }

    }

    @Override
    public void onLoadFinished(Loader<MovieClass[]> loader, MovieClass[] movieClasses) {

        mProgressBar.setVisibility(View.INVISIBLE);
        mMovieClasses = movieClasses;
        if (movieClasses == null || movieClasses.length == 0) {
            showLoadingErrorMessage();
        } else {
            hideLoadingErrorMessage();
        }
        mAdapter.setData(mMovieClasses);

    }

    @Override
    public void onLoaderReset(Loader<MovieClass[]> loader) {
        mAdapter.setData(null);
    }


    @Override
    public void onClick(MovieClass movieDataItem, ImageView imageView) {
        byte[] poster = Utils.getImageDataFromView(imageView);
        movieDataItem.setPosterData(poster);
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(Utils.INTENT_PUTEXTRA_MOVIE_DATA, movieDataItem);
        intent.putExtra(ORDER_BY, mSortOrder);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_menu, menu);
        invalidateOptionsMenu();
        setCustomTitle();
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences.Editor editor;
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            case R.id.action_most_popular:
                mLoadManager.destroyLoader(INTERNET_DATA_LOADER_ID);
                editor = mSharedPreferences.edit();
                editor.putString(ORDER_BY, Utils.POPULAR_MOVIE_URL);
                editor.commit();
                setUpLoader();
                invalidateOptionsMenu();//set to force menu to check for correct title option
                break;

            case R.id.action_top_rated:
                mLoadManager.destroyLoader(INTERNET_DATA_LOADER_ID);
                editor = mSharedPreferences.edit();
                editor.putString(ORDER_BY, Utils.TOP_RATED_MOVIE_URL);
                editor.commit();
                setUpLoader();
                invalidateOptionsMenu();//set to force menu to check for correct title option
                break;

            case R.id.action_my_favorites:
                mLoadManager.destroyLoader(INTERNET_DATA_LOADER_ID);
                editor = mSharedPreferences.edit();
                editor.putString(ORDER_BY, MovieContract.CONTENT_URI.toString());
                editor.commit();
                setUpLoader();
                invalidateOptionsMenu();
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}
