package uk.co.sloshyd.popularmovies;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import uk.co.sloshyd.popularmovies.data.MovieClass;

/**
 * Created by Darren on 13/05/2017.
 * IMPORTANT: Inorder for the code to work it must have a valid API_KEY from TheMoviedb.org this
 * should be pasted into the API_KEY Constant;
 */

public class Utils {

    private static final String API_KEY = "api_key=8fa9c9ea7b5ebdfac69758e769f80f04";
    private static final String BASE_URL_POPULAR = "http://api.themoviedb.org/3/movie/popular?";
    private static final String BASE_URL_RATED= "http://api.themoviedb.org/3/movie/top_rated?";
    public static final String POPULAR_MOVIE_URL = BASE_URL_POPULAR + API_KEY;
    public static final String TOP_RATED_MOVIE_URL = BASE_URL_RATED + API_KEY;
    public static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w185/";
    //JSON Key names
    private static final String POSTER_PATH = "poster_path";
    private static final String OVERVIEW = "overview";
    private static final String RELEASE_DATE ="release_date";
    private static final String MOVIE_TITLE = "title";
    private static final String VOTE_AVERAGE ="vote_average";
    public static final String INTENT_PUTEXTRA_MOVIE_DATA = "movie_data";

    public static final String TAG = Utils.class.getName();


    public static String getResponseFromHttpUrl(URL url) throws IOException {

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        try {

            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            if(urlConnection.getResponseCode() != 200){
                Log.e(TAG, "Error response from server " + urlConnection.getResponseCode());
                return null;
            } else {
                boolean hasInput = scanner.hasNext();
                if (hasInput) {
                    return scanner.next();
                } else {
                    return null;
                }
            }

        } finally {
            urlConnection.disconnect();
        }
    }


    public static MovieClass[] parseJsonString (String json){

        MovieClass[] jsonParsedDataArray = null;

        if(json == null){
            return jsonParsedDataArray;
        }

        try {
            JSONObject root = new JSONObject(json);
            JSONArray movieObjectsArray = root.getJSONArray("results");
            jsonParsedDataArray = new MovieClass[movieObjectsArray.length()];

            for(int i = 0; i < jsonParsedDataArray.length;i++){

                //data to be collected
                String posterPath;
                String overView;
                String releaseDate;
                String title;
                double voteAverage;

                JSONObject movieData = movieObjectsArray.getJSONObject(i);
                posterPath = movieData.getString(POSTER_PATH);
                overView = movieData.getString(OVERVIEW);
                releaseDate = movieData.getString(RELEASE_DATE);
                title = movieData.getString(MOVIE_TITLE);
                voteAverage = movieData.getDouble(VOTE_AVERAGE);

                Log.i(TAG, posterPath + overView + releaseDate + title+ voteAverage);

                jsonParsedDataArray[i] = new MovieClass(posterPath, overView, releaseDate, title, voteAverage);
            }



        } catch (JSONException e){
            Log.e(TAG, "Error parsing JSON");
        }

        return jsonParsedDataArray;
    }

    public static void loadPosterImage(ImageView view, Context context, String posterReference ){
        String posterURL = Utils.IMAGE_BASE_URL + posterReference;
        Picasso.with(context).load(posterURL).into(view);
    }
}
