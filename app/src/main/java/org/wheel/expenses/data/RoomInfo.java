package org.wheel.expenses.data;

import org.json.JSONException;
import org.json.JSONObject;
import org.wheel.expenses.AutoDiffItem;

public class RoomInfo implements AutoDiffItem {
    private int mBalanceByUser;
    private String mId;
    private String mName;

    public RoomInfo(JSONObject jsonObject) {
        try {
            mName = jsonObject.getString("name");
            mId = jsonObject.getString("id");
            mBalanceByUser = jsonObject.getInt("balance");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return mName;
    }

    public int getBalanceByUser() {
        return mBalanceByUser;
    }

    public String getId() {
        return mId;
    }
}
