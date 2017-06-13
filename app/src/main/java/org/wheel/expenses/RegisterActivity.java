package org.wheel.expenses;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;
import org.wheel.expenses.data.User;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    Button mGoToLogin;
    TextView mPotentialError;
    EditText mFullName;
    EditText mUsername;
    EditText mPassword;
    EditText mPasswordAgain;
    Button mRegister;
    CheckBox mSaveDetails;

    public void RegisterClick(View v) {
        if (verify()) {
            HashMap<String, String> params = new HashMap<>();
            params.put("username", mUsername.getText().toString());
            params.put("password", WheelAPI.hashPassword(mPassword.getText().toString()));
            params.put("name", mFullName.getText().toString());
            WheelAPI.getInstance().makeApiRequest(WheelAPI.ApiCall.UserRegister, params, new WheelAPI.WheelAPIListener() {
                @Override
                public void onError(String error) {
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
                    }
                    else {
                        StoredPreferencesManager.getInstance(getApplicationContext())
                                .setPreference(StoredPreferencesManager.PreferenceKey.SAVEDUSERNAME, "");
                        StoredPreferencesManager.getInstance(getApplicationContext())
                                .setPreference(StoredPreferencesManager.PreferenceKey.SAVEDPASSWORD, "");
                    }
                    ApplicationStateManager.getInstance().setCurrentUser(new User(response));
                    setResult(Activity.RESULT_OK, null);
                    finish(); //finish the startNewOne activity
                }
            });
        }
    }
    private boolean verify() {
        boolean allRight = true;
        mPotentialError.setText("");
        mPotentialError.setVisibility(View.GONE);
        if (mFullName.getText().toString().isEmpty() ||
                mUsername.getText().toString().isEmpty() ||
                mPassword.getText().toString().isEmpty() ||
                mPasswordAgain.getText().toString().isEmpty()) {
            allRight = false;
        }
        else if (!mPassword.getText().toString().equals(mPasswordAgain.getText().toString())) {
            mPotentialError.setText("Passwords do not match!");
            mPotentialError.setVisibility(View.VISIBLE);
            allRight = false;
        }
        return allRight;
    }
    public TextWatcher TextChangeWatch = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (verify()) {
                mRegister.setEnabled(true);
            }
            else {
                mRegister.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mGoToLogin = (Button) findViewById(R.id.goToLogin);
        mPotentialError = (TextView) findViewById(R.id.potentialError);
        mPotentialError.setVisibility(View.GONE);
        mRegister = (Button) findViewById(R.id.register);

        mFullName = (EditText) findViewById(R.id.fullname);
        mFullName.addTextChangedListener(TextChangeWatch);
        mUsername = (EditText) findViewById(R.id.username);
        mUsername.addTextChangedListener(TextChangeWatch);
        mPassword = (EditText) findViewById(R.id.password);
        mPassword.addTextChangedListener(TextChangeWatch);
        mPasswordAgain = (EditText) findViewById(R.id.passwordAgain);
        mPasswordAgain.addTextChangedListener(TextChangeWatch);
        mSaveDetails = (CheckBox) findViewById(R.id.save_login);
        mGoToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

}
