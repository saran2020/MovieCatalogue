package itsme.com.moviecatalogue;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by its me on 17-Feb-16.
 */
public class GridViewAdapter extends BaseAdapter {

    //Constants and Global variables
    private Context mContext;
    private Integer[] imageResource = {
            R.drawable.w185
    };

    //Constructor
    public GridViewAdapter(Context context) {
        this.mContext = context;
    }

    //OverridedMethods
    @Override
    public int getCount() {
        return 15;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView
            , ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView
                    .LayoutParams(GridView.LayoutParams.MATCH_PARENT
                    , GridView.LayoutParams.MATCH_PARENT));

            //Cropping the image to fit the screen so that there is no gap in the GridView between two cols.
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView) convertView;
        }
        Picasso.with(mContext)
                .load(imageResource[0])
                .into(imageView);

        return imageView;
    }
}