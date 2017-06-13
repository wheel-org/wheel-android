package org.wheel.expenses;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.http.AndroidHttpClient;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.http.auth.AUTH;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import static com.android.volley.Request.Method.*;

/**
 * Created by Felix on 5/6/2017.
 */

public class WheelAPI {
    public enum ApiCall {
        UserAuth, UserRegister, RoomAuth, RoomRequest, RoomCreate,
        AddTransaction, DeleteTransaction, AuthCheck
    };

    public static final String userAuthURL =        "https://wheel-app.herokuapp.com/login";
    public static final String userRegisterURL =    "https://wheel-app.herokuapp.com/register";
    public static final String roomAuthURL =        "https://wheel-app.herokuapp.com/register";
    public static final String roomRequestURL =     "https://wheel-app.herokuapp.com/";
    public static final String roomCreateURL =      "https://wheel-app.herokuapp.com/";
    public static final String addTransactionURL =  "https://wheel-app.herokuapp.com/";
    public static final String deleteTransactionURL = "https://wheel-app.herokuapp.com/";
    public static final String authCheckURL =           "https://wheel-app.herokuapp.com/auth";

    public static final String CONNECTION_FAIL = "Connection to server failed! Check your internet connection?";
    public RequestQueue requestQueue;

    private static WheelAPI mInstance = null;
    private Context mContext;
    public WheelAPI(Context c) {
        mContext = c;
        String userAgent = "volley/0";
        try {
            String packageName = mContext.getPackageName();
            PackageInfo info = mContext.getPackageManager().getPackageInfo(packageName, 0);
            userAgent = packageName + "/" + info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {}
        HttpStack httpStack = new WheelHttpClientStack(AndroidHttpClient.newInstance(userAgent));
        requestQueue = Volley.newRequestQueue(mContext, httpStack);
    }
    public static void initialize(Context c) {
        if (mInstance == null) {
            mInstance = new WheelAPI(c);
        }
    }
    public static WheelAPI getInstance() {
        return mInstance;
    }
    public void makeApiRequest(WheelAPI.ApiCall callType,
                               Map<String, String> parameters,
                               WheelAPIListener responseCallback) {
        int methodType = POST;
        String endPoint = "";
        switch (callType) {
            case AuthCheck:
                endPoint = authCheckURL;
                methodType = GET;
                break;
            case UserAuth:
                endPoint = userAuthURL;
                break;
            case UserRegister:
                endPoint = userRegisterURL;
                break;
            case RoomAuth:
                endPoint = roomAuthURL;
                break;
            case RoomRequest:
                methodType = GET;
                endPoint = roomRequestURL;
                break;
            case RoomCreate:
                endPoint = roomCreateURL;
                break;
            case AddTransaction:
                endPoint = addTransactionURL;
                break;
            case DeleteTransaction:
                methodType = DELETE;
                endPoint = deleteTransactionURL;
                break;
        }
        if (parameters == null) {
            parameters = new HashMap<>();
        }
        JSONObject params = new JSONObject(parameters);
        requestQueue.add(new JsonObjectRequest(methodType, endPoint, params, onSuccess(responseCallback), onError()));
    }
    public Response.Listener<JSONObject> onSuccess(final WheelAPIListener responseCallback) {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getBoolean("success")) {
                         responseCallback.onSuccess(response.getJSONObject("data"));
                    }
                    else {
                        // Show Error Message
                        responseCallback.onError(response.getString("data"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }
    public Response.ErrorListener onError() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ShowToast(CONNECTION_FAIL);
            }
        };
    }

    public JSONObject getResponseData(JSONObject response) {
        try {
            return response.getJSONObject("data");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
    public void ShowToast(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }
    public static String hashPassword(String password) {
        return getSHA256Password(password);
    }
    static String getSHA256Password(String password) {
        String generatedPassword = null;

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            md.update(password.getBytes("UTF-8")); // Change this to "UTF-16" if needed
            byte[] digest = md.digest();
            generatedPassword = String.format("%064x", new java.math.BigInteger(1, digest));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }
    static String getSHA512Password(String passwordToHash, String salt) {
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt.getBytes("UTF-8"));
            byte[] bytes = md.digest(passwordToHash.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return generatedPassword;
    }
    public interface WheelAPIListener {
        void onError(String error);
        void onSuccess(JSONObject response);
    }
}
