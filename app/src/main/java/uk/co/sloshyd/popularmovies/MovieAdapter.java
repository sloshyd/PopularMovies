package uk.co.sloshyd.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import uk.co.sloshyd.popularmovies.data.MovieClass;

/**
 * Created by Darren on 13/05/2017.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private MovieClass[] mMovies;


    public MovieAdapter (){

    }

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.poster_list_image;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new MovieAdapterViewHolder(view);

    }

    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {
        String data = mMovies[position].getmOverview();
        holder.mTextView.setText(data);
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder{
        public final TextView mTextView;

        public MovieAdapterViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.image_view_list_poster);
        }
    }

    public int getItemCount() {
        if(mMovies == null){
            return 0;
        } else{
            return mMovies.length;
        }

    }

    public void setData(MovieClass[] movieClasses){
        mMovies = movieClasses;
        notifyDataSetChanged();
    }
}
