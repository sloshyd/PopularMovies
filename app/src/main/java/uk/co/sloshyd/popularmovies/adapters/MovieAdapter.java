package uk.co.sloshyd.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import uk.co.sloshyd.popularmovies.R;
import uk.co.sloshyd.popularmovies.Utils;
import uk.co.sloshyd.popularmovies.data.MovieClass;

/**
 * Created by Darren on 13/05/2017.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private MovieClass[] mMovies;
    private Context mContext;
    private final MovieAdapterOnClickHandler mClickHandler;

    public MovieAdapter(Context context, MovieAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;

    }

    //use the handler to get the selected list item object so it can be passed to the Activity where intent can be called
    public interface MovieAdapterOnClickHandler {
        void onClick(MovieClass movieDataItem);
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
        String posterReference = mMovies[position].getmPosterPath();
        Utils.loadPosterImage(holder.mPosterImage, mContext, posterReference);

    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView mPosterImage;

        public MovieAdapterViewHolder(View itemView) {
            super(itemView);
            mPosterImage = (ImageView) itemView.findViewById(R.id.image_view_list_poster);
            //set the onClicklistener
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            //get position in adapter
            int position = getAdapterPosition();
            MovieClass selectedMovie = mMovies[position];
            mClickHandler.onClick(selectedMovie);//handler can now pass this to activity


        }
    }

    public int getItemCount() {
        if (mMovies == null) {
            return 0;
        } else {
            return mMovies.length;
        }

    }

    public void setData(MovieClass[] movieClasses) {
        mMovies = movieClasses;
        notifyDataSetChanged();
    }
}
