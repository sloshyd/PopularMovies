package uk.co.sloshyd.popularmovies;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import uk.co.sloshyd.popularmovies.data.MovieClass;

public class DetailActivity extends AppCompatActivity {

    private MovieClass mMovieData;
    private TextView mOverview;
    private TextView mTitle;
    private ImageView mMoviePoster;
    private TextView mReleaseDate;
    private TextView mAverageVotes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        mMovieData = intent.getParcelableExtra(Utils.INTENT_PUTEXTRA_MOVIE_DATA);

        //setup views
        mTitle = (TextView) findViewById(R.id.tv_detail_title);
        mTitle.setText(mMovieData.getmTitle());

        mOverview = (TextView) findViewById(R.id.tv_detail_overview);
        mOverview.setText(mMovieData.getmOverview());

        mReleaseDate = (TextView) findViewById(R.id.tv_detail_release_date);
        mReleaseDate.setText(mMovieData.getmReleaseDate());

        mAverageVotes = (TextView) findViewById(R.id.view_detail_votes);
        String averageVoteString = Double.toString( mMovieData.getmAverageVote());
        String outOfTenVote = averageVoteString + " / 10 ";
        mAverageVotes.setText(outOfTenVote);

        mMoviePoster = (ImageView) findViewById(R.id.image_view_detail_poster);
        Utils.loadPosterImage(mMoviePoster, this, mMovieData.getmPosterPath());





    }
}
