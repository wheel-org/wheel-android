package org.wheel.expenses.data;

import org.json.JSONObject;

public class RoomDisplayUserInfo {
    private String mName;
    private String mUsername;
    private String mProfilePictureUrl;
    private int mBalance;
    private int mMax;

    public RoomDisplayUserInfo(JSONObject jsonObject) {
        throw new UnsupportedOperationException("Not Yet Supported!");
    }

    public RoomDisplayUserInfo(String name, int balance, int max) {
        mName = name;
        mBalance = balance;
        mMax = max;
    }

    public String getName() {
        return mName;
    }

    public int getBalance() {
        return mBalance;
    }

    public int getMax() {
        return mMax;
    }
}
