package itsme.com.moviecatalogue;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ProgressBar;

import itsme.com.moviecatalogue.Data.MovieContract;
import itsme.com.moviecatalogue.Service.FetchDataService;

/**
 * Created by its me on 17-Feb-16.
 * The data what we get from the cloud is only for 20 movies
 */
public class GridViewFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    Context mContext;
    public static final String EXTRA_SORT_ORDER = "SORT_EXTRA";
    public static final int GRID_VIEW_LIMIT = 20;

    //Projections for the cursor Loader
    public static final String[] MOVIE_PROJECTION = {
            MovieContract.Movie._ID,
            MovieContract.Movie.COLUMN_MOVIE_ID,
            MovieContract.Movie.COLUMN_TITLE,
            MovieContract.Movie.COLUMN_POSTER_PATH,
            MovieContract.Movie.COLUMN_POSTER,
            MovieContract.Movie.COLUMN_OVERVIEW,
            MovieContract.Movie.COLUMN_RELEASE_DATE,
            MovieContract.Movie.COLUMN_RATING,
            MovieContract.Movie.COLUMN_POPULARITY,
            MovieContract.Movie.COLUMN_GENRE_IDS,
            MovieContract.Movie.COLUMN_IS_FAVOURITE
    };

    //Column nos for the projections
    public static final int PROJ_ID = 0;
    public static final int PROJ_MOVIE_ID = 1;
    public static final int PROJ_TITLE = 2;
    public static final int PROJ_POSTER_PATH = 3;
    public static final int PROJ_POSTER = 4;
    public static final int PROJ_OVERVIEW = 5;
    public static final int PROJ_RELEASE_DATE = 6;
    public static final int PROJ_RATING = 7;
    public static final int PROJ_POPULARITY = 8;
    public static final int PROJ_GENER_IDS = 9;
    public static final int PROJ_IS_FAV = 10;


    public GridViewFragment() {
        this.mContext = getActivity();
    }

    //Overrided methods
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                startActivity(new Intent(mContext, SharedPrefrenceActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //User declared methods
    private void updateGridView(View rootView) {

        ProgressBar pb = (ProgressBar) rootView.findViewById(R.id.pb1);
        pb.setVisibility(View.VISIBLE); //Set visibility TRUE for progressbar

        //Updating the gridView so that it can be used in PostExecute to update the UI
        GridView gridViewMovie = (GridView) rootView.findViewById(R.id.gridview_movie_list);
        gridViewMovie.setVisibility(View.GONE);

        Intent serviceIntent = new Intent(mContext, FetchDataService.class);
        serviceIntent.putExtra(EXTRA_SORT_ORDER, Utility.getPrefferedSorting(mContext));

        //Wrap in a pending intent which only fires once.
        PendingIntent pi = PendingIntent.getBroadcast(mContext, 0, serviceIntent, PendingIntent.FLAG_ONE_SHOT);

        AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

        //Set the AlarmManager to wake up the system.
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5000, pi);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // This is called when a new Loader needs to be created.  This
        // fragment only uses one loader, so we don't care about checking the id.

        // To only show current and future dates, filter the query to return weather only for
        // dates after or including today.

        // Sort order:  Based on the user pref.
        String sorting = Utility.getPrefferedSorting(mContext);
        String sortingOrder;
        if (sorting.equals(getString(R.string.list_pref_popularity))) {
            sortingOrder = MovieContract.Movie.COLUMN_POPULARITY + " ASC";
        } else {
            sortingOrder = MovieContract.Movie.COLUMN_RATING +
                    " ASC " +
                    " LIMIT" +
                    GRID_VIEW_LIMIT;
        }
        Uri uri = MovieContract.Movie.buildMovieDbUri();

        return new CursorLoader(mContext,
                uri,
                MOVIE_PROJECTION,
                null,
                null,
                sortingOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}