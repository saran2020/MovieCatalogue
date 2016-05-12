package itsme.com.moviecatalogue;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by its me on 17-Feb-16.
 */
public class GridViewAdapter extends BaseAdapter {

    //Constants and Global variables
    private Context mContext;
    private String[] imageResource;
    private boolean SHOULD_UPDATE_RESOURCE = false;
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
    public View getView(int position, View convertView, ViewGroup parent) {

        //getting the layout inflater for inflating the layout.
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        ImageView imageView;

        if (convertView == null) {
            //This helps us in inflating the gridview with many imageViews.
            imageView = (ImageView) inflater.inflate(R.layout.grid_view_item, null);
        } else {
            imageView = (ImageView) convertView;
        }

        //This methods create a string array full of url for the images to be populated.
        if (!SHOULD_UPDATE_RESOURCE)
            updateResource();

        Log.v("AdapterClass:",imageResource[position]);

        Picasso.with(mContext).setLoggingEnabled(false);
        Picasso.with(mContext)
                .load(imageResource[position])
                .into(imageView);

        return imageView;
    }

    //User Defined Methods
    private void updateResource() {
        for(int i = 0; i < imageResource.length; i++){
            imageResource[i] = "http://image.tmdb.org/t/p/w185/" + imageResource[i];
        }
        SHOULD_UPDATE_RESOURCE = true;
    }
}