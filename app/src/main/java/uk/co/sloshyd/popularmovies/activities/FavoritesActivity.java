package uk.co.sloshyd.popularmovies.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import uk.co.sloshyd.popularmovies.R;
import uk.co.sloshyd.popularmovies.adapters.FavoritesAdapter;
import uk.co.sloshyd.popularmovies.data.MovieContract;

/**
 * Created by Darren on 19/08/2017.
 */

public class FavoritesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final String TAG = FavoritesActivity.class.getSimpleName();
    public static final int LOCAL_DATA_LOADER_ID = 2;
    private RecyclerView mRecyclerView;
    private FavoritesAdapter mAdapter;
    private TextView mTvNoData;


    public FavoritesActivity(){

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        setTitle(R.string.my_favorite_movies_menu_title);

        //set up views
        mTvNoData = (TextView) findViewById(R.id.tv_no_favorites_warning);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_favorite_movies);

        getSupportLoaderManager().initLoader(LOCAL_DATA_LOADER_ID, null,this);


        //set up layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new FavoritesAdapter();
        mRecyclerView.setAdapter(mAdapter);

    }



    public void showNoDataMessage(){
        mRecyclerView.setVisibility(View.INVISIBLE);
        mTvNoData.setVisibility(View.VISIBLE);
    }

    public void showData(){
        mRecyclerView.setVisibility(View.VISIBLE);
        mTvNoData.setVisibility(View.INVISIBLE);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(this,MovieContract.CONTENT_URI,null,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
        //display appropriate screen
        if(data.getCount() == 0 || data == null){
            showNoDataMessage();
        } else {
            showData();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }


}
