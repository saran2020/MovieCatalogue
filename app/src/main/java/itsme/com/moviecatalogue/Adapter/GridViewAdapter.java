package itsme.com.moviecatalogue.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import itsme.com.moviecatalogue.GridViewFragment;
import itsme.com.moviecatalogue.R;
import itsme.com.moviecatalogue.Utility;

/**
 * Created by its me on 17-Feb-16.
 */
public class GridViewAdapter extends CursorAdapter {

    //Constants and Global variables
    private Context mContext;

    public GridViewAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        this.mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        //getting the layout inflater for inflating the layout.
        View view = LayoutInflater.from(mContext).inflate(R.layout.grid_view_item, null);
        view.setTag(new ViewHolder(view));
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        ImageView imageView = viewHolder.poster;
        Bitmap poster = Utility.getImage(cursor.getBlob(GridViewFragment.PROJ_POSTER));
        imageView.setImageBitmap(poster);
    }


    public static class ViewHolder {
        public final ImageView poster;

        public ViewHolder(View view) {
            poster = (ImageView) view.findViewById(R.id.image_view_poster);
        }
    }
}