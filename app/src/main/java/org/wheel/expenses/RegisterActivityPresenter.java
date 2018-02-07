package org.wheel.expenses;

import org.json.JSONObject;
import org.wheel.expenses.data.User;
import org.wheel.expenses.util.ErrorMessage;
import org.wheel.expenses.util.WheelUtil;

import java.util.HashMap;

class RegisterActivityPresenter implements ActivityLifecycleHandler {
    private RegisterActivity mRegisterActivity;
    private StoredPreferencesManager mStoredPreferencesManager;
    private WheelApi mWheelApi;
    private WheelClient mWheelClient;

    public RegisterActivityPresenter(RegisterActivity registerActivity,
                                     StoredPreferencesManager storedPreferencesManager,
                                     WheelApi wheelApi,
                                     WheelClient wheelClient) {

        mRegisterActivity = registerActivity;
        mStoredPreferencesManager = storedPreferencesManager;
        mWheelApi = wheelApi;
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
            mWheelApi.makeApiRequest(WheelApi.ApiCall.UserRegister,
                                     params, new WheelApi.WheelAPIListener() {
                        @Override
                        public void onError(int errorCode) {
                            mWheelApi.ShowToast(mRegisterActivity, ErrorMessage.from(errorCode));
                        }

                        @Override
                        public void onSuccess(JSONObject response) {
                            mStoredPreferencesManager.setSavedUsername(username);
                            mStoredPreferencesManager.setSavedPassword(password);
                            mWheelClient.setCurrentUser(new User(response), password);
                            mRegisterActivity.onRegisterComplete();
                        }

                        @Override
                        public void onConnectionError() {
                            mWheelApi.ShowToast(mRegisterActivity, WheelApi.CONNECTION_FAIL);
                        }
                    });
        }
    }
}
