package org.wheel.expenses;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
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


public class WheelApi {
    static final String baseURL = "https://wheel-app.herokuapp.com";

    enum ApiCall {
        UserAuth("/login"),
        UserRegister("/register"),
        RoomJoin("/rooms/join"),
        RoomRequest("/rooms/get"),
        RoomCreate("/rooms/create"),
        AddTransaction("/transactions/add"),
        DeleteTransaction("/transactions/delete"),
        LeaveRoom("/rooms/leave"),
        UpdatePicture("/picture");

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

    private static WheelApi mInstance = null;

    public WheelApi(RequestQueue rq) {
        requestQueue = rq;
    }

    public static void initialize(Context context) {
        if (mInstance == null) {
            String userAgent = "volley/0";
            try {
                String packageName = context.getPackageName();
                PackageInfo info = context.getPackageManager().getPackageInfo(
                        packageName, 0);
                userAgent = packageName + "/" + info.versionCode;
            } catch (PackageManager.NameNotFoundException ignored) {
            }
            HttpStack httpStack = new WheelHttpClientStack(
                    AndroidHttpClient.newInstance(userAgent));
            mInstance = new WheelApi(Volley.newRequestQueue(context, httpStack));
        }
    }

    public static WheelApi getInstance() {
        return mInstance;
    }

    public void makeApiRequest(WheelApi.ApiCall callType,
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
        return error -> responseCallback.onConnectionError();
    }

    public void ShowToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public interface WheelAPIListener {
        void onError(int errorCode);

        void onSuccess(JSONObject response);

        void onConnectionError();
    }
}
