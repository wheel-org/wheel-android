package org.wheel.expenses.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class User {
    private String mName;
    private String mUsername;
    private String mProfilePictureUrl;
    private ArrayList<RoomInfo> mActiveRooms;

    public User(JSONObject object) {
        try {
            mUsername = object.getString("username");
            mName = object.getString("name");
            JSONArray rooms = object.getJSONArray("rooms");
            for (int i = 0; i < rooms.length(); i++) {
                mActiveRooms.add(new RoomInfo(rooms.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return mName;
    }

    public String getUsername() {
        return mUsername;
    }
}
