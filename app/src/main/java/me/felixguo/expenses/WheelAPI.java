package me.felixguo.expenses;

import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.http.AndroidHttpClient;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

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
        UserAuth, UserRegister, RoomAuth, RoomRequest, RoomCreate, AddTransaction, DeleteTransaction
    };
    public static final String userAuthURL =        "https://expensesapp-server.herokuapp.com/login";
    public static final String userRegisterURL =    "https://expensesapp-server.herokuapp.com/register";
    public static final String roomAuthURL =        "https://expensesapp-server.herokuapp.com/";
    public static final String roomRequestURL =     "https://expensesapp-server.herokuapp.com/";
    public static final String roomCreateURL =      "https://expensesapp-server.herokuapp.com/";
    public static final String addTransactionURL =  "https://expensesapp-server.herokuapp.com/";
    public static final String deleteTransactionURL = "https://expensesapp-server.herokuapp.com/";

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
    public void makeStringRequest(WheelAPI.ApiCall callType, final Map<String, String> parameters,
                                  Response.Listener<String> responseCallback,
                                  Response.ErrorListener errorCallback) {
        int methodType = POST;
        String endPoint = "";
        switch (callType) {
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
        requestQueue.add(new StringRequest(methodType, endPoint, responseCallback, errorCallback){
            @Override
            protected Map<String,String> getParams(){
                return parameters;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        });
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
    static String getSHA512Password(String passwordToHash, String salt){
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt.getBytes("UTF-8"));
            byte[] bytes = md.digest(passwordToHash.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++){
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        }
        catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return generatedPassword;
    }
    public boolean checkAPIResponse(JSONObject response) {
        try {
            if (!response.getBoolean("success")) {
                ShowToast(response.getString("data"));
            }
            return response.getBoolean("success");
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }
    public JSONObject getResponseData(JSONObject response) {
        try {
            return response.getJSONObject("data");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
