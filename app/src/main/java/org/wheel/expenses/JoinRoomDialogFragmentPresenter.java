package org.wheel.expenses;

import org.json.JSONObject;
import org.wheel.expenses.Util.ErrorMessage;

import java.util.HashMap;
import java.util.Map;

public class JoinRoomDialogFragmentPresenter implements ActivityLifecycleHandler {
    private JoinRoomDialogFragment mFragment;
    private WheelClient mWheelClient;
    private WheelAPI mWheelAPI;
    private JoinRoomDialogFragment.JoinRoomDialogFragmentListener mListener;

    public JoinRoomDialogFragmentPresenter(JoinRoomDialogFragment createRoomDialogFragment,
            WheelClient wheelClient, WheelAPI wheelAPI) {
        mFragment = createRoomDialogFragment;
        mWheelClient = wheelClient;
        mWheelAPI = wheelAPI;
    }

    public void onJoinRoomClicked(String roomId, String roomPassword) {
        Map<String, String> params = new HashMap<>();
        params.put("username", mWheelClient.getCurrentUser().getUsername());
        params.put("password", mWheelClient.getCurrentPassword());
        params.put("id", roomId);
        params.put("roomPassword", roomPassword);
        mWheelAPI.makeApiRequest(
                WheelAPI.ApiCall.RoomJoin, params,
                new WheelAPI.WheelAPIListener() {
                    @Override
                    public void onError(int errorCode) {
                        mWheelAPI.ShowToast(ErrorMessage.from(errorCode));
                    }

                    @Override
                    public void onSuccess(JSONObject response) {
                        mListener.onSuccess();
                    }

                    @Override
                    public void onConnectionError() {
                        mWheelAPI.ShowToast(WheelAPI.CONNECTION_FAIL);
                    }
                });
    }

    @Override
    public void onCreate() {

    }

    public void setListener(JoinRoomDialogFragment.JoinRoomDialogFragmentListener listener) {
        mListener = listener;
    }
}
