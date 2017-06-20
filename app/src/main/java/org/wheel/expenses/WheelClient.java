package org.wheel.expenses;

import android.content.Context;

import org.json.JSONObject;
import org.wheel.expenses.data.Room;
import org.wheel.expenses.data.User;

import java.util.HashMap;
import java.util.Map;

public class WheelClient {
    private static WheelClient mInstance;
    private User mCurrentUser;
    private Room mCurrentRoom;
    private String mCurrentPassword;
    private String mCurrentUsername;

    public WheelClient() {
        mCurrentRoom = null;
        mCurrentUser = null;
    }

    public static void initialize(Context context) {
        mInstance = new WheelClient();
        WheelAPI.initialize(context);
        StoredPreferencesManager.initialize(context);
    }

    public static WheelClient getInstance() {
        return mInstance;
    }

    public User getCurrentUser() {
        return mCurrentUser;
    }

    public void setCurrentUser(User mCurrentUser, String password) {
        this.mCurrentUser = mCurrentUser;
        this.mCurrentPassword = password;
        this.mCurrentUsername = mCurrentUser.getUsername();
    }

    public Room getCurrentRoom() {
        return mCurrentRoom;
    }

    public void setCurrentRoom(Room mCurrentRoom) {
        this.mCurrentRoom = mCurrentRoom;
    }

    public String getCurrentPassword() {
        return mCurrentPassword;
    }

    public void updateCurrentUser(final WheelAPI.WheelAPIListener callback) {
        Map<String, String> params = new HashMap<>();
        params.put("username", mCurrentUsername);
        params.put("password", mCurrentPassword);
        WheelAPI.getInstance().makeApiRequest(WheelAPI.ApiCall.UserAuth,
                                              params,
                                              new WheelAPI.WheelAPIListener() {
                                                  @Override
                                                  public void onError(int errorCode) {
                                                      if (callback != null) {
                                                          callback.onError(errorCode);
                                                      }
                                                  }

                                                  @Override
                                                  public void onSuccess(JSONObject response) {
                                                      setCurrentUser(new User(response),
                                                                     mCurrentPassword);
                                                      if (callback != null) {
                                                          callback.onSuccess(response);
                                                      }
                                                  }

                                                  @Override
                                                  public void onConnectionError() {
                                                      if (callback != null) {
                                                          callback.onConnectionError();
                                                      }
                                                  }
                                              });
    }

    public void updateCurrentRoom(final WheelAPI.WheelAPIListener callback) {
        Map<String, String> params = new HashMap<>();
        params.put("username", mCurrentUsername);
        params.put("password", mCurrentPassword);
        params.put("id", mCurrentRoom.getId());
        WheelAPI.getInstance().makeApiRequest(WheelAPI.ApiCall.RoomRequest,
                                              params,
                                              new WheelAPI.WheelAPIListener() {
                                                  @Override
                                                  public void onError(int errorCode) {
                                                      if (callback != null) {
                                                          callback.onError(errorCode);
                                                      }
                                                  }

                                                  @Override
                                                  public void onSuccess(JSONObject response) {
                                                      setCurrentRoom(new Room(response));
                                                      if (callback != null) {
                                                          callback.onSuccess(response);
                                                      }
                                                  }

                                                  @Override
                                                  public void onConnectionError() {
                                                      if (callback != null) {
                                                          callback.onConnectionError();
                                                      }
                                                  }
                                              });
    }

    public String getCurrentUsername() {
        return mCurrentUsername;
    }

    public boolean hasCurrentUser() {
        return mCurrentUser != null;
    }

    public boolean hasCurrentRoom() {
        return mCurrentRoom != null;
    }

    public void resetWheelClient() {
        mCurrentUser = null;
        mCurrentPassword = "";
        mCurrentRoom = null;
    }
}
