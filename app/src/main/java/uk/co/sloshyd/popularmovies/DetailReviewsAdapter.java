package uk.co.sloshyd.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;



/**
 * Created by Darren on 16/08/2017.
 */

public class DetailReviewsAdapter extends RecyclerView.Adapter<DetailReviewsAdapter.ReviewsViewHolder> {

    private ContentValues[] mReviews;//datasource
    public static final String TAG = DetailReviewsAdapter.class.getName();

    public DetailReviewsAdapter(Context context) {


    }

    @Override
    public ReviewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.review_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new DetailReviewsAdapter.ReviewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewsViewHolder holder, int position) {
        String review = mReviews[position].getAsString(Utils.CV_MOVIE_REVIEW_CONTENT_KEY);
        String author = mReviews[position].getAsString(Utils.CV_MOVIE_REVIEW_AUTHOR_KEY);
        holder.sReview.setText(review);
        holder.sReviewAuthor.setText(author);

    }

    public int getItemCount() {
        if (mReviews == null) {
            return 0;
        } else {
            return mReviews.length;

        }

    }

    public void setData(ContentValues[] contentValues) {
        mReviews = contentValues;
        notifyDataSetChanged();
    }


    public class ReviewsViewHolder extends RecyclerView.ViewHolder {

        public final TextView sReview;
        public final TextView sReviewAuthor;

        public ReviewsViewHolder(View itemView) {
            super(itemView);

            sReview = (TextView) itemView.findViewById(R.id.tv_review_item_review);
            sReviewAuthor = (TextView)itemView.findViewById(R.id.tv_review_item_author);

        }
    }
}
