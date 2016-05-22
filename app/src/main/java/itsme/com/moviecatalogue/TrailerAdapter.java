package itsme.com.moviecatalogue;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by its me on 23-May-16.
 */
public class TrailerAdapter extends CursorAdapter {

    Context mContext;
    private static final int NO_TRAILER = 110;
    private static final int YES_TRAILER = 111;
    private static final int VIEW_TYPE_COUNT = 2;
    private static int trailerCounter = 1;

    public TrailerAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        this.mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        int viewType = getItemViewType(cursor.getCount());
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
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? NO_TRAILER : YES_TRAILER;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }
}
