package itsme.com.moviecatalogue;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

/**
 * Created by its me on 17-Feb-16.
 */
public class GridViewFragment extends Fragment {

    //Constants & global variables
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
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    //User declared methods
    private void updateGridView(View rootView) {

        rootView.findViewById(R.id.pb1).setVisibility(View.VISIBLE);    //Set visibility TRUE for progressbar

        GridView gridViewMovie = (GridView) rootView.findViewById(R.id.gridview_movie_list);
        gridViewMovie.setAdapter(new GridViewAdapter(getActivity().getBaseContext()));
    }

    /*//User declared classes
    private class FetchMovieDataTask extends AsyncTask<Void, Void, Void>{



    }*/
}
