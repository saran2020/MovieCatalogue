package itsme.com.moviecatalogue;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * Created by its me on 17-Feb-16.
 */
public class GridViewAdapter extends BaseAdapter {

    //Constants and Global variables
    private Context mContext;
    private String[] imageResource;
    private boolean flag = false;
    private Integer[] image ={R.drawable.w185_2};

    //Constructor
    public GridViewAdapter(Context context, String[] Image) {
        this.mContext = context;
        this.imageResource = Image;
    }

    //OverridedMethods
    @Override
    public int getCount() {
        return imageResource.length;
        //return 3;
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

        if(!flag)
            updateResource();

        Log.v("AdapterClass:",imageResource[position]);

        Picasso.with(mContext).setLoggingEnabled(false);
        Picasso.with(mContext)
                .load(image[0])
                .into(imageView);

        return imageView;
    }

    //User Defined Methods
    private void updateResource() {
        for(int i = 0; i < imageResource.length; i++){
            imageResource[i] = "http://image.tmdb.org/t/p/w185/" + imageResource[i];
        }
        flag = true;
    }
}