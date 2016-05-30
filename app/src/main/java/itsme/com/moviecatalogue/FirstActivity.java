package itsme.com.moviecatalogue;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class FirstActivity extends AppCompatActivity implements GridViewFragment.CallBack {

    boolean isDualPane;
    String DETAIL_FRAGMENT_TAG = "detail_view";
    String myPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        myPref = Utility.getPrefferedSorting(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);


        if (null != findViewById(R.id.movie_detail_container)) {
            isDualPane = true;

            if (savedInstanceState == null) {

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.movie_detail_container,
                                new DetailActivityFragment(), DETAIL_FRAGMENT_TAG)
                        .commit();
            }
        } else {
            isDualPane = false;
        }
    }

    /**
     * Check if the preference was changed since the activity got started.
     * If yes then call the cloud get the new data and refresh the view.
     */
    @Override
    protected void onResume() {
        super.onResume();
        String newPref = Utility.getPrefferedSorting(this);
        if (null != newPref && !newPref.equals(myPref)) {
            GridViewFragment gf = (GridViewFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.fragment_grid_view);
            if (gf != null) {
                gf.onSortOrderChange();
            }
            myPref = newPref;
        }
    }

    @Override
    public void onItemSelected(Uri contentUri) {
        Log.v("FirstActivity", "URI: " + contentUri.toString());

        if (isDualPane) {

            Bundle args = new Bundle();
            args.putParcelable(DetailActivityFragment.DETAIL_URI, contentUri);

            DetailActivityFragment fragment = new DetailActivityFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction().replace(
                    R.id.movie_detail_container,
                    fragment,
                    DETAIL_FRAGMENT_TAG).commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .setData(contentUri);
            startActivity(intent);
        }
    }
}