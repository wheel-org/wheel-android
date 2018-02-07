package org.wheel.expenses;

import org.json.JSONObject;
import org.wheel.expenses.util.ErrorMessage;
import org.wheel.expenses.data.Room;

import java.util.HashMap;
import java.util.Map;

public class CreateRoomDialogFragmentPresenter implements ActivityLifecycleHandler {
    private CreateRoomDialogFragment mFragment;
    private WheelClient mWheelClient;
    private WheelApi mWheelApi;
    private CreateRoomDialogFragment.CreateRoomDialogFragmentListener mListener;

    public CreateRoomDialogFragmentPresenter(CreateRoomDialogFragment createRoomDialogFragment,
                                             WheelClient wheelClient, WheelApi wheelApi) {
        mFragment = createRoomDialogFragment;
        mWheelClient = wheelClient;
        mWheelApi = wheelApi;
    }

    public void onCreateRoomClicked(String roomName, String roomPassword) {
        mFragment.disableButtons();
        Map<String, String> params = new HashMap<>();
        params.put("username", mWheelClient.getCurrentUser().getUsername());
        params.put("password", mWheelClient.getCurrentPassword());
        params.put("roomName", roomName);
        params.put("roomPassword", roomPassword);
        mWheelApi.makeApiRequest(
                WheelApi.ApiCall.RoomCreate, params,
                new WheelApi.WheelAPIListener() {
                    @Override
                    public void onError(int errorCode) {
                        mWheelApi.ShowToast(mFragment.getActivity(), ErrorMessage.from(errorCode));
                        mFragment.enableButtons();
                    }

                    @Override
                    public void onSuccess(JSONObject response) {
                        mWheelClient.setCurrentRoom(new Room(response));
                        mListener.onSuccess();
                        mFragment.dismissDialog();
                    }

                    @Override
                    public void onConnectionError() {
                        mWheelApi.ShowToast(mFragment.getActivity(), WheelApi.CONNECTION_FAIL);
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
