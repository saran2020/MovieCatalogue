package itsme.com.moviecatalogue;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/**
 * Created by its me on 22-Feb-16.
 */
public class SharedPrefrenceActivity extends PreferenceActivity
        implements Preference.OnPreferenceChangeListener{

    //Overrided methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);
        bindPreferenceSummaryToValue(findPreference(getString(R.string.keyListPref)));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String prefOption = newValue.toString();
        ListPreference listPref = (ListPreference) preference;
        int id = listPref.findIndexOfValue(prefOption);
        if(id >= 0)
            listPref.setSummary(prefOption);
        return true;
    }

    //User declared method
    private void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(this);

        onPreferenceChange(preference, PreferenceManager
                .getDefaultSharedPreferences(preference.getContext())
                .getString(preference.getKey(), ""));
    }
}
