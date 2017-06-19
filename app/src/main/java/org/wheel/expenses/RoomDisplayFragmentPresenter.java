package org.wheel.expenses;

import org.wheel.expenses.data.Room;

import java.util.Map;

class RoomDisplayFragmentPresenter implements ActivityLifecycleHandler {
    private RoomDisplayFragment mFragment;
    private Room mRoomToDisplay;
    private WheelClient mWheelClient;

    public RoomDisplayFragmentPresenter(RoomDisplayFragment fragment, Room roomToDisplay,
            WheelClient wheelClient) {
        mRoomToDisplay = roomToDisplay;
        mFragment = fragment;
        mWheelClient = wheelClient;
    }

    @Override
    public void onCreate() {
        mFragment.setRoomHeaderText(mRoomToDisplay.getName());
        Map<String, Integer> users = mWheelClient.getCurrentRoom().getUsers();
        mFragment.setUserList(users);
    }
}
