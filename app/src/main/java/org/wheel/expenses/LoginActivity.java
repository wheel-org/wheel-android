package org.wheel.expenses;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import org.json.JSONObject;
import org.wheel.expenses.data.User;

import java.util.HashMap;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
    private final static int REGISTER_RESULT = 1;
    // UI references.
    private CheckBox mSaveDetails;
    private Button mSignInBtn;
    private Button mRegisterBtn;
    private EditText mPassword;
    private EditText mUsername;
    private Button mGoToRegister;

    private void startMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        String storedUsername = StoredPreferencesManager.getInstance(getApplicationContext())
                .getPreference(StoredPreferencesManager.PreferenceKey.SAVEDUSERNAME);
        // Setup References
        mSaveDetails = (CheckBox) findViewById(R.id.save_login);
        mSignInBtn = (Button) findViewById(R.id.signin);
        mRegisterBtn = (Button) findViewById(R.id.register);
        mPassword = (EditText) findViewById(R.id.password);
        mUsername = (EditText) findViewById(R.id.username);
        mGoToRegister = (Button) findViewById(R.id.goToRegister);
        mGoToRegister.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                ApplicationStateManager.getInstance().setCurrentUser(null);
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivityForResult(intent, REGISTER_RESULT);
            }
        });
        if (!storedUsername.isEmpty()) {
            mUsername.setText(storedUsername);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REGISTER_RESULT) {
            if (ApplicationStateManager.getInstance().getCurrentUser() != null) {
                startMain();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void LoginClick(View v) {
        attemptLogin(mUsername.getText().toString(), mPassword.getText().toString());
    }

    private void attemptLogin(String username, String password) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("username", username);
        params.put("password", WheelAPI.hashPassword(password));
        mSignInBtn.setEnabled(false);
        WheelAPI.getInstance().makeApiRequest(WheelAPI.ApiCall.UserAuth, params, new WheelAPI.WheelAPIListener() {
            @Override
            public void onError(String error) {
                mSignInBtn.setEnabled(true);
                WheelAPI.getInstance().ShowToast(error);
            }

            @Override
            public void onSuccess(JSONObject response) {
                if (mSaveDetails.isChecked()) {
                    StoredPreferencesManager.getInstance(getApplicationContext())
                            .setPreference(StoredPreferencesManager.PreferenceKey.SAVEDUSERNAME,
                                    mUsername.getText().toString());
                    StoredPreferencesManager.getInstance(getApplicationContext())
                            .setPreference(StoredPreferencesManager.PreferenceKey.SAVEDPASSWORD,
                                    WheelAPI.hashPassword(mPassword.getText().toString()));
                } else {
                    StoredPreferencesManager.getInstance(getApplicationContext())
                            .setPreference(StoredPreferencesManager.PreferenceKey.SAVEDUSERNAME, "");
                    StoredPreferencesManager.getInstance(getApplicationContext())
                            .setPreference(StoredPreferencesManager.PreferenceKey.SAVEDPASSWORD, "");
                }
                mSignInBtn.setEnabled(true);
                WheelAPI.getInstance().ShowToast("Logging in!");
                ApplicationStateManager.getInstance().setCurrentUser(new User(response));
                startMain();
            }

            @Override
            public void onConnectionError() {
                WheelAPI.getInstance().ShowToast(WheelAPI.CONNECTION_FAIL);
            }
        });
    }

}

