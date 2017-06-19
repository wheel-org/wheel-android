package org.wheel.expenses.data;

import org.json.JSONException;
import org.json.JSONObject;
import org.wheel.expenses.WheelUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Transaction {
    private String mUser;
    private int mAmount;
    private String mDescription;
    private Date mCreatedDate;
    private String mId;

    static DateFormat df = new SimpleDateFormat("EEE, MMMM dd 'at' hh:mma", Locale.US);

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

    public String getDate() {
        return df.format(mCreatedDate);
    }

    public String getId() {
        return mId;
    }
}
