package org.wheel.expenses;

import org.json.JSONObject;
import org.wheel.expenses.Util.ErrorMessage;

import java.util.HashMap;
import java.util.Map;

public class CreateRoomDialogFragmentPresenter implements ActivityLifecycleHandler {
    private CreateRoomDialogFragment mFragment;
    private WheelClient mWheelClient;
    private WheelAPI mWheelAPI;
    private CreateRoomDialogFragment.CreateRoomDialogFragmentListener mListener;

    public CreateRoomDialogFragmentPresenter(CreateRoomDialogFragment createRoomDialogFragment,
                                             WheelClient wheelClient, WheelAPI wheelAPI) {
        mFragment = createRoomDialogFragment;
        mWheelClient = wheelClient;
        mWheelAPI = wheelAPI;
    }

    public void onCreateRoomClicked(String roomName, String roomPassword) {
        mFragment.disableButtons();
        Map<String, String> params = new HashMap<>();
        params.put("username", mWheelClient.getCurrentUser().getUsername());
        params.put("password", mWheelClient.getCurrentPassword());
        params.put("roomName", roomName);
        params.put("roomPassword", roomPassword);
        mWheelAPI.makeApiRequest(
                WheelAPI.ApiCall.RoomCreate, params,
                new WheelAPI.WheelAPIListener() {
                    @Override
                    public void onError(int errorCode) {
                        mWheelAPI.ShowToast(ErrorMessage.from(errorCode));
                        mFragment.enableButtons();
                    }

                    @Override
                    public void onSuccess(JSONObject response) {
                        mListener.onSuccess();
                        mFragment.dismissDialog();
                    }

                    @Override
                    public void onConnectionError() {
                        mWheelAPI.ShowToast(WheelAPI.CONNECTION_FAIL);
                        mFragment.enableButtons();
                    }
                });
    }

    @Override
    public void onCreate() {

    }

    public void setListener(CreateRoomDialogFragment.CreateRoomDialogFragmentListener listener) {
        mListener = listener;
    }
}
