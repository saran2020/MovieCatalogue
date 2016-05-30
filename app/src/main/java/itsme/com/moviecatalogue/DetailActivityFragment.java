package itsme.com.moviecatalogue;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
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
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import itsme.com.moviecatalogue.Data.MovieContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    //Constants for the class.
    private static final int DETAIL_LOADER = 101;
    public static final String DETAIL_URI = "movie_uri";
    private static final String YOUTUBE_LINK = "http://www.youtube.com/watch?v="; //Default link to the trailers

    //Global variables
    Uri mUri;
    Context mContext;
    boolean isFav; //Will carry if the movie is marked as fav. This will be used to populate the db in the end.
    String movieTitle; //Will have the title of the movie being shown in the detailView
    long movieId; //Will have the movieId of the movie being shown in the detailView
    String[] trailerKeys; //Will hav all the keys to the trailers.

    //Views that needs to be populated
    TextView mTitleTextView;
    ImageView mPosterView;
    TextView mReleaseYear;
    TextView mRatingTextView;
    ImageButton mIsFavImageButton;
    TextView mSynopsisTextView;
    ListView mTrailerListView;

    //Adapter for the Trailer List View
    ArrayAdapter<String> mAdapter;

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
            MovieContract.Movie.COLUMN_IS_FAVOURITE,
            MovieContract.Movie.COLUMN_TRAILERS
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
    public static final int PROJ_TRAILER = 11;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity();

        if (savedInstanceState == null) {

            //Get the uri for the data of the detailView. Also get the movie id from the uri
            Bundle arguments = getArguments();
            if (arguments != null) {
                mUri = arguments.getParcelable(DetailActivityFragment.DETAIL_URI);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        mAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.trailer_layout, //Sets the layout that needs to be populated
                R.id.trailer_text_view, //Sets the id of the TextView that needs to be populated with the contents in the array.
                new ArrayList<String>()); //Send an empty List at the beginning.

        mTitleTextView = (TextView) rootView.findViewById(R.id.movie_title);
        mPosterView = (ImageView) rootView.findViewById(R.id.poster_image);
        mReleaseYear = (TextView) rootView.findViewById(R.id.release_year);
        mRatingTextView = (TextView) rootView.findViewById(R.id.ratings);
        mIsFavImageButton = (ImageButton) rootView.findViewById(R.id.is_fav);
        mSynopsisTextView = (TextView) rootView.findViewById(R.id.synopsis);
        mTrailerListView = (ListView) rootView.findViewById(R.id.trailer_list_view);

        mIsFavImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFav) {
                    isFav = false;
                    mIsFavImageButton.setImageResource(R.drawable.ic_star_grey);
                    Toast.makeText(mContext,
                            movieTitle + " removed from favourite", Toast.LENGTH_SHORT).show();
                } else {
                    isFav = true;
                    mIsFavImageButton.setImageResource(R.drawable.ic_star_yellow);
                    Toast.makeText(mContext,
                            movieTitle + " added to favourite", Toast.LENGTH_SHORT).show();
                }

            }
        });

        // Set the adapter to the listView and also handel the click of the the item.
        mTrailerListView.setAdapter(mAdapter);
        mTrailerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                watchTrailerYoutube(position);
            }
        });

        //Will set the scrolling for the View
        mSynopsisTextView.setMovementMethod(new ScrollingMovementMethod());

        return rootView;
    }

    private void watchTrailerYoutube(int position) {
        if (trailerKeys != null) {
            try {
                startActivity(new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("vnd.youtube:" + trailerKeys[position])));
            } catch (ActivityNotFoundException ex) {
                startActivity(new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(YOUTUBE_LINK + trailerKeys[position])));
            }
        }
    }

    /**
     * Before leaving the Fragment we have to update the current status of the fav into the db.
     * It checks if the constant isFav is true or not. If true sets to 1 or else sets to 0.
     */
    @Override
    public void onPause() {
        super.onPause();
        ContentValues cv = new ContentValues();
        cv.put(MovieContract.Movie.COLUMN_IS_FAVOURITE, (isFav) ? 1 : 0);
        String whereCluse = MovieContract.Movie.COLUMN_MOVIE_ID + "=?";
        String[] whereArgs = {Long.toString(movieId)};
        mContext.getContentResolver().update(
                MovieContract.Movie.buildMovieUri(movieId),
                cv,
                whereCluse,
                whereArgs);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if (mUri != null) {

            return new CursorLoader(mContext,
                    mUri,
                    MOVIE_PROJECTION,
                    null,
                    null,
                    null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            movieId = data.getLong(PROJ_MOVIE_ID);
            movieTitle = data.getString(PROJ_TITLE);
            mTitleTextView.setText(movieTitle);
            mPosterView.setImageBitmap(Utility.getImage(data.getBlob(PROJ_POSTER)));
            mReleaseYear.setText(Utility.getYear(data.getString(PROJ_RELEASE_DATE)));
            mRatingTextView.setText(Utility.getRating(data.getFloat(PROJ_RATING)));
            mSynopsisTextView.setText(data.getString(PROJ_OVERVIEW));
            isFav = (1 == (data.getInt(PROJ_IS_FAV)));
            if (isFav)
                mIsFavImageButton.setImageResource(R.drawable.ic_star_yellow);
            else
                mIsFavImageButton.setImageResource(R.drawable.ic_star_grey);

            // Get the trailer keys and update the list View based on the no of keys
            mAdapter.clear();

            String trailer = data.getString(PROJ_TRAILER);
            if (null != trailer) {
                if (trailer.contains(","))
                    trailerKeys = trailer.split(",");
                else
                    trailerKeys = new String[]{trailer};
                for (int i = 0; i < trailerKeys.length; i++) {
                    mAdapter.add("Trailer " + (i + 1));
                }
            }
            setHeightsBasedOnItems();
        }
    }

    /**
     * This sets the height of the List view based on number of contents in the view.
     * And wil also call the method to set the heights of the scrollView at the end.
     *
     * @return True if all went well. False or else.
     */
    private boolean setHeightsBasedOnItems() {

        if (mAdapter != null) {

            int numberOfItems = mAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = mAdapter.getView(itemPos, null, mTrailerListView);
                item.measure(0, 0);
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = mTrailerListView.getDividerHeight() *
                    (numberOfItems - 1);

            // Set list height.
            ViewGroup.LayoutParams params = mTrailerListView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight;
            mTrailerListView.setLayoutParams(params);
            mTrailerListView.requestLayout();

            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}