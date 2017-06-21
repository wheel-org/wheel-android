package org.wheel.expenses;

import static com.android.volley.Request.Method.DELETE;
import static com.android.volley.Request.Method.POST;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.http.AndroidHttpClient;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class WheelAPI {
    enum ApiCall {
        UserAuth, UserRegister, RoomJoin, RoomRequest, RoomCreate,
        AddTransaction, DeleteTransaction, UpdateUser
    }

    private static final String userAuthURL =
            "https://wheel-app.herokuapp.com/login";
    private static final String userRegisterURL =
            "https://wheel-app.herokuapp.com/register";
    private static final String roomAuthURL =
            "https://wheel-app.herokuapp.com/rooms/join";
    private static final String roomRequestURL =
            "https://wheel-app.herokuapp.com/rooms/get";
    private static final String roomCreateURL =
            "https://wheel-app.herokuapp.com/rooms/create";
    private static final String addTransactionURL =
            "https://wheel-app.herokuapp.com/transactions/add";
    private static final String deleteTransactionURL =
            "https://wheel-app.herokuapp.com/transactions/delete";
    private static final String updateUserURL =
            "https://wheel-app.herokuapp.com/users/self";

    static final String CONNECTION_FAIL =
            "Connection to server failed! Check your internet connection?";
    private RequestQueue requestQueue;

    private static WheelAPI mInstance = null;
    private Context mContext;

    public WheelAPI(Context c) {
        mContext = c;
        String userAgent = "volley/0";
        try {
            String packageName = mContext.getPackageName();
            PackageInfo info = mContext.getPackageManager().getPackageInfo(
                    packageName, 0);
            userAgent = packageName + "/" + info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
        }
        HttpStack httpStack = new WheelHttpClientStack(
                AndroidHttpClient.newInstance(userAgent));
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
            case UserAuth:
                endPoint = userAuthURL;
                break;
            case UserRegister:
                endPoint = userRegisterURL;
                break;
            case RoomJoin:
                endPoint = roomAuthURL;
                break;
            case RoomRequest:
                endPoint = roomRequestURL;
                break;
            case UpdateUser:
                endPoint = updateUserURL;
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
        requestQueue.add(new JsonObjectRequest(methodType, endPoint, params,
                onSuccess(responseCallback), onError(responseCallback)));
    }

    public Response.Listener<JSONObject> onSuccess(
            final WheelAPIListener responseCallback) {
        return response -> {
            try {
                if (response.getBoolean("success")) {
                    responseCallback.onSuccess(
                            response.getJSONObject("data"));
                } else {
                    // Show Error Message
                    responseCallback.onError(response.getInt("data"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        };
    }

    public Response.ErrorListener onError(
            final WheelAPIListener responseCallback) {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                responseCallback.onConnectionError();
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

    public interface WheelAPIListener {
        void onError(int errorCode);

        void onSuccess(JSONObject response);

        void onConnectionError();
    }
}
