package itsme.com.moviecatalogue;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import itsme.com.moviecatalogue.Data.MovieContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

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


    //Constants and global variables
    private String TITLE;
    private String IMAGE;
    private String OVERVIEW;
    private String RELEASE_DATE;
    private Float RATING;

    //Overrided methods
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getActivity().getIntent().getExtras();
        if (bundle != null) {
            TITLE = bundle.getString("Title");
            IMAGE = bundle.getString("Image");
            OVERVIEW = bundle.getString("Overview");
            RELEASE_DATE = bundle.getString("Release date");
            RATING = bundle.getFloat("Rating");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        updateGUI(rootView);

        return rootView;
    }

    //Userdefined methods
    private void updateGUI(View rootView) {

        //Update the poster
        IMAGE = "http://image.tmdb.org/t/p/w154/" + IMAGE;
        ImageView poster = (ImageView) rootView.findViewById(R.id.imgView_Poster);
        Picasso.with(getActivity().getBaseContext())
                .load(IMAGE)
                .into(poster);

        //Update the release date
        TextView releaseDate = (TextView) rootView.findViewById(R.id.txtView_RelaseDate);
        releaseDate.setText(RELEASE_DATE);

        //Update the Overview
        TextView overView = (TextView) rootView.findViewById(R.id.txtView_OverView);
        overView.setText(OVERVIEW);

        //Update movie Title
        TextView title = (TextView) rootView.findViewById(R.id.txtView_MovieTitle);
        title.setText(TITLE);

        //Update the Ratings of thr movie
        TextView ratings = (TextView) rootView.findViewById(R.id.txtView_Ratings);
        ratings.setText(Float.toString(RATING));

        return;
    }
}