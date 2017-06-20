package org.wheel.expenses;

import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;

import org.json.JSONObject;
import org.wheel.expenses.data.Room;
import org.wheel.expenses.data.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class RoomDisplayFragmentPresenter implements ActivityLifecycleHandler,
        OnRefreshListener {
    private RoomDisplayFragment mFragment;
    private MainActivity mMainActivity;
    private Room mRoomToDisplay;
    private WheelClient mWheelClient;
    private WheelAPI mWheelAPI;

    public RoomDisplayFragmentPresenter(MainActivity mainActivity,
            RoomDisplayFragment fragment, Room roomToDisplay,
            WheelClient wheelClient, WheelAPI wheelAPI) {
        mMainActivity = mainActivity;
        mRoomToDisplay = roomToDisplay;
        mFragment = fragment;
        mWheelClient = wheelClient;
        mWheelAPI = wheelAPI;
    }

    @Override
    public void onCreate() {
        mFragment.setRoomHeaderText(mRoomToDisplay.getName());
        Map<String, Integer> users = mWheelClient.getCurrentRoom().getUsers();
        mFragment.setUserList(users);

        ArrayList<Transaction> transactions = mWheelClient.getCurrentRoom().getTransactions();
        mFragment.setTransactionList(transactions);
    }

    @Override
    public void onRefresh() {
        mWheelClient.updateCurrentRoom(new WheelAPI.WheelAPIListener() {
            @Override
            public void onError(int errorCode) {
                mWheelAPI.ShowToast(ErrorMessage.from(errorCode));
            }

            @Override
            public void onSuccess(JSONObject response) {
                onCreate();
            }

            @Override
            public void onConnectionError() {
                mWheelAPI.ShowToast(WheelAPI.CONNECTION_FAIL);
            }
        });
    }

    public void onTextChanged() {
        if (mFragment.getPriceInput() != 0 && !mFragment.getDescription().isEmpty()) {
            mFragment.enableSendBtn();
        } else {
            mFragment.disableSendBtn();
        }
    }

    public void onSendTransactionClicked() {
        HashMap<String, String> params = new HashMap<>();
        params.put("username", mWheelClient.getCurrentUsername());
        params.put("password", mWheelClient.getCurrentPassword());
        params.put("id", mWheelClient.getCurrentRoom().getId());
        params.put("amount", String.valueOf(mFragment.getPriceInput()));
        params.put("desc", mFragment.getDescription());
        mFragment.disableSendBtn();
        mWheelAPI.makeApiRequest(WheelAPI.ApiCall.AddTransaction, params,
                new WheelAPI.WheelAPIListener() {
                    @Override
                    public void onError(int errorCode) {
                        mWheelAPI.ShowToast(ErrorMessage.from(errorCode));
                        mFragment.enableSendBtn();
                    }

                    @Override
                    public void onSuccess(JSONObject response) {
                        onRefresh();
                        mMainActivity.updateActivity();
                        mFragment.resetTextFields();
                        mFragment.enableSendBtn();
                    }

                    @Override
                    public void onConnectionError() {
                        mWheelAPI.ShowToast(WheelAPI.CONNECTION_FAIL);
                    }
                });
    }
}
