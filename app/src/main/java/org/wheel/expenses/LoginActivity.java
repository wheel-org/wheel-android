package org.wheel.expenses;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements TextWatcher {

    @BindView(R.id.login_save_details_checkbox)
    CheckBox mSaveDetails;

    @BindView(R.id.login_login_btn)
    Button mLogInBtn;

    @BindView(R.id.login_password)
    EditText mPassword;

    @BindView(R.id.login_username)
    EditText mUsername;

    @BindView(R.id.login_goto_register)
    Button mGoToRegister;

    private LoginActivityPresenter mPresenter;

    public void GoToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void GoToRegister() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.setDebug(true);
        ButterKnife.bind(this);
        mPresenter = new LoginActivityPresenter(this, StoredPreferencesManager.getInstance(),
                WheelAPI.getInstance(), WheelClient.getInstance());
        mGoToRegister.setOnClickListener((v) -> GoToRegister());
        mLogInBtn.setOnClickListener((v) -> mPresenter.onLoginClicked());
        mPresenter.onCreate();
        disableLoginButton();
        mUsername.addTextChangedListener(this);
        mPassword.addTextChangedListener(this);
    }

    public void disableLoginButton() {
        mLogInBtn.setEnabled(false);
    }

    public void enableLoginButton() {
        mLogInBtn.setEnabled(true);
    }

    public void setUsernameText(String username) {
        mUsername.setText(username);
    }

    public String getUsernameText() {
        return mUsername.getText().toString();
    }

    public String getPasswordText() {
        return mPassword.getText().toString();
    }

    public boolean isSaveDetailsChecked() {
        return mSaveDetails.isChecked();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
            Intent data) {
        if (WheelClient.getInstance().getCurrentUser() != null) {
            GoToMain();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (!getUsernameText().isEmpty() && !getPasswordText().isEmpty()) {
            enableLoginButton();
        }
        else {
            disableLoginButton();
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}

