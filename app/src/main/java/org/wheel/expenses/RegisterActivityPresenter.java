package org.wheel.expenses;

import org.json.JSONObject;
import org.wheel.expenses.util.ErrorMessage;
import org.wheel.expenses.util.WheelUtil;
import org.wheel.expenses.data.User;

import java.util.HashMap;

class RegisterActivityPresenter implements ActivityLifecycleHandler {
    private RegisterActivity mRegisterActivity;
    private StoredPreferencesManager mStoredPreferencesManager;
    private WheelAPI mWheelAPI;
    private WheelClient mWheelClient;

    public RegisterActivityPresenter(RegisterActivity registerActivity,
            StoredPreferencesManager storedPreferencesManager, WheelAPI wheelAPI,
            WheelClient wheelClient) {

        mRegisterActivity = registerActivity;
        mStoredPreferencesManager = storedPreferencesManager;
        mWheelAPI = wheelAPI;
        mWheelClient = wheelClient;
        mRegisterActivity.disableRegisterBtn();
    }

    @Override
    public void onCreate() {

    }

    private boolean isAnyFieldEmpty() {
        return mRegisterActivity.getFullName().isEmpty() ||
                mRegisterActivity.getUsername().isEmpty() ||
                mRegisterActivity.getPassword().isEmpty() ||
                mRegisterActivity.getPasswordAgain().isEmpty();
    }

    private boolean isPasswordsMatching() {
        return mRegisterActivity.getPassword().equals(mRegisterActivity.getPasswordAgain());
    }

    public boolean verifyRegisterFields() {
        boolean allRight = true;
        mRegisterActivity.hidePotentialError();
        if (isAnyFieldEmpty()) {
            allRight = false;
        } else if (!isPasswordsMatching()) {
            mRegisterActivity.showPotentialError();
            allRight = false;
        }
        return allRight;
    }

    public void onTextChanged() {
        if (verifyRegisterFields()) {
            mRegisterActivity.enableRegisterBtn();
        } else {
            mRegisterActivity.disableRegisterBtn();
        }
    }

    public void onRegisterClick() {
        if (verifyRegisterFields()) {
            final String username = mRegisterActivity.getUsername();
            final String fullName = mRegisterActivity.getFullName();
            final String password = WheelUtil.hashPassword(mRegisterActivity.getPassword());
            HashMap<String, String> params = new HashMap<>();
            params.put("username", username);
            params.put("password", password);
            params.put("name", fullName);
            mWheelAPI.makeApiRequest(WheelAPI.ApiCall.UserRegister,
                    params, new WheelAPI.WheelAPIListener() {
                        @Override
                        public void onError(int errorCode) {
                            mWheelAPI.ShowToast(ErrorMessage.from(errorCode));
                        }

                        @Override
                        public void onSuccess(JSONObject response) {
                            if (mRegisterActivity.isSaveDetailsChecked()) {
                                mStoredPreferencesManager.setSavedUsername(username);
                                mStoredPreferencesManager.setSavedPassword(password);
                            } else {
                                mStoredPreferencesManager.setSavedUsername("");
                                mStoredPreferencesManager.setSavedPassword("");
                            }
                            mWheelClient.setCurrentUser(new User(response), password);
                            mRegisterActivity.onRegisterComplete();
                        }

                        @Override
                        public void onConnectionError() {
                            mWheelAPI.ShowToast(WheelAPI.CONNECTION_FAIL);
                        }
                    });
        }
    }
}
