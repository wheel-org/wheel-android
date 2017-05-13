package me.felixguo.expenses;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;

import java.util.HashMap;

/**
 * Created by Felix on 5/6/2017.
 */

public class StoredPreferencesManager {
    public enum PreferenceKey {
        SAVEDUSERNAME,
        SAVEDPASSWORD
    }

    private String mPreferenceKey;
    private Context mContext;
    private static StoredPreferencesManager mInstance;
    public StoredPreferencesManager(Context context) {
        mPreferenceKey = "WheelPreferences";
        mContext = context;
    }
    public static StoredPreferencesManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new StoredPreferencesManager(context);
        }
        return mInstance;
    }
    private String getPreferenceKeyString(PreferenceKey key) {
        return mPreferenceKey + String.valueOf(key);
    }
    public String getPreference(PreferenceKey key) {
        return mContext.getSharedPreferences(mPreferenceKey, 0).getString(getPreferenceKeyString(key), "");
    }
    public void setPreference(PreferenceKey key, String value) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences(mPreferenceKey, 0).edit();
        editor.putString(getPreferenceKeyString(key), value);
        editor.apply();
    }
}
