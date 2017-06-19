package org.wheel.expenses;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.wheel.expenses.data.Room;

import java.util.Collections;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RoomDisplayFragment extends Fragment implements MainActivityContentFragment {

    @BindView(R.id.room_display_send_transaction_btn)
    Button mSendBtn;

    @BindView(R.id.room_display_price_input)
    EditText mPriceInput;

    @BindView(R.id.room_display_desc_input)
    EditText mDescInput;

    @BindView(R.id.room_display_intro_text)
    TextView mUserWelcome;

    @BindView(R.id.room_display_user_list)
    LinearLayout mUserList;

    @BindView(R.id.swipe_container)
    android.support.v4.widget.SwipeRefreshLayout swipeRefreshLayout;

    private RoomDisplayFragmentPresenter mPresenter;
    private Room mRoomToDisplay;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        mPresenter = new RoomDisplayFragmentPresenter(this, mRoomToDisplay,
                WheelClient.getInstance());
        View v = inflater.inflate(R.layout.fragment_room_display, container, false);
        ButterKnife.bind(this, v);
        mPresenter.onCreate();
        return v;
    }

    public void setRoomToDisplay(Room roomToDisplay) {
        mRoomToDisplay = roomToDisplay;
    }

    public void setRoomHeaderText(String headerText) {
        mUserWelcome.setText(headerText);
    }

    public void setUserList(Map<String, Integer> userList) {
        int max = (int) Math.pow(10, Math.ceil(Math.log10(Collections.max(userList.values()))));
        LayoutInflater layoutInflater = LayoutInflater.from(this.getActivity());
        mUserList.removeAllViews();
        for (Map.Entry<String, Integer> entry : userList.entrySet()) {
            View v = layoutInflater.inflate(R.layout.room_user_entry, mUserList, true);
            TextView text = (TextView) v.findViewById(R.id.user_entry_text);
            ProgressBar progressBar = (ProgressBar) v.findViewById(R.id.user_entry_progress);
            text.setText(
                    entry.getKey() + " (" + WheelUtil.getStringFromPrice(entry.getValue()) + ")");
            progressBar.setMax(max);
            progressBar.setProgress(entry.getValue());
        }

    }
}
