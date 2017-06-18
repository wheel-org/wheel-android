package org.wheel.expenses;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class StoredPreferencesManager {
    private final static String mSavedUsernameKey = "saved_username";
    private final static String mSavedPasswordKey = "saved_password";

    private String mPreferenceKey;
    private static StoredPreferencesManager mInstance;
    private SharedPreferences.Editor mEditor;
    private SharedPreferences mSharedPreferences;

    @SuppressLint("CommitPrefEdits")
    public StoredPreferencesManager(Context context) {
        mPreferenceKey = "WheelPreferences";
        mSharedPreferences = context.getSharedPreferences(mPreferenceKey, 0);
        mEditor = mSharedPreferences.edit();

    }

    public static void initialize(Context context) {
        mInstance = new StoredPreferencesManager(context);
    }

    public static StoredPreferencesManager getInstance() {
        return mInstance;
    }

    public String getSavedUsername() {
        return mSharedPreferences.getString(mSavedUsernameKey, "");
    }

    public String getSavedPassword() {
        return mSharedPreferences.getString(mSavedPasswordKey, "");
    }

    public void setSavedUsername(String username) {
        mEditor.putString(mSavedUsernameKey, username);
        mEditor.apply();
    }

    public void setSavedPassword(String password) {
        mEditor.putString(mSavedPasswordKey, password);
        mEditor.apply();
    }
}
