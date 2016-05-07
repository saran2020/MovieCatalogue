package itsme.com.moviecatalogue;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

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