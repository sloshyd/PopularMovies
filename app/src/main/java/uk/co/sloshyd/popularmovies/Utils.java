package uk.co.sloshyd.popularmovies;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
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
 * <p>
 * NOTE: This will not work without a valid API_KEY pasted into API_KEY constant
 */

public class Utils {

    private static final String API_KEY = "api_key=8fa9c9ea7b5ebdfac69758e769f80f04";
    private static final String BASE_URL = "http://api.themoviedb.org/3/movie/";
    private static final String URL_POPULAR = "popular?";
    private static final String URL_RATED = "top_rated?";
    public static final String POPULAR_MOVIE_URL = BASE_URL + URL_POPULAR + API_KEY;
    public static final String TOP_RATED_MOVIE_URL = BASE_URL + URL_RATED + API_KEY;
    public static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w185/";
    //API destination for Trailers

    public static final String URL_MOVIE_TRAILER = "/videos?";
    public static final String URL_REVIEWS = "/reviews?";
    //JSON Key names Movie Data
    private static final String POSTER_PATH = "poster_path";
    private static final String OVERVIEW = "overview";
    private static final String RELEASE_DATE = "release_date";
    private static final String MOVIE_TITLE = "title";
    private static final String VOTE_AVERAGE = "vote_average";
    public static final String INTENT_PUTEXTRA_MOVIE_DATA = "movie_data";
    public static final String MOVIE_ID = "id";//unique id on TheMovieDB - used for searching comments and trailers

    //Trailer data feed constants
    public static final String TRAILER_ID = "key";//this is the youtube key used to identify video
    public static final String TRAILER_NAME = "name";// name of the trailer
    //Trailer Info ContentValues
    public static final String CV_YOUTUBE_ID_KEY = "youtubeId";
    public static final String CV_TRAILER_TITLE_KEY = "trailerTitle";

    //MovieReviews
    //datafeed
    private static final String REVIEW_AUTHOR_NAME ="author";
    private static final String REVIEW_CONTENT ="content" ;

    //CV
    public static final String CV_MOVIE_REVIEW_AUTHOR_KEY = "author";
    public static final String CV_MOVIE_REVIEW_CONTENT_KEY ="reviewContent";

    public static final String TAG = Utils.class.getName();


    public static String getResponseFromHttpUrl(URL url) throws IOException {

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        try {

            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            if (urlConnection.getResponseCode() != 200) {
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


    public static MovieClass[] parseJsonString(String json) {

        MovieClass[] jsonParsedDataArray = null;

        if (json == null) {
            return jsonParsedDataArray;
        }

        try {
            JSONObject root = new JSONObject(json);
            JSONArray movieObjectsArray = root.getJSONArray("results");
            jsonParsedDataArray = new MovieClass[movieObjectsArray.length()];

            for (int i = 0; i < jsonParsedDataArray.length; i++) {

                //data to be collected
                String posterPath;
                String overView;
                String releaseDate;
                String title;
                double voteAverage;
                String movieId;

                JSONObject movieData = movieObjectsArray.getJSONObject(i);
                posterPath = movieData.getString(POSTER_PATH);
                overView = movieData.getString(OVERVIEW);
                releaseDate = movieData.getString(RELEASE_DATE);
                title = movieData.getString(MOVIE_TITLE);
                voteAverage = movieData.getDouble(VOTE_AVERAGE);
                movieId = movieData.getString(MOVIE_ID);

                Log.i(TAG, posterPath + overView + releaseDate + title + voteAverage + "id " + movieId);

                jsonParsedDataArray[i] =
                        new MovieClass(posterPath, overView, releaseDate, title, voteAverage, movieId);
            }


        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON");
        }

        return jsonParsedDataArray;
    }

    public static void loadPosterImage(ImageView view, Context context, String posterReference) {
        String posterURL = Utils.IMAGE_BASE_URL + posterReference;
        Picasso.with(context).load(posterURL).into(view);
    }

    public static ContentValues[] parseMovieTrailers(String json) {
        ContentValues[] values = null;

        if (json == null) {
            return values;
        }
        try {
            JSONObject root = new JSONObject(json);
            JSONArray movieObjectsArray = root.getJSONArray("results");
            values = new ContentValues[movieObjectsArray.length()];

            for (int i = 0; i < movieObjectsArray.length(); i++) {
                String trailerId;
                String nameOfTrailer;

                JSONObject trailerInfo = movieObjectsArray.getJSONObject(i);

                trailerId = trailerInfo.getString(TRAILER_ID);
                nameOfTrailer = trailerInfo.getString(TRAILER_NAME);

                ContentValues contentValues = new ContentValues();
                contentValues.put(CV_YOUTUBE_ID_KEY, trailerId);
                contentValues.put(CV_TRAILER_TITLE_KEY, nameOfTrailer);
                values[i] = contentValues;
            }


        } catch (JSONException e) {
            Log.e(TAG, "Error Parsing Trailer JSON " + e);
        }


        return values;
    }

    //get the string of the URL for the movie trailers
    public static String getTrailersURL(String trailerId){


        return new String (BASE_URL + trailerId + URL_MOVIE_TRAILER + API_KEY);
    }

    public static ContentValues[] parseMovieReviews(String json) {
        ContentValues[] values = null;

        if (json == null) {
            return values;
        }
        try {
            JSONObject root = new JSONObject(json);
            JSONArray movieObjectsArray = root.getJSONArray("results");
            values = new ContentValues[movieObjectsArray.length()];

            for (int i = 0; i < movieObjectsArray.length(); i++) {
                String author;
                String content;

                JSONObject trailerInfo = movieObjectsArray.getJSONObject(i);

                author = trailerInfo.getString(REVIEW_AUTHOR_NAME);
                content = trailerInfo.getString(REVIEW_CONTENT);

                ContentValues contentValues = new ContentValues();
                contentValues.put(CV_MOVIE_REVIEW_AUTHOR_KEY, author);
                contentValues.put(CV_MOVIE_REVIEW_CONTENT_KEY, content);
                Log.i(TAG, author + " " + content);
                values[i] = contentValues;
            }


        } catch (JSONException e) {
            Log.e(TAG, "Error Parsing Trailer JSON " + e);
        }


        return values;


    }

    public static String getMovieReviewsURL(String movieId){


        return new String (BASE_URL + movieId + URL_REVIEWS + API_KEY);
    }

    public static Bitmap getPoster(byte[] poster){
        if(poster == null){
            return null;
        }
        Bitmap bitmap = BitmapFactory.decodeByteArray(poster, 0, poster.length);
        return bitmap;
    }

    public static byte[] getImageDataFromView(ImageView imageView){

        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageInByte = baos.toByteArray();

        try {
                baos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageInByte;
    }


}
