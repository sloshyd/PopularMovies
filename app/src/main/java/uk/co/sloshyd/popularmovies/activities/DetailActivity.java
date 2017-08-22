package uk.co.sloshyd.popularmovies.activities;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

import uk.co.sloshyd.popularmovies.Loaders.ReviewsLoader;
import uk.co.sloshyd.popularmovies.Loaders.TrailersLoader;
import uk.co.sloshyd.popularmovies.R;
import uk.co.sloshyd.popularmovies.Utils;
import uk.co.sloshyd.popularmovies.adapters.DetailReviewsAdapter;
import uk.co.sloshyd.popularmovies.data.MovieClass;
import uk.co.sloshyd.popularmovies.data.MovieContract;
import uk.co.sloshyd.popularmovies.databinding.ActivityDetailBinding;
import uk.co.sloshyd.popularmovies.data.MovieContract.MovieEntry;



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
    private static final boolean IS_MOVIE_FAVORITE = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //initialize the binding object and set contentView
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);


        Intent intent = getIntent();
        mMovieData = intent.getParcelableExtra(Utils.INTENT_PUTEXTRA_MOVIE_DATA);
        mLoadManager = getLoaderManager();
        //initialize loaders
        mLoadManager.initLoader(TRAILER_LOADER_ID, null, this);
        mLoadManager.initLoader(REVIEW_LOADER_ID, null, this);
        //setup views

        mBinding.tvDetailTitle.setText(mMovieData.getmTitle());
        mBinding.tvDetailOverview.setText(mMovieData.getmOverview());
        mBinding.tvDetailReleaseDate.setText(mMovieData.getmReleaseDate());

        String averageVoteString = Double.toString(mMovieData.getmAverageVote());
        String outOfTenVote = averageVoteString + " / 10 ";
        mBinding.viewDetailVotes.setText(outOfTenVote);


        Utils.loadPosterImage(mBinding.imageViewDetailPoster, this, mMovieData.getmPosterPath());

        //set up adapter for the reviews section
        mBinding.reviewsRecylerView.findViewById(R.id.reviews_recyler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mBinding.reviewsRecylerView.setLayoutManager(layoutManager);
        mBinding.reviewsRecylerView.setHasFixedSize(true);
        mAdapter = new DetailReviewsAdapter(this);
        mBinding.reviewsRecylerView.setAdapter(mAdapter);
        isMoveFavorite();
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
                launchTraierIntent(v, trailerID);
            }
        });
        ImageView shareIcon = (ImageView) v.findViewById(R.id.iv_share_trailer_icon);
        shareIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchTraierIntent(v, trailerID);
            }
        });
        return v;
    }

    public void launchTraierIntent(View v, String id) {
        int viewId = v.getId();

        Uri uri = Uri.parse(YOU_TUBE_URL + id);
        switch (viewId) {

            case R.id.iv_play_trailer_icon:
                Intent launchVideo = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(launchVideo);
                break;
            case R.id.iv_share_trailer_icon:
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, uri.toString());
                startActivity(Intent.createChooser(sharingIntent, "Share using"));
                break;
            default:
                break;
        }
    }

    public void addToFavorites(View v) {

        ImageView imageView = (ImageView) findViewById(R.id.image_view_detail_poster);
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageInByte = baos.toByteArray();

        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieEntry.COLUMN_NAME_MOVIE_TITLE, mMovieData.getmTitle());
        contentValues.put(MovieEntry.COLUMN_NAME_RELEASE_DATE, mMovieData.getmReleaseDate());
        contentValues.put(MovieEntry.COLUMN_NAME_MOVIE_RATING, mMovieData.getmAverageVote());
        contentValues.put(MovieEntry.COLUMN_NAME_MOVIE_DESCRIPTION, mMovieData.getmOverview());
        contentValues.put(MovieEntry.COLUMN_NAME_MOVIE_POSTER, imageInByte);
        contentValues.put(MovieEntry.COLUMN_NAME_MOVIE_ID, mMovieData.getId());

        Uri uri = getContentResolver().insert(MovieContract.CONTENT_URI, contentValues);
        if (uri == null) {
            Toast.makeText(this, R.string.toast_message_unsuccessfully_add_move, Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Error loading movie into database");
        } else {
            Toast.makeText(this, R.string.toast_message_successfully_added_move, Toast.LENGTH_SHORT).show();
            mBinding.button.setVisibility(View.INVISIBLE);
        }

    }

    public void isMoveFavorite() {
        //this is just done as an async task as this is a one time operation
        CheckMovieFavoriteTask checkMovieFavoriteTask = new CheckMovieFavoriteTask();
        checkMovieFavoriteTask.execute(mMovieData);


    }


    public class CheckMovieFavoriteTask extends AsyncTask<MovieClass, String, Boolean> {

        @Override
        protected void onPostExecute(Boolean inFavorites) {
            if(inFavorites){
                mBinding.button.setVisibility(View.INVISIBLE);
            } else {
                mBinding.button.setVisibility(View.VISIBLE);
            }

        }



        @Override
        protected Boolean doInBackground(MovieClass... params) {

            String id = params[0].getId();
            Uri newUri = Uri.parse(MovieContract.CONTENT_URI.toString())
                    .buildUpon()
                    .appendPath(id)
                    .build();
            Log.i(TAG, newUri.toString());

            String[]projection = new String[]{MovieContract.MovieEntry.COLUMN_NAME_MOVIE_ID};

            Cursor cursor = getContentResolver().query(newUri, projection, null, null, null);

            if(cursor.getCount() == 1){
                cursor.close();
                return true;
            }else {
                cursor.close();
                return false;
            }


        }

    }
}



