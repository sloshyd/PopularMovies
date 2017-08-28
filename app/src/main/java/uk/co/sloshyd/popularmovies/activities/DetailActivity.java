package uk.co.sloshyd.popularmovies.activities;

import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import uk.co.sloshyd.popularmovies.Loaders.ReviewsLoader;
import uk.co.sloshyd.popularmovies.Loaders.TrailersLoader;
import uk.co.sloshyd.popularmovies.R;
import uk.co.sloshyd.popularmovies.Utils;
import uk.co.sloshyd.popularmovies.adapters.DetailReviewsAdapter;
import uk.co.sloshyd.popularmovies.data.MovieClass;
import uk.co.sloshyd.popularmovies.data.MovieContract;
import uk.co.sloshyd.popularmovies.databinding.ActivityDetailBinding;
import uk.co.sloshyd.popularmovies.sync.LoadDataService;
import uk.co.sloshyd.popularmovies.sync.Tasks;

import static uk.co.sloshyd.popularmovies.activities.ListActivity.SAVED_POSITION;


public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ContentValues[]> {


    private MovieClass mMovieData;
    private ActivityDetailBinding mBinding;
    private LoaderManager mLoadManager;
    private DetailReviewsAdapter mAdapter;
    private static final int TRAILER_LOADER_ID = 201;
    private static final int REVIEW_LOADER_ID = 203;
    private static final String YOU_TUBE_URL = "http://www.youtube.com/watch?v=";
    private static final int NUMBER_OF_TRAILERS_TO_SHOW = 3;
    public static final String TAG = DetailActivity.class.getSimpleName();
    public boolean isFavorite;
    public SavedDataBroadcastReceiver mBroadcastReceiver;
    public String mSortBy;//used to determine how data is managed in the views - data from the list of savedFavorites does not show trailers or comments
    private String SAVED_SCROLL_POSITION = "scrollPosition";
    private int mSavedPosition = 0;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //destroy the broadcastreceiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //initialize the binding object and set contentView
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        Intent intent = getIntent();
        mMovieData = intent.getParcelableExtra(Utils.INTENT_PUTEXTRA_MOVIE_DATA);
        mSortBy = intent.getStringExtra(ListActivity.ORDER_BY);
        setUpDetailLayout();//the layout varies depending if the listing is from Populat/TopRated and myFavorites
        isMoveFavorite();

        mBinding.tvDetailTitle.setText(mMovieData.getmTitle());
        mBinding.tvDetailOverview.setText(mMovieData.getmOverview());
        mBinding.tvDetailReleaseDate.setText(mMovieData.getmReleaseDate());

        String averageVoteString = Double.toString(mMovieData.getmAverageVote());
        String outOfTenVote = averageVoteString + " / 10 ";
        mBinding.viewDetailVotes.setText(outOfTenVote);
        setUpLoaders();
        //set up adapter for the reviews section
        mBinding.reviewsRecylerView.findViewById(R.id.reviews_recyler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mBinding.reviewsRecylerView.setLayoutManager(layoutManager);
        mBinding.reviewsRecylerView.setHasFixedSize(true);
        mAdapter = new DetailReviewsAdapter(this);
        mBinding.reviewsRecylerView.setAdapter(mAdapter);

        //set up broadcastReceiver and intentfilter for response for savedData
        mBroadcastReceiver = new SavedDataBroadcastReceiver();
        IntentFilter filter = new IntentFilter(Tasks.INSERT_COMPLETE);
        filter.addAction(Tasks.INSERT_FAIL);
        filter.addAction(Tasks.DELETE_COMPLETE);
        filter.addAction(Tasks.DELETE_FAIL);
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mBroadcastReceiver, filter);
        if(savedInstanceState != null){
            mSavedPosition = savedInstanceState.getInt(SAVED_SCROLL_POSITION);
            mBinding.svDetails.scrollTo(0, (int) mSavedPosition);
        }
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {

        switch (id) {
            case TRAILER_LOADER_ID:
                if (mMovieData == null)
                    return null;

                return new TrailersLoader(this, mMovieData.getId());

            case REVIEW_LOADER_ID:
                if (mMovieData == null)
                    return null;

                return new ReviewsLoader(this, mMovieData.getId());

            default:
                return null;
        }

    }

    @Override
    public void onLoadFinished(Loader loader, ContentValues[] data) {

        int id = loader.getId();

        switch (id) {
            case TRAILER_LOADER_ID:
                createViews(data);
                break;

            case REVIEW_LOADER_ID:
                mAdapter.setData(data);
                break;
            default:
                throw new IllegalArgumentException("Invalid loader id");
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putFloat(SAVED_SCROLL_POSITION,mBinding.svDetails.getY());
    }

    @Override
    public void onLoaderReset(Loader loader) {
        int id = loader.getId();

        switch (id) {
            case TRAILER_LOADER_ID:
                createViews(null);
                break;

            case REVIEW_LOADER_ID:
                mAdapter.setData(null);
                break;
            default:
                throw new IllegalArgumentException("Invalid loader id");
        }
    }


    public void createViews(ContentValues[] contentValues) {

        if (contentValues == null) {
            return;
        }

        ViewGroup viewGroup = mBinding.lvTrailers;//create a reference for viewGroup
        viewGroup.removeAllViews();// remove any views already in the viewgroup


        //max number of trailers needs to be set to 3
        int numberOfTrailers;
        if (contentValues.length < NUMBER_OF_TRAILERS_TO_SHOW) {
            numberOfTrailers = contentValues.length;
        } else {
            numberOfTrailers = NUMBER_OF_TRAILERS_TO_SHOW;
        }

        for (int i = 0; i < numberOfTrailers; i++) {

            String id = contentValues[i].getAsString(Utils.CV_YOUTUBE_ID_KEY);
            String name = contentValues[i].getAsString(Utils.CV_TRAILER_TITLE_KEY);
            View newView = getView();
            bindViewData(newView, name, id);
            viewGroup.addView(newView);
        }
    }

    public View getView() {

        return View.inflate(getBaseContext(), R.layout.trailer_list, null);
    }

    public View bindViewData(View v, String title, final String trailerID) {

        TextView tvTrailer = (TextView) v.findViewById(R.id.tv_trailer_title);
        tvTrailer.setText(title);
        ImageView playIcon = (ImageView) v.findViewById(R.id.iv_play_trailer_icon);
        playIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    launchTrailerIntent(v, trailerID);
            }
        });
        ImageView shareIcon = (ImageView) v.findViewById(R.id.iv_share_trailer_icon);
        shareIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchTrailerIntent(v, trailerID);
            }
        });
        return v;
    }

    public void launchTrailerIntent(View v, String id) {
        int viewId = v.getId();

        Uri uri = Uri.parse(YOU_TUBE_URL + id);
        switch (viewId) {

            case R.id.iv_play_trailer_icon:
                Intent launchVideo = new Intent(Intent.ACTION_VIEW, uri);
                if(launchVideo.resolveActivity(getPackageManager())!=null){
                    startActivity(launchVideo);
                }

                break;
            case R.id.iv_share_trailer_icon:
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, uri.toString());

                if(sharingIntent.resolveActivity(getPackageManager())!= null){
                    startActivity(Intent.createChooser(sharingIntent, "Share using"));
                }

                break;
            default:
                break;
        }
    }

    public void addToFavorites(View v) {

        Intent serviceIntent = new Intent(this, LoadDataService.class);

        if (!isFavorite) {
            byte[] imageInByte = Utils.getImageDataFromView(mBinding.imageViewDetailPoster);
            mMovieData.setPosterData(imageInByte);
            serviceIntent.setData(MovieContract.CONTENT_URI);
            serviceIntent.setAction(Tasks.ACTION_ADD_RECORD);
            serviceIntent.putExtra("data", mMovieData);
            startService(serviceIntent);
        } else {
            Uri deleteUri = Uri.parse(MovieContract.CONTENT_URI.toString())
                    .buildUpon()
                    .appendPath(mMovieData.getId())
                    .build();
            serviceIntent.setData(deleteUri);
            serviceIntent.setAction(Tasks.ACTION_DELETE_RECORD);
            startService(serviceIntent);
        }
    }


    public void isMoveFavorite() {
        //this is just done as an async task as this is a one time operation
        CheckMovieFavoriteTask checkMovieFavoriteTask = new CheckMovieFavoriteTask();
        checkMovieFavoriteTask.execute(mMovieData);

    }

    public void setUpDetailLayout(){
        //MyFavorites list does not display reviews or trailers
        if(mSortBy.equals(MovieContract.CONTENT_URI.toString())){
            mBinding.lvTrailers.setVisibility(View.INVISIBLE);
            mBinding.tvHeaderReviews.setVisibility(View.INVISIBLE);
            mBinding.tvTopMoviesHeading.setVisibility(View.INVISIBLE);
        }else{
            mBinding.tvHeaderReviews.setVisibility(View.VISIBLE);
            mBinding.tvTopMoviesHeading.setVisibility(View.VISIBLE);
            mBinding.lvTrailers.setVisibility(View.VISIBLE);
        }
    }

    public class CheckMovieFavoriteTask extends AsyncTask<MovieClass, String, Boolean> {

        @Override
        protected void onPostExecute(Boolean inFavorites) {
            if (inFavorites) {
                mBinding.button.setText(R.string.btn_not_favorite);
                isFavorite = true;
                loadImage();

            } else {
                mBinding.button.setText(R.string.btn_favorite);
                isFavorite = false;
                loadImage();
            }
            setUpLoaders();
        }

        @Override
        protected Boolean doInBackground(MovieClass... params) {

            String id = params[0].getId();
            Uri newUri = Uri.parse(MovieContract.CONTENT_URI.toString())
                    .buildUpon()
                    .appendPath(id)
                    .build();

            String[] projection = new String[]{MovieContract.MovieEntry.COLUMN_NAME_MOVIE_ID};

            Cursor cursor = getContentResolver().query(newUri, projection, null, null, null);

            if (cursor.getCount() == 1) {
                cursor.close();
                return true;
            } else {
                cursor.close();
                return false;
            }
        }
    }

    public void setUpLoaders() {
        //initialize loaders
        if (mSortBy.equals(MovieContract.CONTENT_URI.toString())) {
            return;// do not load review and trailers if movie is favorite
        } else {
            mLoadManager = getLoaderManager();
            mLoadManager.initLoader(TRAILER_LOADER_ID, null, this);
            mLoadManager.initLoader(REVIEW_LOADER_ID, null, this);
        }
    }

    public void loadImage() {
        if (isFavorite) {
            Bitmap poster = Utils.getPoster(mMovieData.getmPoster());
            mBinding.imageViewDetailPoster.setImageBitmap(poster);
        } else {
            Utils.loadPosterImage(mBinding.imageViewDetailPoster, DetailActivity.this, mMovieData.getmPosterPath());
        }

    }

    public void updateAddRemoveButton() {
        if (isFavorite) {
            mBinding.button.setText(R.string.btn_favorite);
        } else {
            mBinding.button.setText(R.string.btn_not_favorite);
        }
        isFavorite = !isFavorite;
    }

    //create a customBroadcastReceiver to handle response from service
    private class SavedDataBroadcastReceiver extends BroadcastReceiver {

        private SavedDataBroadcastReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Tasks.INSERT_COMPLETE)) {
                Toast.makeText(getBaseContext(), R.string.toast_message_successfully_added_move, Toast.LENGTH_SHORT).show();
                updateAddRemoveButton();
            } else if (action.equals(Tasks.INSERT_FAIL)) {
                Toast.makeText(getBaseContext(), R.string.toast_message_unsuccessfully_add_move, Toast.LENGTH_SHORT).show();
                updateAddRemoveButton();
            } else if (action.equals(Tasks.DELETE_COMPLETE)) {
                Toast.makeText(getBaseContext(), R.string.delete_sucessful_message, Toast.LENGTH_SHORT).show();
                updateAddRemoveButton();
            } else if (action.equals(Tasks.DELETE_FAIL)) {
                Toast.makeText(getBaseContext(), R.string.delete_unsucessful_message, Toast.LENGTH_SHORT).show();
                updateAddRemoveButton();
            } else {
                throw new IllegalArgumentException("Invalid response from broadcast");
            }
        }
    }
}




