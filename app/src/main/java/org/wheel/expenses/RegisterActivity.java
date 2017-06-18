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

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterActivity extends AppCompatActivity implements TextWatcher {

    @BindView(R.id.register_goto_login)
    Button mGoToLogin;

    @BindView(R.id.register_potential_error)
    TextView mPotentialError;

    @BindView(R.id.register_full_name)
    EditText mFullName;

    @BindView(R.id.register_username)
    EditText mUsername;

    @BindView(R.id.register_password)
    EditText mPassword;

    @BindView(R.id.register_password_again)
    EditText mPasswordAgain;

    @BindView(R.id.register_register_btn)
    Button mRegisterBtn;

    @BindView(R.id.register_save_details_checkbox)
    CheckBox mSaveDetails;

    private RegisterActivityPresenter mPresenter;

    public String getFullName() {
        return mFullName.getText().toString();
    }

    public String getUsername() {
        return mUsername.getText().toString();
    }

    public String getPassword() {
        return mPassword.getText().toString();
    }

    public String getPasswordAgain() {
        return mPasswordAgain.getText().toString();
    }

    public boolean isSaveDetailsChecked() {
        return mSaveDetails.isChecked();
    }

    public void showPotentialError() {
        mPotentialError.setVisibility(View.VISIBLE);
    }

    public void hidePotentialError() {
        mPotentialError.setVisibility(View.GONE);
    }

    public void enableRegisterBtn() {
        mRegisterBtn.setEnabled(true);
    }

    public void disableRegisterBtn() {
        mRegisterBtn.setEnabled(false);
    }

    public void onRegisterComplete() {
        setResult(Activity.RESULT_OK, null);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        mPresenter = new RegisterActivityPresenter(this, StoredPreferencesManager.getInstance(),
                WheelAPI.getInstance(), WheelClient.getInstance());

        mPotentialError.setVisibility(View.GONE);
        mFullName.addTextChangedListener(this);
        mUsername.addTextChangedListener(this);
        mPassword.addTextChangedListener(this);
        mPasswordAgain.addTextChangedListener(this);
        mGoToLogin.setOnClickListener(view -> finish());
        mRegisterBtn.setOnClickListener(view -> mPresenter.onRegisterClick());
        mPotentialError.setText(R.string.register_password_not_match);

        mPresenter.onCreate();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        mPresenter.onTextChanged();
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
