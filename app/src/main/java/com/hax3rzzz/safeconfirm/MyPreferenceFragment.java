package com.hax3rzzz.safeconfirm;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by MILESWT on 7/21/2017.
 */

public class MyPreferenceFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.fragment_preference);
    }
}
