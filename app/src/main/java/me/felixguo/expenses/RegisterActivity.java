package me.felixguo.expenses;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    Button mGoToLogin;
    TextView mPotentialError;
    EditText mFullName;
    EditText mUsername;
    EditText mPassword;
    EditText mPasswordAgain;
    Button mRegister;

    public void RegisterClick(View v) {
        if (verify()) {
            HashMap<String, String> params = new HashMap<>();
            params.put("username", mUsername.getText().toString());
            params.put("password", WheelAPI.hashPassword(mPassword.getText().toString()));
            params.put("name", mFullName.getText().toString());
            WheelAPI.getInstance().makeStringRequest(WheelAPI.ApiCall.UserRegister, params, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    WheelAPI.getInstance().ShowToast(WheelAPI.CONNECTION_FAIL);
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

        mGoToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

}
