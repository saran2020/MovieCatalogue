package itsme.com.moviecatalogue;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by its me on 13-May-16.
 */
public class Utility {

    /**
     * This method gets the context and returnees the current setting
     * which user has selected for displaying the gridview with data.
     *
     * @param context The context of the current activity
     * @return A string value represnting the current setting.
     */
    public static String getPrefferedSorting(Context context) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        String SORT_BY_POPULARITY = context.getString(R.string.list_pref_default);
        return sp.getString(context.getString(R.string.keyListPref), SORT_BY_POPULARITY);
    }
}
