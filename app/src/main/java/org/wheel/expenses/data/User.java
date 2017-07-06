package org.wheel.expenses.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class User {
    private String mName;
    private String mUsername;
    private String mProfilePicture;
    private ArrayList<RoomInfo> mActiveRooms;

    public User(JSONObject object) {
        try {
            mUsername = object.getString("username");
            mName = object.getString("name");
            mProfilePicture = object.getString("picture");
            JSONArray rooms = object.getJSONArray("rooms");
            mActiveRooms = new ArrayList<>();
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

    public ArrayList<RoomInfo> getActiveRooms() {
        return mActiveRooms;
    }

    public String getProfilePicture() {
        return mProfilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        mProfilePicture = profilePicture;
    }
}
