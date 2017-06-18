package org.wheel.expenses;

import org.json.JSONObject;
import org.wheel.expenses.data.Room;
import org.wheel.expenses.data.RoomInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivityPresenter implements ActivityLifecycleHandler,
        CreateRoomDialogFragment.CreateRoomDialogFragmentListener {
    private MainActivity mActivity;
    private WheelClient mWheelClient;
    private WheelAPI mWheelAPI;

    public MainActivityPresenter(MainActivity activity, WheelClient wheelClient,
            WheelAPI wheelAPI) {

        mActivity = activity;
        mWheelClient = wheelClient;
        mWheelAPI = wheelAPI;
    }

    private ArrayList<DrawerRoomEntry> createDrawerRoomList() {
        ArrayList<RoomInfo> activeRooms =
                mWheelClient.getCurrentUser()
                        .getActiveRooms();
        ArrayList<DrawerRoomEntry> result = new ArrayList<>();
        for (int i = 0; i < activeRooms.size(); i++) {
            result.add(new DrawerRoomEntry(activeRooms.get(i)));
        }
        return result;
    }

    public void onDrawerRoomItemClicked(DrawerRoomEntry item) {

        Map<String, String> params = new HashMap<>();
        params.put("username",
                mWheelClient.getCurrentUsername());
        params.put("password",
                mWheelClient.getCurrentPassword());
        params.put("id", item.getRoomId());
        mWheelAPI.makeApiRequest(WheelAPI.ApiCall.RoomRequest,
                params, new WheelAPI.WheelAPIListener() {
                    @Override
                    public void onError(int errorCode) {
                        mWheelAPI.ShowToast(
                                ErrorMessage.from(errorCode));
                    }

                    @Override
                    public void onSuccess(JSONObject response) {
                        mWheelClient.setCurrentRoom(
                                new Room(response));
                    }

                    @Override
                    public void onConnectionError() {
                        mWheelAPI.ShowToast(
                                WheelAPI.CONNECTION_FAIL);
                    }
                });

    }

    public void onCreateRoomClicked() {
        CreateRoomDialogFragment newFragment = new CreateRoomDialogFragment();
        newFragment.show(mActivity.getFragmentManager(), "dialog");
        newFragment.setListener(this);
    }

    private void updateActivity() {
        mActivity.setDrawerText(mWheelClient.getCurrentUser().getName(),
                mActivity.getString(R.string.drawer_logged_in,
                        mWheelClient.getCurrentUser().getUsername()));
        mActivity.updateDrawerList(createDrawerRoomList());
    }

    @Override
    public void onCreate() {
        updateActivity();
    }

    @Override
    public void onSuccess() {
        updateActivity();
    }
}
