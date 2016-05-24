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
import java.net.URLDecoder;

import itsme.com.moviecatalogue.Data.MovieContract;

/**
 * Created by its me on 23-May-16.
 * This service get the trailer data from the movieDB using Api call
 * This service doesn't get the data from the server while other details are populated
 * but it gets it only when some movie has been clicked and stores it into the db for
 * future use. This class only gets the data fro the 2 trailers and not more than that.
 * EX of trailer data: nyc6RJEEe0U,zSWdZVtXT7E
 * Two trailer keys are separated by ',' sign.
 */
public class GetMovieDetailsService extends IntentService {

    private static final String LOG_TAG = GetMovieDetailsService.class.getSimpleName();
    private static final String WORKER_THREAD_NAME = "GetMovieDeatils";
    public static final String EXTRA_MOVIE_ID = "movieid";

    public GetMovieDetailsService() {
        super(WORKER_THREAD_NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String movieId = intent.getStringExtra(EXTRA_MOVIE_ID);
        Log.v(LOG_TAG, "MovieID: " + movieId);

        String trailers = getTrailers(movieId);

        Uri uri = MovieContract.Movie.buildMovieUri(Long.parseLong(movieId));

        ContentValues cv = new ContentValues();
        cv.put(MovieContract.Movie.COLUMN_TRAILERS, trailers);
        int rowsUpdated = 0;

        if (cv.containsKey(MovieContract.Movie.COLUMN_TRAILERS)) {
            rowsUpdated = this.getContentResolver().update(uri, cv, null, null);
        }

        Log.v(LOG_TAG, "RowsUpdated = " + rowsUpdated);

    }

    private String getTrailers(String movieId) {
        HttpURLConnection URLConnection = null;
        BufferedReader reader = null;
        String jsonString = null;

        try {
            final String BASE_URL = "http://api.themoviedb.org/3/movie";
            final String APP_ID = "api_key";
            final String VIDEOS_PATH = "videos";

            Uri uri = Uri.parse(BASE_URL).buildUpon()
                    .appendPath(movieId)
                    .appendPath(VIDEOS_PATH)
                    .appendQueryParameter(APP_ID,
                            itsme.com.moviecatalogue.BuildConfig.THE_MOVIE_DB_API_KEY)
                    .build();

            //This will remove the %3F from the url http://api.themoviedb.org/3/movie/movie_id/videos%3F?api_key=###
            String decodeUri = URLDecoder.decode(uri.toString(), "UTF-8");
            Log.v(LOG_TAG, "Trailer uri: " + decodeUri);
            URL urlObject = new URL(decodeUri);
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

            Log.v(LOG_TAG, "JSONString" + jsonString);
        } catch (IOException e) {
            Log.e(LOG_TAG, "ERROR" + e.toString());
        } finally {
            if (URLConnection != null) {
                URLConnection.disconnect();
            }

            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "ERROR closing stream" + e.toString());
                }
            }
        }

        try {
            return formatTrailerJson(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.v(LOG_TAG, "Json exception encountered in fetching trailer");
        }

        return null;
    }

    private String formatTrailerJson(String jsonString)
            throws JSONException {

        String returnValue = "";
        int trailerCounter = 0;
        //Required for formatting
        final String JSON_RESULT = "results";

        //Required Fields
        final String YOUTUBE_LINK = "key";
        final String TRAILER_TYPE = "type";
        final String TRAILER = "Trailer";

        //Getting a JSON object for the complete String
        JSONObject movieTrailerJson = new JSONObject(jsonString);
        //Getting a array of trailers of the movie
        JSONArray trailerArray = movieTrailerJson.getJSONArray(JSON_RESULT);

        for (int pos = 0; pos < trailerArray.length(); pos++) {
            //Get all the keys of the trailers and make it to a single string separated by ','.
            JSONObject trailer = trailerArray.getJSONObject(pos);

            if (trailerCounter < 2 && (TRAILER.equals(trailer.getString(TRAILER_TYPE)))) {
                returnValue += trailer.getString(YOUTUBE_LINK);
                trailerCounter++;
                //After getting the key for one trailer separate it with a ','.
                if (trailerCounter == 1)
                    returnValue += ",";
            }
        }
        Log.v(LOG_TAG, "Trailer String: " + returnValue);
        return returnValue;
    }
}
