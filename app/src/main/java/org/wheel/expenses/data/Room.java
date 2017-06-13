package org.wheel.expenses.data;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Felix on 6/12/2017.
 */

public class Room {
    private Map<User, Double> mUsers;
    private ArrayList<Transaction> mTransactions;
    private String mAdminUsername;
    private String mId;
    private String mName;

    public Room(JSONObject jsonObject) {
        try {
            mName = jsonObject.getString("name");
            mId = jsonObject.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public Map<User, Double> getUsers() {
        return mUsers;
    }
    public ArrayList<Transaction> getTransactions() {
        return mTransactions;
    }
}
