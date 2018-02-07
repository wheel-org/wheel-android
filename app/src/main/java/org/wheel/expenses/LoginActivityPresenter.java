package org.wheel.expenses;

import org.json.JSONObject;
import org.wheel.expenses.data.User;
import org.wheel.expenses.util.ErrorMessage;
import org.wheel.expenses.util.WheelUtil;

import java.util.HashMap;

public class LoginActivityPresenter implements ActivityLifecycleHandler {

    private StoredPreferencesManager mStoredPreferencesManager;
    private WheelApi mWheelApi;
    private WheelClient mWheelClient;
    private LoginActivity mLoginActivity;

    public LoginActivityPresenter(LoginActivity loginActivity,
                                  StoredPreferencesManager storedPreferencesManager,
                                  WheelApi wheelApi,
                                  WheelClient wheelClient) {
        mLoginActivity = loginActivity;
        mStoredPreferencesManager = storedPreferencesManager;
        mWheelApi = wheelApi;
        mWheelClient = wheelClient;
        mLoginActivity.disableLoginButton();
    }

    @Override
    public void onCreate() {
        String storedUsername = mStoredPreferencesManager.getSavedUsername();
        if (!storedUsername.isEmpty()) {
            mLoginActivity.setUsernameText(storedUsername);
        }
    }

    public void onLoginClicked() {
        HashMap<String, String> params = new HashMap<>();
        final String username = mLoginActivity.getUsernameText();
        final String password = WheelUtil.hashPassword(mLoginActivity.getPasswordText());
        params.put("username", username);
        params.put("password", password);
        mLoginActivity.disableLoginButton();
        mWheelApi.makeApiRequest(WheelApi.ApiCall.UserAuth, params,
                                 new WheelApi.WheelAPIListener() {
                                     @Override
                                     public void onError(int errorCode) {
                                         mLoginActivity.enableLoginButton();
                                         mWheelApi.ShowToast(mLoginActivity,
                                                             ErrorMessage.from(errorCode));
                                     }

                                     @Override
                                     public void onSuccess(JSONObject response) {
                                         mStoredPreferencesManager.setSavedUsername(username);
                                         mStoredPreferencesManager.setSavedPassword(password);
                                         mLoginActivity.enableLoginButton();
                                         mWheelClient.setCurrentUser(new User(response), password);
                                         mLoginActivity.GoToMain();
                                     }

                                     @Override
                                     public void onConnectionError() {
                                         mLoginActivity.enableLoginButton();
                                         mWheelApi.ShowToast(mLoginActivity,
                                                             WheelApi.CONNECTION_FAIL);
                                     }
                                 });
    }
}
