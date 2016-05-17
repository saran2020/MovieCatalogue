package itsme.com.moviecatalogue;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

/**
 * Created by its me on 13-May-16.
 */
public class Utility {

    private static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/w185/";

    /**
     * This method gets the context and returnees the current setting
     * which user has selected for displaying the gridview with data.
     * @param context The context of the current activity
     * @return A string value represnting the current setting.
     */
    public static String getPrefferedSorting(Context context) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        String SORT_BY_POPULARITY = context.getString(R.string.list_pref_default);
        return sp.getString(context.getString(R.string.keyListPref), SORT_BY_POPULARITY);
    }

    /**
     * This method get image from the path given to us.
     *
     * @param path The path to the image to be fetched.
     * @return a byte array that can be stored into a database.
     */
    public static byte[] getByteFromImage(String path) throws IOException {
        String url = BASE_IMAGE_URL + path;
        Bitmap bitmap = getBitmapFromUrl(url);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    private static Bitmap getBitmapFromUrl(String url) throws IOException {
        URL imageUrl = new URL(url);
        return BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream());
    }

    public static Bitmap getImage(byte[] imageArray) {
        return BitmapFactory.decodeByteArray(imageArray, 0, imageArray.length);
    }
}
