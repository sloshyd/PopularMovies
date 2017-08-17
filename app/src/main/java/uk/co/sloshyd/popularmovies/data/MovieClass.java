package uk.co.sloshyd.popularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Darren on 15/05/2017.
 * MovieClass acts as dataStorage object, implements Parceable to make data transfer faster
 */

public class MovieClass implements Parcelable {

    private String mPosterPath;
    private String mOverview;
    private String mReleaseDate;
    private String mTitle;
    private double mAverageVote;
    private String mID;

    public MovieClass (String posterPath, String overView,
                       String releaseDate, String title, double averageVote, String id){
        mPosterPath = posterPath;
        mOverview = overView;
        mReleaseDate = releaseDate;
        mTitle = title;
        mAverageVote = averageVote;
        mID = id;

    }

    private MovieClass (Parcel in){
        mPosterPath = in.readString();
        mOverview = in.readString();
        mReleaseDate = in.readString();
        mTitle = in.readString();
        mAverageVote = in.readDouble();
        mID = in.readString();
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

    public String getId() {
        return mID;
    }

    @Override
    public String toString() {
        return "MovieClass{" +
                "mPosterPath='" + mPosterPath + '\'' +
                ", mOverview='" + mOverview + '\'' +
                ", mReleaseDate='" + mReleaseDate + '\'' +
                ", mTitle='" + mTitle + '\'' +
                ", mAverageVote=" + mAverageVote +
                ", mID=" + mID +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeString(mPosterPath);
        out.writeString(mOverview);
        out.writeString(mReleaseDate);
        out.writeString(mTitle);
        out.writeDouble(mAverageVote);
        out.writeString(mID);
    }

    //must have CREATOR to create object from parcel data
    public static final Parcelable.Creator<MovieClass> CREATOR
            = new Parcelable.Creator<MovieClass>() {
        public MovieClass createFromParcel(Parcel in) {
            return new MovieClass(in);
        }

        public MovieClass[] newArray(int size) {
            return new MovieClass[size];
        }
    };



}
