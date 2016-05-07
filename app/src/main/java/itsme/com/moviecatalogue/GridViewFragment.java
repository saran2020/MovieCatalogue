package itsme.com.moviecatalogue;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;

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

/**
 * Created by its me on 17-Feb-16.
 * The data what we get from the cloud is only for 20 movies
 */
public class GridViewFragment extends Fragment {

    private static String SORT_BY_POPULARITY;
    //Constants & global variables
    private final String[] TITLE = new String[20];
    private final String[] RELEASE_DATE = new String[20];
    private final String[] OVERVIEW = new String[20];
    private final String[] IMAGE = new String[20];
    private final Float[] RATING = new Float[20];
    //Made it global so that this can be used to call adapter in post execute
    GridView gridViewMovie;
    ProgressBar pb;

    //Overrided methods
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SORT_BY_POPULARITY = getResources().getStringArray(R.array.listPrefEntryValue)[0];
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container
            , Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.grid_view_fragment, container, false);
        updateGridView(rootView);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateGridView(getView());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_grid_view_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_setting:
                //This sets the currently selected option to the summery
                startActivity(new Intent(getActivity(), SharedPrefrenceActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //User declared methods
    private void updateGridView(View rootView) {

        pb = (ProgressBar) rootView.findViewById(R.id.pb1);
        pb.setVisibility(View.VISIBLE); //Set visibility TRUE for progressbar

        //Getting data from shared preference
        String sortBy = getSortingData();

        //Updating the gridView so that it can be used in PostExecute to update the UI
        gridViewMovie = (GridView) rootView.findViewById(R.id.gridview_movie_list);
        gridViewMovie.setVisibility(View.GONE);

        FetchMovieDataTask fetchData = new FetchMovieDataTask();
        fetchData.execute(sortBy);

    }

    public String getSortingData() {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(getActivity().getBaseContext());

        return sp.getString(getString(R.string.keyListPref), SORT_BY_POPULARITY);
    }

    //User declared classes
    private class FetchMovieDataTask extends AsyncTask<String, Void, Void>{
        //Constants and Global Variables
        private final String LOG_TAG = GridViewFragment.class.getSimpleName();

        //Overrided methods
        @Override
        protected Void doInBackground(String... params) {

            HttpURLConnection URLConnection = null;
            BufferedReader reader = null;
            String jsonString = null;

            //Used for building string
            final String POPULARITY="popularity.desc";
            final String RATING="vote_average.desc";
            final String COUNT="100";

            try {
                final String BASE_URL = "https://api.themoviedb.org/3/discover/movie?";
                final String SORT = "sort_by";
                final String VOTE_COUNT = "vote_count.gte";
                final String APP_ID = "api_key";

                Uri uri;
                if(params[0].matches(SORT_BY_POPULARITY)) {
                    uri = Uri.parse(BASE_URL).buildUpon().appendQueryParameter(SORT, POPULARITY)
                            .appendQueryParameter(APP_ID, BuildConfig.THE_MOVIE_DB_API_KEY)
                            .build();
                }else {
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

        @Override
        protected void onPostExecute(Void aVoid) {
            //This method is used to update the UI after we get data from cloud
            super.onPostExecute(aVoid);

            gridViewMovie.setAdapter(new GridViewAdapter(getActivity().getApplicationContext(), IMAGE));
            gridViewMovie.setVisibility(View.VISIBLE);

            //removes the progress bar since execution is done
            pb.setVisibility(View.GONE);

            //On click listener for the grid view to start an activity.
            gridViewMovie.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    Intent detailViewIntent = new Intent(getActivity(), DetailActivity.class);

                    //Filling the bundle with the data to be passed to the detail view
                    Bundle bundle = new Bundle();
                    bundle.putString("Title", TITLE[position]);
                    bundle.putString("Image", IMAGE[position]);
                    bundle.putString("Release date", RELEASE_DATE[position]);
                    bundle.putString("Overview", OVERVIEW[position]);
                    bundle.putFloat("Rating", RATING[position]);

                    detailViewIntent.putExtras(bundle);
                    startActivity(detailViewIntent);
                }
            });
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
}
