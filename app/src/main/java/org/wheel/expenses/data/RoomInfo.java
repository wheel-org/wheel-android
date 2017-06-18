package org.wheel.expenses.data;

import org.json.JSONException;
import org.json.JSONObject;

public class RoomInfo {
    private double mBalanceByUser;
    private String mId;
    private String mName;

    public RoomInfo(JSONObject jsonObject) {
        try {
            mName = jsonObject.getString("name");
            mId = jsonObject.getString("id");
            mBalanceByUser = jsonObject.getDouble("balance");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return mName;
    }

    public double getBalanceByUser() {
        return mBalanceByUser;
    }

    public String getId() {
        return mId;
    }
}
