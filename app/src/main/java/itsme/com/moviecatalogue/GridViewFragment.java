package itsme.com.moviecatalogue;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ProgressBar;

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
    }

    public String getSortingData() {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(getActivity().getBaseContext());

        return sp.getString(getString(R.string.keyListPref), SORT_BY_POPULARITY);
    }
}
