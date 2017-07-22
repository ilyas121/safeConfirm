package com.hax3rzzz.safeconfirm;

import android.preference.PreferenceActivity;

import java.util.List;

/**
 * Created by MILESWT on 7/21/2017.
 */

public class MyPreferenceActivity extends PreferenceActivity
{
    @Override
    public void onBuildHeaders(List<Header> target)
    {
        loadHeadersFromResource(R.xml.headers_preference, target);
    }

    @Override
    protected boolean isValidFragment(String fragmentName)
    {
        return MyPreferenceFragment.class.getName().equals(fragmentName);
    }
}
