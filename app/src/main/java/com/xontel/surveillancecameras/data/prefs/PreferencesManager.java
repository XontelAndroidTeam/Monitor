package com.xontel.surveillancecameras.data.prefs;

import android.content.Context;
import android.content.SharedPreferences;

import com.xontel.surveillancecameras.data.utils.LoggedInMode;
import com.xontel.surveillancecameras.di.ApplicationContext;
import com.xontel.surveillancecameras.di.PreferenceInfo;
import com.xontel.surveillancecameras.root.AppConstant;

import javax.inject.Inject;

public class PreferencesManager implements PreferencesHelper {
    private static final String PREF_KEY_FIRST_TIME = "PREF_KEY_FIRST_TIME";
    private static final String PREF_KEY_GRID_COUNT = "PREF_KEY_GRID_COUNT";
    public static final int GRID_COUNT_DEF_VALUE = 4 ;

    private final SharedPreferences mPrefs;
    private Context mAppContext;

    @Inject
    public PreferencesManager(@ApplicationContext Context context,
                              @PreferenceInfo String prefFileName) {
        mPrefs = context.getSharedPreferences(prefFileName, Context.MODE_PRIVATE);
        mAppContext = context;
    }




    @Override
    public void setFirstTime(boolean firstTime) {
        SharedPreferences pref = mAppContext.getSharedPreferences(AppConstant.SHARED_PREF, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(PREF_KEY_FIRST_TIME, firstTime);
        editor.apply();
    }

    @Override
    public void setGridCount(int gridCount) {
        mPrefs.edit().putInt(PREF_KEY_GRID_COUNT, gridCount).apply();
    }

    @Override
    public int getGridCount() {
        return mPrefs.getInt(PREF_KEY_GRID_COUNT, GRID_COUNT_DEF_VALUE);
    }

}
