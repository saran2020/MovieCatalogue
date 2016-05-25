package itsme.com.moviecatalogue.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import itsme.com.moviecatalogue.R;

/**
 * Created by its me on 23-May-16.
 */
public class TrailerAdapter<S> extends ArrayAdapter<String> {

    Context mContext;
    private static final int NO_TRAILER = 110;
    private static final int YES_TRAILER = 111;
    private static final int VIEW_TYPE_COUNT = 2;
    private static int trailerCounter = 1;
    String[] trailerLink;

    public TrailerAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.trailer_layout, parent, false);
        }

        TextView tv = (TextView) convertView.findViewById(R.id.trailer_text_view);
        tv.setText("Trailer " + (position + 1));

        return convertView;
    }

    /*public TrailerAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        this.mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        String trailerUrl = cursor.getString(DetailActivityFragment.PROJ_TRAILER);
        trailerLink = trailerUrl.split(",");
        int viewType = getItemViewType(trailerLink.length);
        int layoutId = -1;
        switch (viewType) {
            case NO_TRAILER:
                layoutId = R.layout.no_trailer_layout;
                break;
            case YES_TRAILER:
                layoutId = R.layout.trailer_layout;
                break;
        }

        View view = LayoutInflater.from(mContext).inflate(layoutId, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        if (YES_TRAILER == getItemViewType(cursor.getCount())) {
            TextView trailerTextView = (TextView) view.findViewById(R.id.trailer_text_view);
            trailerTextView.setText("Trailer " + trailerCounter);
        }
    }*/

    @Override
    public int getItemViewType(int position) {
        //return (position == 0) ? NO_TRAILER : YES_TRAILER;
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }
}
