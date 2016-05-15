package itsme.com.moviecatalogue;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

    Context mContext;

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
                startActivity(new Intent(getActivity(), SharedPrefrenceActivity.class));
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

        //TODO: Add a call to the Service for fetching data.
    }
}