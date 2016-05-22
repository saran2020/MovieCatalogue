package itsme.com.moviecatalogue;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import itsme.com.moviecatalogue.Data.MovieContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    //Constants for the class.
    private static final int DETAIL_LOADER = 101;
    private static final String DETAIL_URI = "movie_uri";
    Uri mUri;

    //Views that needs to be populated
    TextView mTitleTextView;
    ImageView mPosterView;
    TextView mReleaseYear;
    TextView mRatingTextView;
    TextView mRunTimeTextView;
    ImageButton mIsFavImageButton;
    TextView mSynopsisTextView;
    ListView mTrailerListView;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DetailActivityFragment.DETAIL_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        mTitleTextView = (TextView) rootView.findViewById(R.id.movie_title);
        mPosterView = (ImageView) rootView.findViewById(R.id.poster_image);
        mReleaseYear = (TextView) rootView.findViewById(R.id.release_year);
        mRatingTextView = (TextView) rootView.findViewById(R.id.ratings);
        mRunTimeTextView = (TextView) rootView.findViewById(R.id.run_time);
        mIsFavImageButton = (ImageButton) rootView.findViewById(R.id.is_fav);
        mSynopsisTextView = (TextView) rootView.findViewById(R.id.synopsis);
        mTrailerListView = (ListView) rootView.findViewById(R.id.trailer_list_view);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if (mUri != null) {

            return new CursorLoader(getActivity(),
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
            mTitleTextView.setText(data.getString(PROJ_TITLE));
            mPosterView.setImageBitmap(Utility.getImage(data.getBlob(PROJ_POSTER)));
            mReleaseYear.setText(Utility.getYear(data.getString(PROJ_RELEASE_DATE)));
            mRatingTextView.setText(Utility.getRating(data.getFloat(PROJ_RATING)));
            mRunTimeTextView.setText(data.getString(PROJ_RUNTIME) + " min");
            mSynopsisTextView.setText(data.getString(PROJ_OVERVIEW));
            if ("false".equals(data.getString(PROJ_IS_FAV)))
                mIsFavImageButton.setImageResource(R.drawable.ic_star_grey);
            else
                mIsFavImageButton.setImageResource(R.drawable.ic_star_yellow);
            mTrailerListView;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}