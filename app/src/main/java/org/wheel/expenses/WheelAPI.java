package org.wheel.expenses;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.http.AndroidHttpClient;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import static com.android.volley.Request.Method.POST;


public class WheelAPI {
    static final String baseURL = "https://wheel-app.herokuapp.com";

    enum ApiCall {
        UserAuth("/login"),
        UserRegister("/register"),
        RoomJoin("/rooms/join"),
        RoomRequest("/rooms/get"),
        RoomCreate("/rooms/create"),
        AddTransaction("/transactions/add"),
        DeleteTransaction("/transactions/delete"),
        LeaveRoom("/rooms/leave");

        private String mUrl;

        public String getUrl() {
            return mUrl;
        }

        ApiCall(String url) {
            mUrl = url;
        }
    }

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
        String endPoint = baseURL + callType.getUrl();

        if (parameters == null) {
            parameters = new HashMap<>();
        }
        JSONObject params = new JSONObject(parameters);
        requestQueue.add(new JsonObjectRequest(POST,
                                               endPoint,
                                               params,
                                               onSuccess(responseCallback),
                                               onError(responseCallback)));
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
