package uk.co.sloshyd.popularmovies.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import uk.co.sloshyd.popularmovies.R;
import uk.co.sloshyd.popularmovies.data.MovieContract;

/**
 * Created by Darren on 19/08/2017.
 */

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavoriteAdapterHolder> {

    Cursor mCursor;

    public FavoritesAdapter() {

    }


    @Override
    public FavoriteAdapterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.favorite_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new FavoriteAdapterHolder(view);
    }

    @Override
    public void onBindViewHolder(FavoriteAdapterHolder holder, int position) {
        mCursor.moveToPosition(position);
        String title = mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_NAME_MOVIE_TITLE));
        String releaseDate = mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_NAME_RELEASE_DATE));
        byte[] imageData = mCursor.getBlob(mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_NAME_MOVIE_POSTER));
        String description = mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_NAME_MOVIE_DESCRIPTION));
        String rating = mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_NAME_MOVIE_RATING));
        //bind data to the holder
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
        holder.imageView.setImageBitmap(bitmap);

        holder.tv_description.setText(description);
        holder.tv_title.setText(title);

        holder.tv_release_date.setText(releaseDate);

        holder.tv_rating.setText(rating + "/10");
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        } else {
            return mCursor.getCount();
        }
    }

    public static class FavoriteAdapterHolder extends RecyclerView.ViewHolder{
        final TextView tv_description;
        final TextView tv_title;
        final ImageView imageView;
        final TextView tv_release_date;
        final TextView tv_rating;

        public FavoriteAdapterHolder(View itemView) {
            super(itemView);
            tv_description = (TextView)itemView.findViewById(R.id.tv_favorite_description);
            tv_title= (TextView) itemView.findViewById(R.id.tv_favorite_title);
            imageView = (ImageView) itemView.findViewById(R.id.iv_favorite_poster);
            tv_release_date = (TextView) itemView.findViewById(R.id.tv_favorite_release_date);
            tv_rating = (TextView)itemView.findViewById(R.id.tv_favorite_rating);
        }

    }

    public void swapCursor(Cursor cursor){
        mCursor = cursor;
    }
}
