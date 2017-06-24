package org.wheel.expenses.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Room {
    private Map<String, Integer> mUsers;
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
            mUsers = new HashMap<>();
            mTransactions = new ArrayList<>();
            for (int i = 0; i < userMap.length(); i++) {
                mUsers.put(userMap.getJSONObject(i).getString("user"),
                           userMap.getJSONObject(i).getInt("balance"));
            }
            JSONArray transactions = jsonObject.getJSONArray("transactions");
            for (int i = 0; i < transactions.length(); i++) {
                mTransactions.add(new Transaction(transactions.getJSONObject(i)));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Integer> getUsers() {
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
