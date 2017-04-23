package smbdsbrain.yatask;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by Paul on 4/23/2017.
 */

public class PrefFragment extends PreferenceFragment {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);
    }

}