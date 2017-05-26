package uk.co.sloshyd.popularmovies;


import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import uk.co.sloshyd.popularmovies.data.MovieClass;

public class ListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<MovieClass[]> ,
        MovieAdapter.MovieAdapterOnClickHandler{

    public static final int LOADER_ID = 1;

    private LoaderManager mLoadManager;
    private MovieAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private TextView mErrorTextView;
    private ProgressBar mProgressBar;
    private MovieClass[] mMovieClasses;
    //sharedPreference
    private String mSortOrder;
    SharedPreferences mSharedPreferences;
    private static final String SORT_BY_DEFAULT = Utils.POPULAR_MOVIE_URL;//sets default option to Popular movie
    public static final String ORDER_BY = "order";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        //setup preference
        mSharedPreferences = getPreferences(MODE_PRIVATE);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        setUpLoader();
        //set up Error message for no returned data
        mErrorTextView = (TextView) findViewById(R.id.tv_error_message_display);
        //get reference to RecyclerView
        mRecyclerView = (RecyclerView) findViewById(R.id.recyler_view);
        //set up layout manager
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new MovieAdapter(this, this);
        mRecyclerView.setAdapter(mAdapter);

    }

    public void setUpLoader(){
        //set up loadManager
        mSortOrder = mSharedPreferences.getString(ORDER_BY,SORT_BY_DEFAULT); //the default is to sort by popular if there is no entry or an error then the default will be selected
        mLoadManager = getLoaderManager();
        mLoadManager.initLoader(LOADER_ID, null, this);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    public void showLoadingErrorMessage(){
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorTextView.setVisibility(View.VISIBLE);
    }

    public void hideLoadingErrorMessage(){
        mRecyclerView.setVisibility(View.VISIBLE);
        mErrorTextView.setVisibility(View.INVISIBLE);
    }



    @Override
    public Loader<MovieClass[]> onCreateLoader(int i, Bundle bundle) {

        if(mSortOrder == Utils.POPULAR_MOVIE_URL ){

            return new MovieDataLoader(this, Utils.TOP_RATED_MOVIE_URL);
        } else {
            return new MovieDataLoader(this, Utils.POPULAR_MOVIE_URL);
        }

    }

    @Override
    public void onLoadFinished(Loader<MovieClass[]> loader, MovieClass[] movieClasses) {

        mProgressBar.setVisibility(View.INVISIBLE);
        mMovieClasses = movieClasses;
        if(movieClasses == null){
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
    public void onClick(MovieClass movieDataItem) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(Utils.INTENT_PUTEXTRA_MOVIE_DATA, movieDataItem);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_menu, menu);

        if(mSharedPreferences.getString(ORDER_BY, SORT_BY_DEFAULT).equals( Utils.POPULAR_MOVIE_URL)){
            invalidateOptionsMenu();
            setTitle(R.string.popular_movies_menu_title);
        } else {
            invalidateOptionsMenu();
            setTitle(R.string.top_rated_movies_menu_title);
        }
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences.Editor editor;
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            case R.id.action_most_popular:
                mLoadManager.destroyLoader(LOADER_ID);
                editor = mSharedPreferences.edit();
                editor.putString(ORDER_BY, Utils.POPULAR_MOVIE_URL);
                editor.commit();
                setUpLoader();
                invalidateOptionsMenu();//set to force menu to check for correct title option
                break;

            case R.id.action_top_rated:
                mLoadManager.destroyLoader(LOADER_ID);
                editor = mSharedPreferences.edit();
                editor.putString(ORDER_BY, Utils.TOP_RATED_MOVIE_URL);
                editor.commit();
                setUpLoader();
                invalidateOptionsMenu();//set to force menu to check for correct title option
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}
