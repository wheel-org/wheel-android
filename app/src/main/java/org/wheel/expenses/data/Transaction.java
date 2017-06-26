package org.wheel.expenses.data;

import org.json.JSONException;
import org.json.JSONObject;
import org.wheel.expenses.AutoDiffItem;
import org.wheel.expenses.util.WheelUtil;

import java.util.Date;

public class Transaction implements AutoDiffItem {
    private String mUser;
    private int mAmount;
    private String mDescription;
    private Date mCreatedDate;
    private String mId;

    public Transaction(JSONObject jsonObject) {
        try {
            mUser = jsonObject.getString("username");
            mId = jsonObject.getString("id");
            mAmount = jsonObject.getInt("amount");
            mCreatedDate = new Date(jsonObject.getLong("date"));
            mDescription = jsonObject.getString("desc");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getUser() {
        return mUser;
    }

    public String getAmount() {
        return WheelUtil.getStringFromPrice(mAmount);
    }

    public String getDescription() {
        return mDescription;
    }

    public String getDateString() {
        return WheelUtil.getFriendlyDateString(mCreatedDate);
    }

    public Date getDate() {
        return mCreatedDate;
    }

    public String getId() {
        return mId;
    }
}
