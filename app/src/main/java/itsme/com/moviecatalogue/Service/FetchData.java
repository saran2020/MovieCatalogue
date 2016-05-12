package itsme.com.moviecatalogue.Service;

import android.app.IntentService;
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
import java.util.Date;
import java.util.Locale;

import itsme.com.moviecatalogue.BuildConfig;

/**
 * Created by its me on 13-May-16.
 */
public class FetchData extends IntentService {

    //User declared classes
    //Constants and Global Variables
    private final String LOG_TAG = FetchData.class.getSimpleName();

    @Override
    protected void onHandleIntent(Intent intent) {
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
            if (params[0].matches(SORT_BY_POPULARITY)) {
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
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;

            while ((line = reader.readLine()) != null)
                buffer.append(line + "/n");

            if (buffer.length() == 0)
                return null;

            jsonString = buffer.toString();

            Log.v(LOG_TAG, "JSONSTring" + jsonString);
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
        }

        return null;
    }

    //User defined methods
    private void formatJsonString(String jsonString)
            throws JSONException {

        //Required for formatting
        final String JSON_RESULT = "results";

        //Required Fields
        final String JSON_TITLE = "original_title";
        final String JSON_RELEASE_DATE = "release_date";
        final String JSON_OVERVIEW = "overview";
        final String JSON_IMAGE = "poster_path";
        final String JSON_RATING = "vote_average";

        //Getting a JSON object for the complete String
        JSONObject movieDetailJson = new JSONObject(jsonString);
        //Getting a array of 20 movies from the Json
        JSONArray movieArray = movieDetailJson.getJSONArray(JSON_RESULT);

        for (int pos = 0; pos < movieArray.length(); pos++) {
            String title;
            String releaseDate;
            String overView;
            String poster;
            String rating;

            //Get single movie from a array of 20 movies
            JSONObject movie = movieArray.getJSONObject(pos);

            //Getting data for a single movie
            title = movie.getString(JSON_TITLE);
            releaseDate = movie.getString(JSON_RELEASE_DATE);
            overView = movie.getString(JSON_OVERVIEW);
            poster = movie.getString(JSON_IMAGE);
            rating = movie.getString(JSON_RATING);

            updateToArray(pos, title, releaseDate, overView, poster, rating);
        }

        //Exiting the method after populating the array
        return;
    }

    private void updateToArray(Integer pos, String title, String releaseDate
            , String overView, String poster, String rating) {

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

        TITLE[pos] = title;
        RELEASE_DATE[pos] = format.format(date);
        OVERVIEW[pos] = overView;
        IMAGE[pos] = poster;
        RATING[pos] = Float.valueOf(rating);
    }
}
