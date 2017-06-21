package org.wheel.expenses;

import android.app.Fragment;
import android.app.FragmentTransaction;

import org.json.JSONObject;
import org.wheel.expenses.Util.ErrorMessage;
import org.wheel.expenses.data.Room;
import org.wheel.expenses.data.RoomInfo;
import org.wheel.expenses.data.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivityPresenter implements ActivityLifecycleHandler,
        CreateRoomDialogFragment.CreateRoomDialogFragmentListener,
        JoinRoomDialogFragment.JoinRoomDialogFragmentListener {

    private MainActivity mActivity;
    private WheelClient mWheelClient;
    private WheelAPI mWheelAPI;
    private String mLastLoadedRoomID;
    private Fragment mDisplayedFragment;
    private boolean mLoadLock;

    public MainActivityPresenter(MainActivity activity, WheelClient wheelClient,
            WheelAPI wheelAPI) {

        mActivity = activity;
        mWheelClient = wheelClient;
        mWheelAPI = wheelAPI;
    }

    public void showDefaultUserFragment() {
        showUserFragment(mWheelClient.getCurrentUser());
    }

    public void showUserFragment(User user) {
        if (mDisplayedFragment != null) {
            mActivity.getFragmentManager().beginTransaction().remove(mDisplayedFragment).commit();
        }
        UserDisplayFragment myf = new UserDisplayFragment();
        myf.setUserToDisplay(user);
        FragmentTransaction transaction = mActivity.getFragmentManager().beginTransaction();
        transaction.add(R.id.main_fragment_container, myf);
        transaction.commit();
        mDisplayedFragment = myf;
        mActivity.hideLoading();
    }

    public void showRoomFragment(Room room) {
        RoomDisplayFragment myf = new RoomDisplayFragment();
        myf.setRoomToDisplay(room);
        FragmentTransaction transaction = mActivity.getFragmentManager().beginTransaction();
        transaction.add(R.id.main_fragment_container, myf);
        transaction.commit();
        mDisplayedFragment = myf;
    }

    public void loadFailedRefreshTryAgain() {
        loadRoom(mLastLoadedRoomID);
    }

    public void loadRoom(String roomID) {
        if (mLoadLock || roomID.isEmpty()) {
            return;
        }
        mLastLoadedRoomID = roomID;
        mLoadLock = true;
        mActivity.showLoading();
        Map<String, String> params = new HashMap<>();
        params.put("username", mWheelClient.getCurrentUsername());
        params.put("password", mWheelClient.getCurrentPassword());
        params.put("id", roomID);
        mWheelAPI.makeApiRequest(WheelAPI.ApiCall.RoomRequest,
                params, new WheelAPI.WheelAPIListener() {
                    @Override
                    public void onError(int errorCode) {
                        mWheelAPI.ShowToast(ErrorMessage.from(errorCode));
                        mLoadLock = false;
                    }

                    @Override
                    public void onSuccess(JSONObject response) {
                        mWheelClient.setCurrentRoom(new Room(response));
                        if (mDisplayedFragment != null) {
                            mActivity.getFragmentManager().beginTransaction().remove(
                                    mDisplayedFragment).commit();
                        }
                        showRoomFragment(mWheelClient.getCurrentRoom());
                        mActivity.hideLoading();
                        mLoadLock = false;
                    }

                    @Override
                    public void onConnectionError() {
                        mActivity.errorLoading();
                        mLoadLock = false;
                    }
                });

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
        loadRoom(item.getRoomId());
    }

    public void onCreateRoomClicked() {
        CreateRoomDialogFragment newFragment = new CreateRoomDialogFragment();
        newFragment.show(mActivity.getFragmentManager(), "dialog");
        newFragment.setListener(this);
    }

    public void updateActivity() {
        mWheelClient.updateCurrentUser(new WheelAPI.WheelAPIListener() {
            @Override
            public void onError(int errorCode) {
                mWheelAPI.ShowToast(ErrorMessage.from(errorCode));
            }

            @Override
            public void onSuccess(JSONObject response) {
                updateTextActivity();
            }

            @Override
            public void onConnectionError() {
                mWheelAPI.ShowToast(WheelAPI.CONNECTION_FAIL);
            }
        });
    }

    private void updateTextActivity() {
        mActivity.setDrawerText(mWheelClient.getCurrentUser().getName(),
                mActivity.getString(R.string.drawer_logged_in,
                        mWheelClient.getCurrentUser().getUsername()));
        mActivity.updateDrawerList(createDrawerRoomList());
    }

    @Override
    public void onCreate() {
        updateTextActivity();
        boolean launchDefault = true;
        if (mWheelClient.getWheelDeeplinkBundle() != null) {
            if (mWheelClient.getWheelDeeplinkBundle().getAction()
                    == WheelDeeplinkBundle.Actions.ROOM) {
                loadRoom(mWheelClient.getWheelDeeplinkBundle().getData());
                launchDefault = false;
            }
        }
        if (launchDefault) {
            showDefaultUserFragment();
        }
        mWheelClient.setWheelDeeplinkBundle(null);
    }

    @Override
    public void onSuccess() {
        updateTextActivity();
    }

    public void onJoinRoomClicked() {
        JoinRoomDialogFragment newFragment = new JoinRoomDialogFragment();
        newFragment.show(mActivity.getFragmentManager(), "dialog");
        newFragment.setListener(this);
    }
}
