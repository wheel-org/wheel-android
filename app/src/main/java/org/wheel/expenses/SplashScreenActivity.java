package org.wheel.expenses;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

import org.json.JSONObject;
import org.wheel.expenses.data.User;

import java.util.HashMap;

public class SplashScreenActivity extends AppCompatActivity {

    private void startMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void startLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ApplicationStateManager.initialize();
        WheelAPI.initialize(getApplicationContext());
        String storedUsername = StoredPreferencesManager.getInstance(getApplicationContext())
                .getPreference(StoredPreferencesManager.PreferenceKey.SAVEDUSERNAME);
        String storedPassword = StoredPreferencesManager.getInstance(getApplicationContext())
                .getPreference(StoredPreferencesManager.PreferenceKey.SAVEDPASSWORD);
        if (!storedUsername.isEmpty() && !storedPassword.isEmpty()) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("username", storedUsername);
            params.put("password", storedPassword);
            WheelAPI.getInstance().makeApiRequest(WheelAPI.ApiCall.UserAuth, params, new WheelAPI.WheelAPIListener() {
                @Override
                public void onError(String error) {
                    StoredPreferencesManager.getInstance(getApplicationContext())
                            .setPreference(StoredPreferencesManager.PreferenceKey.SAVEDPASSWORD, "");
                    startLogin();
                }

                @Override
                public void onSuccess(JSONObject response) {
                    // Should be a User Object
                    ApplicationStateManager.getInstance().setCurrentUser(new User(response));
                    startMain();
                }
            });
        }

    }
}
