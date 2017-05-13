package me.felixguo.expenses;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.READ_CONTACTS;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {


    // UI references.
    private CheckBox mSaveDetails;
    private Button mSignInBtn;
    private Button mRegisterBtn;
    private EditText mPasswordInput;
    private EditText mUsernameInput;
    private Button mGoToRegister;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        WheelAPI.initialize(getApplicationContext());
        // Setup References
        mSaveDetails = (CheckBox) findViewById(R.id.saveLogin);
        mSignInBtn = (Button) findViewById(R.id.signin);
        mRegisterBtn = (Button) findViewById(R.id.register);
        mPasswordInput = (EditText) findViewById(R.id.password);
        mUsernameInput = (EditText) findViewById(R.id.username);
        mGoToRegister = (Button) findViewById(R.id.goToRegister);
        mGoToRegister.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void attemptLogin(String username, String password) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("username", username);
        params.put("password", WheelAPI.hashPassword(password));
        mSignInBtn.setEnabled(false);
        WheelAPI.getInstance().makeStringRequest(WheelAPI.ApiCall.UserAuth, params, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mSignInBtn.setEnabled(true);
                WheelAPI.getInstance().ShowToast("Login Success");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mSignInBtn.setEnabled(true);
                WheelAPI.getInstance().ShowToast("Login Failed!");
            }
        });
    }

}

