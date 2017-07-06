package org.wheel.expenses.data;

import org.json.JSONException;
import org.json.JSONObject;

public class UserInfo {
    private int mBalance;
    private String mId;
    private String mUsername;
    private String mProfilePicture;
    private String mName;
    private int mMaxBalance;

    public UserInfo(JSONObject jsonObject) {
        try {
            mName = jsonObject.getString("name");
            mUsername = jsonObject.getString("user");
            mProfilePicture = jsonObject.getString("picture");
            mBalance = jsonObject.getInt("balance");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return mName;
    }

    public int getBalance() {
        return mBalance;
    }

    public String getId() {
        return mId;
    }

    public String getUsername() {
        return mUsername;
    }

    public String getProfilePicture() {
        return mProfilePicture;
    }

    public int getMaxBalance() {
        return mMaxBalance;
    }

    public void setMaxBalance(int maxBalance) {
        mMaxBalance = maxBalance;
    }
}
