package org.wheel.expenses;

import org.json.JSONObject;
import org.wheel.expenses.data.User;

import java.util.HashMap;

public class LoginActivityPresenter implements ActivityLifecycleHandler {

    private StoredPreferencesManager mStoredPreferencesManager;
    private WheelAPI mWheelAPI;
    private WheelClient mWheelClient;
    private LoginActivity mLoginActivity;

    public LoginActivityPresenter(LoginActivity loginActivity,
            StoredPreferencesManager storedPreferencesManager,
            WheelAPI wheelAPI,
            WheelClient wheelClient) {
        mLoginActivity = loginActivity;
        mStoredPreferencesManager = storedPreferencesManager;
        mWheelAPI = wheelAPI;
        mWheelClient = wheelClient;
    }

    @Override
    public void onCreate() {
        String storedUsername = mStoredPreferencesManager.getSavedUsername();
        if (!storedUsername.isEmpty()) {
            mLoginActivity.setUsernameText(storedUsername);
        }
    }

    public void onLoginClicked() {
        HashMap<String, String> params = new HashMap<String, String>();
        final String username = mLoginActivity.getUsernameText();
        final String password = WheelAPI.hashPassword(mLoginActivity.getPasswordText());
        params.put("username", username);
        params.put("password", password);
        mLoginActivity.disableLoginButton();
        mWheelAPI.makeApiRequest(WheelAPI.ApiCall.UserAuth, params,
                new WheelAPI.WheelAPIListener() {
                    @Override
                    public void onError(int errorCode) {
                        mLoginActivity.enableLoginButton();
                        mWheelAPI.ShowToast(ErrorMessage.from(errorCode));
                    }

                    @Override
                    public void onSuccess(JSONObject response) {
                        if (mLoginActivity.isSaveDetailsChecked()) {
                            mStoredPreferencesManager.setSavedUsername(username);
                            mStoredPreferencesManager.setSavedPassword(password);
                        } else {
                            mStoredPreferencesManager.setSavedPassword("");
                            mStoredPreferencesManager.setSavedUsername("");
                        }
                        mLoginActivity.enableLoginButton();
                        mWheelClient.setCurrentUser(new User(response), password);
                        mLoginActivity.GoToMain();
                    }

                    @Override
                    public void onConnectionError() {
                        mLoginActivity.enableLoginButton();
                        mWheelAPI.ShowToast(WheelAPI.CONNECTION_FAIL);
                    }
                });
    }
}
