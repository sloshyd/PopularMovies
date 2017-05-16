package uk.co.sloshyd.popularmovies.data;

/**
 * Created by Darren on 15/05/2017.
 * MovieClass acts as dataStorage object
 */

public class MovieClass {

    private String mPosterPath;
    private String mOverview;
    private String mReleaseDate;
    private String mTitle;
    private double mAverageVote;

    public MovieClass (String posterPath, String overView,
                       String releaseDate, String title, double averageVote){
        mPosterPath = posterPath;
        mOverview = overView;
        mReleaseDate = releaseDate;
        mTitle = title;
        mAverageVote = averageVote;

    }

    public String getmPosterPath() {
        return mPosterPath;
    }

    public String getmOverview() {
        return mOverview;
    }

    public String getmReleaseDate() {
        return mReleaseDate;
    }

    public String getmTitle() {
        return mTitle;
    }

    public double getmAverageVote() {
        return mAverageVote;
    }

    @Override
    public String toString() {
        return "MovieClass{" +
                "mPosterPath='" + mPosterPath + '\'' +
                ", mOverview='" + mOverview + '\'' +
                ", mReleaseDate='" + mReleaseDate + '\'' +
                ", mTitle='" + mTitle + '\'' +
                ", mAverageVote=" + mAverageVote +
                '}';
    }
}
