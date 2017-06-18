package org.wheel.expenses;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONObject;
import org.wheel.expenses.data.User;

import java.util.HashMap;

public class SplashScreenActivity extends AppCompatActivity {
    private TextView loadingText;
    private SwipeRefreshLayout swipeRefreshLayout;

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
        WheelClient.initialize(getApplicationContext());
        setContentView(R.layout.activity_splash_screen);
        loadingText = (TextView) findViewById(R.id.splash_loading_text);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(
                R.id.splash_swipe_layout);
        tryStart();
        swipeRefreshLayout.setOnRefreshListener(this::tryStart);
    }

    private void tryStart() {
        WheelAPI.initialize(getApplicationContext());
        String storedUsername = StoredPreferencesManager.getInstance().getSavedUsername();
        final String storedPassword = StoredPreferencesManager.getInstance().getSavedPassword();
        Log.v("Splash", storedPassword);
        loadingText.setText(R.string.splash_connect_to_server_loading_text);
        if (!storedUsername.isEmpty() && !storedPassword.isEmpty()) {
            HashMap<String, String> params = new HashMap<>();
            params.put("username", storedUsername);
            params.put("password", storedPassword);
            WheelAPI.getInstance().makeApiRequest(WheelAPI.ApiCall.UserAuth,
                    params, new WheelAPI.WheelAPIListener() {
                        @Override
                        public void onError(int errorCode) {
                            StoredPreferencesManager.getInstance().setSavedPassword("");
                            startLogin();
                        }

                        @Override
                        public void onSuccess(JSONObject response) {
                            // Should be a User Object
                            WheelClient.getInstance().setCurrentUser(new User(response),
                                    storedPassword);
                            startMain();
                        }

                        @Override
                        public void onConnectionError() {
                            swipeRefreshLayout.setRefreshing(false);
                            loadingText.setText(
                                    R.string.splash_swipe_to_try_again);
                        }
                    });
        } else {
            startLogin();
        }
    }
}
