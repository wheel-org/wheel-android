package org.wheel.expenses;

import org.json.JSONObject;
import org.wheel.expenses.data.Room;
import org.wheel.expenses.util.ErrorMessage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivityPresenter implements ActivityLifecycleHandler,
                                              CreateRoomDialogFragment.CreateRoomDialogFragmentListener,
                                              JoinRoomDialogFragment.JoinRoomDialogFragmentListener {

    private MainActivity mActivity;
    private WheelClient mWheelClient;
    private WheelApi mWheelApi;
    private StoredPreferencesManager mStoredPreferencesManager;
    private String mLastLoadedRoomID;

    private RoomDisplayFragment mDisplayedFragment;

    private boolean mLoadLock;

    public MainActivityPresenter(MainActivity activity,
                                 WheelClient wheelClient,
                                 WheelApi wheelApi,
                                 StoredPreferencesManager storedPreferencesManager) {

        mActivity = activity;
        mWheelClient = wheelClient;
        mWheelApi = wheelApi;
        mStoredPreferencesManager = storedPreferencesManager;
    }

    public void updateRoomFragment(boolean fullReload) {
        if (mDisplayedFragment == null) {
            mDisplayedFragment = new RoomDisplayFragment();
            FragmentTransaction
                    transaction = mActivity.getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.main_fragment_container, mDisplayedFragment);
            transaction.commit();
        }
        mActivity.hideLoading();
        mDisplayedFragment.setRoomToDisplay(mWheelClient.getCurrentRoom(), fullReload);
    }

    public void loadFailedRefreshTryAgain() {
        makeLoadRoomRequest(mLastLoadedRoomID);
    }

    public void loadRoom(String roomID) {
        if (mDisplayedFragment != null) {
            mDisplayedFragment.updateTransactionList(new ArrayList<>(), true);
        }
        makeLoadRoomRequest(roomID);
    }

    private void makeLoadRoomRequest(String roomID) {
        if (mLoadLock || roomID.isEmpty()) {
            return;
        }
        mLoadLock = true;
        mActivity.showSmallDrawer();
        mActivity.showLoading();
        Map<String, String> params = new HashMap<>();
        params.put("username", mWheelClient.getCurrentUsername());
        params.put("password", mWheelClient.getCurrentPassword());
        params.put("id", roomID);
        mWheelApi.makeApiRequest(WheelApi.ApiCall.RoomRequest,
                                 params, new WheelApi.WheelAPIListener() {
                    @Override
                    public void onError(int errorCode) {
                        mWheelApi.ShowToast(mActivity, ErrorMessage.from(errorCode));
                        mLoadLock = false;
                    }

                    @Override
                    public void onSuccess(JSONObject response) {
                        mWheelClient.setCurrentRoom(new Room(response));
                        updateRoomFragment(true);
                        mLoadLock = false;
                        mActivity.closeDrawerIfNotLocked();
                    }

                    @Override
                    public void onConnectionError() {
                        mActivity.errorLoading();
                        mLoadLock = false;
                    }
                });
        mLastLoadedRoomID = roomID;
    }

    public void onCreateRoomClicked() {
        CreateRoomDialogFragment newFragment = new CreateRoomDialogFragment();
        newFragment.show(mActivity.getFragmentManager(), "dialog");
        newFragment.setListener(this);
    }

    public void updateActivity() {
        mWheelClient.updateCurrentUser(new WheelApi.WheelAPIListener() {
            @Override
            public void onError(int errorCode) {
                mWheelApi.ShowToast(mActivity, ErrorMessage.from(errorCode));
                mActivity.setDrawerRefreshing(false);
            }

            @Override
            public void onSuccess(JSONObject response) {
                updateTextActivity();
                mActivity.setDrawerRefreshing(false);
            }

            @Override
            public void onConnectionError() {
                mWheelApi.ShowToast(mActivity, WheelApi.CONNECTION_FAIL);
                mActivity.setDrawerRefreshing(false);
            }
        });
    }

    private void updateTextActivity() {
        mActivity.setDrawerText(mWheelClient.getCurrentUser().getName(),
                                mActivity.getString(R.string.drawer_logged_in,
                                                    mWheelClient.getCurrentUser().getUsername()));
        mActivity.updateDrawerList(mWheelClient.getCurrentUser().getActiveRooms());
        mActivity.setDrawerPicture(mWheelClient.getCurrentUser().getProfilePicture());
    }

    @Override
    public void onCreate() {
        updateTextActivity();
        if (mWheelClient.getWheelDeeplinkBundle() != null) {
            if (mWheelClient.getWheelDeeplinkBundle().getAction()
                == WheelDeeplinkBundle.Actions.ROOM) {
                String roomID = mWheelClient.getWheelDeeplinkBundle().getData();
                if (!mWheelClient.userInRoom(roomID)) {
                    onJoinRoomClickedWithParam(roomID);
                } else {
                    loadRoom(mWheelClient.getWheelDeeplinkBundle().getData());
                }
            }
        }
        mWheelClient.setWheelDeeplinkBundle(null);
    }

    private void onJoinRoomClickedWithParam(String roomID) {
        JoinRoomDialogFragment newFragment = new JoinRoomDialogFragment();
        newFragment.setInitalValue(roomID);
        newFragment.show(mActivity.getFragmentManager(), "dialog");
        newFragment.setListener(this);
    }

    @Override
    public void onSuccess() {
        updateActivity();
        updateRoomFragment(true);
    }

    public void onJoinRoomClicked() {
        onJoinRoomClickedWithParam(null);
    }

    public void onLogoutClicked() {
        mStoredPreferencesManager.setSavedPassword("");
        mWheelClient.logoutUser();
        mActivity.finish();
        Intent intent = new Intent(mActivity, LoginActivity.class);
        mActivity.startActivity(intent);
    }

    public void hideRoomFragment() {
        if (mDisplayedFragment != null) {
            mActivity.getSupportFragmentManager()
                     .beginTransaction()
                     .remove(mDisplayedFragment)
                     .commit();
            mDisplayedFragment = null;
        }
        mActivity.showLargeDrawer();
    }

    public void closeDrawerIfNotLocked() {
        mActivity.closeDrawerIfNotLocked();
    }

    public void updateUserImage(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        Map<String, String> params = new HashMap<>();
        params.put("username", mWheelClient.getCurrentUsername());
        params.put("password", mWheelClient.getCurrentPassword());
        params.put("picture", encoded);
        mWheelApi.makeApiRequest(WheelApi.ApiCall.UpdatePicture,
                                 params,
                                 new WheelApi.WheelAPIListener() {
                                     @Override
                                     public void onError(int errorCode) {
                                         mWheelApi.ShowToast(mActivity,
                                                             ErrorMessage.from(errorCode));
                                     }

                                     @Override
                                     public void onSuccess(JSONObject response) {
                                         mWheelClient.updatePicture(encoded);
                                         updateActivity();
                                         mWheelClient.updateCurrentRoom(
                                                 new WheelApi.WheelAPIListener() {
                                                     @Override
                                                     public void onError(int errorCode) {
                                                         mWheelApi.ShowToast(mActivity,
                                                                             ErrorMessage.from(
                                                                                     errorCode));
                                                     }

                                                     @Override
                                                     public void onSuccess(JSONObject response) {
                                                         mWheelClient.setCurrentRoom(new Room(
                                                                 response));
                                                         updateRoomFragment(true);
                                                     }

                                                     @Override
                                                     public void onConnectionError() {
                                                         mWheelApi.ShowToast(mActivity,
                                                                             WheelApi.CONNECTION_FAIL);
                                                     }
                                                 });
                                     }

                                     @Override
                                     public void onConnectionError() {
                                         mWheelApi.ShowToast(mActivity,
                                                             WheelApi.CONNECTION_FAIL);
                                     }
                                 });
    }
}
