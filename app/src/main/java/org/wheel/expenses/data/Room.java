package org.wheel.expenses.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Room {
    private ArrayList<UserInfo> mUsers;
    private ArrayList<Transaction> mTransactions;
    private String mAdminUsername;
    private String mId;
    private String mName;

    public Room(JSONObject jsonObject) {
        try {
            mName = jsonObject.getString("name");
            mId = jsonObject.getString("id");
            if (jsonObject.has("admin")) {
                mAdminUsername = jsonObject.getString("admin");
            } else {
                mAdminUsername = "";
            }
            JSONArray userMap = jsonObject.getJSONArray("users");
            mUsers = new ArrayList<>();
            mTransactions = new ArrayList<>();
            for (int i = 0; i < userMap.length(); i++) {
                mUsers.add(new UserInfo(userMap.getJSONObject(i)));
            }
            JSONArray transactions = jsonObject.getJSONArray("transactions");
            for (int i = 0; i < transactions.length(); i++) {
                mTransactions.add(new Transaction(transactions.getJSONObject(i)));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<UserInfo> getUsers() {
        return mUsers;
    }

    public ArrayList<Transaction> getTransactions() {
        return mTransactions;
    }

    public String getName() {
        return mName;
    }

    public String getId() {
        return mId;
    }

    public String getAdminUsername() {
        return mAdminUsername;
    }
}
