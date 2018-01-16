package org.wheel.expenses;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.wheel.expenses.data.Room;
import org.wheel.expenses.data.Transaction;
import org.wheel.expenses.data.UserInfo;
import org.wheel.expenses.util.RecyclerViewUtil;
import org.wheel.expenses.util.WheelUtil;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RoomDisplayFragment extends Fragment implements
        TextWatcher {

    @BindView(R.id.room_display_send_transaction_btn)
    LinearLayout mSendBtn;

    @BindView(R.id.room_display_leave_room_btn)
    LinearLayout mLeaveRoom;

    @BindView(R.id.room_display_share_link_btn)
    LinearLayout mShareBtn;

    @BindView(R.id.room_display_price_input)
    EditText mPriceInput;

    @BindView(R.id.room_display_desc_input)
    EditText mDescInput;

    @BindView(R.id.room_display_intro_text)
    TextView mUserWelcome;

    @BindView(R.id.room_display_id)
    TextView mRoomID;

    @BindView(R.id.room_display_user_list)
    RecyclerView mUserListRecyclerView;

    @BindView(R.id.swipe_container)
    android.support.v4.widget.SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.room_display_past_transactions)
    RecyclerView mTransactionsRecyclerView;

    TransactionListAdapter mTransactionListAdapter;
    RoomDisplayUserListAdapter mRoomDisplayUserListAdapter;

    private RoomDisplayFragmentPresenter mPresenter;
    private Room mRoomToDisplay;
    private String mCurrentPriceString = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        mPresenter = new RoomDisplayFragmentPresenter(
                (MainActivity) this.getActivity(),
                this,
                WheelClient.getInstance(),
                WheelAPI.getInstance());
        View v = inflater.inflate(R.layout.fragment_room_display, container, false);
        ButterKnife.bind(this, v);

        mTransactionListAdapter = new TransactionListAdapter(mPresenter);
        mTransactionsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mTransactionsRecyclerView.setAdapter(mTransactionListAdapter);
        RecyclerViewUtil.Setup(mTransactionsRecyclerView);

        mRoomDisplayUserListAdapter = new RoomDisplayUserListAdapter();
        mUserListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL,
                false));
        mUserListRecyclerView.setAdapter(mRoomDisplayUserListAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(mPresenter);
        mSendBtn.setOnClickListener(view -> mPresenter.onSendTransactionClicked());
        mLeaveRoom.setOnClickListener(view -> mPresenter.onLeaveRoomClicked());
        mShareBtn.setOnClickListener(view -> mPresenter.onShareLinkClicked());
        mPriceInput.setCursorVisible(false);
        mPriceInput.addTextChangedListener(this);
        mDescInput.addTextChangedListener(this);
        mSendBtn.setEnabled(false);
        mPresenter.onCreate();
        return v;
    }

    public void setIdDisplayString(String s) {
        mRoomID.setText(s);
    }

    public void setRoomToDisplay(Room roomToDisplay, boolean fullReload) {
        mRoomToDisplay = roomToDisplay;

        if (mPresenter != null) {
            mPresenter.updateData(fullReload);
        }
    }

    public void setRoomHeaderText(String headerText) {
        mUserWelcome.setText(headerText);
    }

    public void setUserListRecyclerView(ArrayList<UserInfo> userListRecyclerView) {
        int maxValue = 0;
        for (int i = 0; i < userListRecyclerView.size(); i++) {
            maxValue = Math.max(maxValue, userListRecyclerView.get(i).getBalance());
        }
        int max = Math.max(10, (int) Math.pow(10, Math.ceil(Math.log10(maxValue))));
        for (int i = 0; i < userListRecyclerView.size(); i++) {
            userListRecyclerView.get(i).setMaxBalance(max);
        }
        mRoomDisplayUserListAdapter.update(userListRecyclerView);
    }

    public void updateTransactionList(ArrayList<Transaction> list, boolean fullReload) {
        Collections.sort(list, (t1, t2) -> t2.getDate().compareTo(t1.getDate()));
        if (fullReload) {
            mTransactionListAdapter.updateAll(list);
        } else {
            mTransactionListAdapter.update(list);
        }
        mTransactionsRecyclerView.post(() -> mTransactionsRecyclerView.smoothScrollToPosition(0));
    }

    public void enableSendBtnAndTextboxes() {
        mPriceInput.setEnabled(true);
        mDescInput.setEnabled(true);
    }

    public void disableSendBtnAndTextboxes() {
        mSendBtn.setEnabled(false);
        mPriceInput.setEnabled(false);
        mDescInput.setEnabled(false);
    }

    public void enableTransactionListInteraction() {
        mTransactionsRecyclerView.setEnabled(true);
    }

    public void disableTransactionListInteraction() {
        mTransactionsRecyclerView.setEnabled(false);
    }

    public int getPriceInput() {
        return WheelUtil.getPriceFromString(mPriceInput.getText().toString());
    }

    public String getDescription() {
        return mDescInput.getText().toString();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!s.toString().equals(mCurrentPriceString)) {
            mPriceInput.removeTextChangedListener(this);

            int stripped = WheelUtil.getPriceFromString(mPriceInput.getText().toString());
            String result = "";
            if (stripped != 0) {
                result = WheelUtil.getStringFromPrice(stripped);
            }

            mCurrentPriceString = result;
            mPriceInput.getText().clear();
            mPriceInput.getText().setFilters(new InputFilter[]{});
            mPriceInput.getText().append(result);

            mPriceInput.addTextChangedListener(this);
            mPresenter.onTextChanged();
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    public void resetTextFields() {
        mDescInput.setText("");
        mPriceInput.setText("");
        mDescInput.clearFocus();
        mPriceInput.clearFocus();
    }

    public void hideRefreshing() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    public void showRefreshing() {
        mSwipeRefreshLayout.setRefreshing(true);
    }

    public void enableSendBtn() {
        mSendBtn.setEnabled(true);
    }

    public void disableSendBtn() {
        mSendBtn.setEnabled(false);
    }
}
