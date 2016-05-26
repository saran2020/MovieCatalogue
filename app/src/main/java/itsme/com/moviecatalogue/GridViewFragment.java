package itsme.com.moviecatalogue;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;

import itsme.com.moviecatalogue.Adapter.GridViewAdapter;
import itsme.com.moviecatalogue.Data.MovieContract;
import itsme.com.moviecatalogue.Service.FetchDataService;
import itsme.com.moviecatalogue.Service.GetMovieDetailsService;

/**
 * Created by its me on 17-Feb-16.
 * The data what we get from the cloud is only for 20 movies
 */
public class GridViewFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    Context mContext;
    GridViewAdapter mAdapter;
    public static final String EXTRA_SORT_ORDER = "SORT_EXTRA";
    public static final int GRID_VIEW_LIMIT = 20;

    private static final String LOG_TAG = GridViewFragment.class.getSimpleName();
    private static final int MOVIE_LOADER = 0;

    //Projections for the cursor Loader
    public static final String[] MOVIE_PROJECTION = {
            MovieContract.Movie._ID,
            MovieContract.Movie.COLUMN_MOVIE_ID,
            MovieContract.Movie.COLUMN_POSTER,
            MovieContract.Movie.COLUMN_RATING,
            MovieContract.Movie.COLUMN_POPULARITY,
            MovieContract.Movie.COLUMN_GENRE_IDS,
            MovieContract.Movie.COLUMN_IS_FAVOURITE
    };

    //TODO:Modify the projection to get only required data.
    //Column nos for the projections
    public static final int PROJ_ID = 0;
    public static final int PROJ_MOVIE_ID = 1;
    public static final int PROJ_POSTER = 2;
    public static final int PROJ_RATING = 3;
    public static final int PROJ_POPULARITY = 4;
    public static final int PROJ_GENER_IDS = 5;
    public static final int PROJ_IS_FAV = 6;

    public interface CallBack {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        void onItemSelected(Uri uri);
    }

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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    //User declared methods
    private void updateGridView(View rootView) {

        mContext = getActivity();
        mAdapter = new GridViewAdapter(mContext, null, 0);

        ProgressBar pb = (ProgressBar) rootView.findViewById(R.id.pb1);
        pb.setVisibility(View.VISIBLE); //Set visibility TRUE for progressbar

        //Updating the gridView so that it can be used in PostExecute to update the UI
        GridView gridViewMovie = (GridView) rootView.findViewById(R.id.gridview_movie_list);
        gridViewMovie.setAdapter(mAdapter);
        gridViewMovie.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    long movieId = cursor.getLong(PROJ_MOVIE_ID);
                    Intent serviceIntent = new Intent(mContext, GetMovieDetailsService.class);
                    serviceIntent.putExtra(GetMovieDetailsService.EXTRA_MOVIE_ID, Long.toString(movieId));
                    mContext.startService(serviceIntent);

                    ((CallBack) getActivity())
                            .onItemSelected(MovieContract.Movie.buildMovieUri(movieId));
                }
            }
        });


        Intent serviceIntent = new Intent(mContext, FetchDataService.class);
        serviceIntent.putExtra(EXTRA_SORT_ORDER, Utility.getPrefferedSorting(mContext));

        mContext.startService(serviceIntent);
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
            sortingOrder = MovieContract.Movie.COLUMN_RATING + " ASC";
        }
        Uri uri = MovieContract.Movie.buildMovieDbUri();

        return new CursorLoader(getActivity(),
                uri,
                MOVIE_PROJECTION,
                null,
                null,
                sortingOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}