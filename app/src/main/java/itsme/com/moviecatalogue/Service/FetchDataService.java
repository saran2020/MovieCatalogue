package itsme.com.moviecatalogue.Service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

import itsme.com.moviecatalogue.BuildConfig;
import itsme.com.moviecatalogue.Data.MovieContract;
import itsme.com.moviecatalogue.GridViewFragment;
import itsme.com.moviecatalogue.R;
import itsme.com.moviecatalogue.Utility;

/**
 * Created by its me on 13-May-16.
 */
public class FetchDataService extends IntentService {

    //User declared classes
    //Constants and Global Variables
    private static final String LOG_TAG = FetchDataService.class.getSimpleName();
    private static final String WORKER_THREAD_NAME = "FetchData";
    public boolean isServiceActive = false;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public FetchDataService() {
        super(WORKER_THREAD_NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.v(LOG_TAG, "Service started sucessfully");
        HttpURLConnection URLConnection = null;
        BufferedReader reader = null;
        String jsonString = null;

        //Used for building string
        final String POPULARITY = "popularity.desc";
        final String RATING = "vote_average.desc";
        final String COUNT = "100";

        try {
            final String BASE_URL = "https://api.themoviedb.org/3/discover/movie?";
            final String SORT = "sort_by";
            final String VOTE_COUNT = "vote_count.gte";
            final String APP_ID = "api_key";

            Uri uri;

            //If there is no sorting order we don't know which data to get hence return.
            if (!(intent.hasExtra(GridViewFragment.EXTRA_SORT_ORDER)))
                return;
            String sortingOrder = intent.getStringExtra(GridViewFragment.EXTRA_SORT_ORDER);
            if (sortingOrder.matches(getString(R.string.list_pref_popularity))) {
                uri = Uri.parse(BASE_URL).buildUpon().appendQueryParameter(SORT, POPULARITY)
                        .appendQueryParameter(APP_ID, BuildConfig.THE_MOVIE_DB_API_KEY)
                        .build();
            } else {
                uri = Uri.parse(BASE_URL).buildUpon().appendQueryParameter(SORT, RATING)
                        .appendQueryParameter(VOTE_COUNT, COUNT)
                        .appendQueryParameter(APP_ID, BuildConfig.THE_MOVIE_DB_API_KEY)
                        .build();
            }

            String url = uri.toString();
            Log.v(LOG_TAG, "URL: " + url);

            URL urlObject = new URL(url);

            URLConnection = (HttpURLConnection) urlObject.openConnection();
            URLConnection.setRequestMethod("GET");
            URLConnection.setConnectTimeout(5000);
            URLConnection.setReadTimeout(5000);
            URLConnection.connect();

            InputStream inputStream = URLConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;

            while ((line = reader.readLine()) != null)
                buffer.append(line + "/n");

            if (buffer.length() == 0)
                return;

            jsonString = buffer.toString();

            Log.v(LOG_TAG, "JSONString" + jsonString);
        } catch (IOException e) {
            Log.e("FetchMovieDataTask", "ERROR" + e.toString());
        } finally {
            if (URLConnection != null) {
                URLConnection.disconnect();
            }

            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e("FetchMovieDataTask", "ERROR closing stream" + e.toString());
                }
            }
        }

        try {
            formatJsonString(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return;
    }

    //User defined methods
    private void formatJsonString(String jsonString)
            throws JSONException, IOException {

        //Required for formatting
        final String JSON_RESULT = "results";

        //Required Fields
        final String JSON_TITLE = "original_title";
        final String JSON_RELEASE_DATE = "release_date";
        final String JSON_OVERVIEW = "overview";
        final String JSON_IMAGE = "poster_path";
        final String JSON_RATING = "vote_average";
        final String JSON_MOVIE_ID = "id";
        final String JSON_GENRE_IDS = "genre_ids";
        final String JSON_POPULARITY = "popularity";

        //Getting a JSON object for the complete String
        JSONObject movieDetailJson = new JSONObject(jsonString);
        //Getting a array of 20 movies from the Json
        JSONArray movieArray = movieDetailJson.getJSONArray(JSON_RESULT);

        Vector<ContentValues> cvVector = new Vector<>(movieArray.length());

        for (int pos = 0; pos < movieArray.length(); pos++) {
            String title;
            String releaseDate;
            String overView;
            String poster_path;
            String rating;
            String movie_id;
            String genres = null;
            String popularity;

            //Get single movie from a array of 20 movies
            JSONObject movie = movieArray.getJSONObject(pos);

            //Getting data for a single movie
            title = movie.getString(JSON_TITLE);
            releaseDate = movie.getString(JSON_RELEASE_DATE);
            overView = movie.getString(JSON_OVERVIEW);
            poster_path = movie.getString(JSON_IMAGE);
            rating = movie.getString(JSON_RATING);
            movie_id = movie.getString(JSON_MOVIE_ID);
            popularity = movie.getString(JSON_POPULARITY);

            //Getting the Json array for the move genre
            // and making a string of the array to update into the Db
            JSONArray genre = movie.getJSONArray(JSON_GENRE_IDS);
            if (genre != null) {
                int genre_ids[] = new int[genre.length()];

                for (int i = 0; i < genre.length(); i++) {
                    genre_ids[i] = genre.getInt(i);
                }
                genres = Arrays.toString(genre_ids);
            }

            //Getting date from Year-Month-Date format
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            Date date = null;
            try {
                date = format.parse(releaseDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            //Changing date to Date-Month-Year format
            format = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);

            //Declaring a content value a building an array of content values.
            ContentValues values = new ContentValues();

            values.put(MovieContract.Movie.COLUMN_TITLE, title);
            values.put(MovieContract.Movie.COLUMN_RELEASE_DATE, format.format(date));
            values.put(MovieContract.Movie.COLUMN_OVERVIEW, overView);
            values.put(MovieContract.Movie.COLUMN_POSTER_PATH, poster_path);
            values.put(MovieContract.Movie.COLUMN_RATING, Float.valueOf(rating));
            values.put(MovieContract.Movie.COLUMN_MOVIE_ID, movie_id);
            values.put(MovieContract.Movie.COLUMN_POPULARITY, popularity);
            values.put(MovieContract.Movie.COLUMN_POSTER, Utility.getByteFromImage(poster_path));
            values.put(MovieContract.Movie.COLUMN_IS_FAVOURITE,
                    MovieContract.Movie.IS_FAVOURITE_FALSE);    //We have to set favourite to false when getting the data from cloud.

            Log.v(LOG_TAG, "Movie Title: " + title +
                    "Release Date: " + format.format(date) +
                    "Overview: " + overView +
                    "Posterpath: " + poster_path +
                    "Rating: " + Float.valueOf(rating) +
                    "MovieID: " + movie_id +
                    "Popularity: " + popularity +
                    "Is_fav: " + MovieContract.Movie.IS_FAVOURITE_FALSE); //Didn't log the poster

            if (genres != null)
                values.put(MovieContract.Movie.COLUMN_GENRE_IDS, genres);

            cvVector.add(values);
        }

        int inserted = 0;
        if (cvVector.size() > 0) {
            ContentValues[] contentValues = new ContentValues[cvVector.size()];
            cvVector.toArray(contentValues);
            inserted = this.getContentResolver().
                    bulkInsert(MovieContract.Movie.CONTENT_URI, contentValues);
        }

        Log.v(LOG_TAG, "No of rows inserted: " + inserted);

        return;
    }
}