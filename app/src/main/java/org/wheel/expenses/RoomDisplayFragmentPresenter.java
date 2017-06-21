package org.wheel.expenses;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;

import org.json.JSONObject;
import org.wheel.expenses.Util.ErrorMessage;
import org.wheel.expenses.Util.WheelUtil;
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
        mFragment.setShareBtnDisplayString(
                mFragment.getString(R.string.room_display_share_room));
        mFragment.setIdDisplayString(
                String.format(mFragment.getString(R.string.room_display_id_display),
                        mWheelClient.getCurrentRoom().getId()));
    }

    @Override
    public void onRefresh() {
        mWheelClient.updateCurrentRoom(new WheelAPI.WheelAPIListener() {
            @Override
            public void onError(int errorCode) {
                mWheelAPI.ShowToast(ErrorMessage.from(errorCode));
                mFragment.hideRefreshing();
            }

            @Override
            public void onSuccess(JSONObject response) {
                onCreate();
                mFragment.hideRefreshing();
            }

            @Override
            public void onConnectionError() {
                mWheelAPI.ShowToast(WheelAPI.CONNECTION_FAIL);
                mFragment.hideRefreshing();
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
                        mFragment.enableSendBtn();
                    }
                });
    }

    public void promptDeleteTransaction(Transaction transaction) {
        AlertDialog al = new AlertDialog.Builder(mFragment.getActivity()).create();
        al.setTitle(mFragment.getString(R.string.delete_transaction_title));
        al.setMessage(String.format(
                mFragment.getString(R.string.delete_transaction_confirmation_message),
                transaction.getAmount(), transaction.getDate()));
        al.setIcon(mFragment.getResources().getDrawable(R.mipmap.delete_alert));
        al.setButton(AlertDialog.BUTTON_POSITIVE, "Delete",
                (dialogInterface, i) -> deleteTransaction(transaction.getId()));
        al.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                (dialogInterface, i) -> dialogInterface.dismiss());
        al.show();
    }

    private void deleteTransaction(String transactionId) {
        HashMap<String, String> params = new HashMap<>();
        params.put("username", mWheelClient.getCurrentUsername());
        params.put("password", mWheelClient.getCurrentPassword());
        params.put("roomid", mWheelClient.getCurrentRoom().getId());
        params.put("transid", transactionId);
        mFragment.disableTransactionListInteraction();
        mWheelAPI.makeApiRequest(WheelAPI.ApiCall.DeleteTransaction, params,
                new WheelAPI.WheelAPIListener() {
                    @Override
                    public void onError(int errorCode) {
                        mWheelAPI.ShowToast(ErrorMessage.from(errorCode));
                        mFragment.enableTransactionListInteraction();
                    }

                    @Override
                    public void onSuccess(JSONObject response) {
                        mFragment.showRefreshing();
                        onRefresh();
                        mFragment.enableTransactionListInteraction();
                    }

                    @Override
                    public void onConnectionError() {
                        mWheelAPI.ShowToast(WheelAPI.CONNECTION_FAIL);
                        mFragment.enableTransactionListInteraction();
                    }
                });
    }

    public void onShareLinkClicked() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                WheelUtil.getRoomShareUrl(mWheelClient.getCurrentRoom().getId()));
        sendIntent.setType("text/plain");
        mFragment.startActivity(sendIntent);
    }
}
