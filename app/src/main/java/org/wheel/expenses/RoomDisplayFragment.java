package org.wheel.expenses;

import org.wheel.expenses.data.Room;
import org.wheel.expenses.data.Transaction;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.KeyEvent.KEYCODE_DEL;

public class RoomDisplayFragment extends Fragment implements MainActivityContentFragment,
                                                             TextWatcher {

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
    android.support.v4.widget.SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.room_display_past_transactions)
    ListView mTransactionsListView;

    TransactionListAdapter mTransactionListAdapter;

    private RoomDisplayFragmentPresenter mPresenter;
    private Room mRoomToDisplay;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mPresenter = new RoomDisplayFragmentPresenter(this,
                                                      mRoomToDisplay,
                                                      WheelClient.getInstance(),
                                                      WheelAPI.getInstance());
        View v = inflater.inflate(R.layout.fragment_room_display, container, false);
        ButterKnife.bind(this, v);

        mTransactionListAdapter = new TransactionListAdapter(this.getActivity());
        mTransactionsListView.setAdapter(mTransactionListAdapter);
        mSwipeRefreshLayout.setOnRefreshListener(mPresenter);
        mSendBtn.setOnClickListener(view -> mPresenter.onSendTransactionClicked());
        mPriceInput.setCursorVisible(false);
        mPriceInput.setOnKeyListener(new View.OnKeyListener() {
            StringBuilder input = new StringBuilder();

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    char uchar = (char) event.getUnicodeChar();
                    if (keyCode == KEYCODE_DEL && input.length() > 0) {
                        input.deleteCharAt(input.length() - 1);
                    } else if (uchar >= '0' && uchar <= '9') {
                        input.append(uchar);
                    }
                    int stripped = WheelUtil.getPriceFromString(input.toString());
                    String result = WheelUtil.getStringFromPrice(stripped);
                    mPriceInput.setText(result);
                }
                return true;
            }
        });
        mPriceInput.addTextChangedListener(this);
        mDescInput.addTextChangedListener(this);
        mSendBtn.setEnabled(false);
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

    public void setTransactionList(ArrayList<Transaction> list) {
        mTransactionListAdapter.update(list);
    }

    public void enableSendBtn() {
        mSendBtn.setEnabled(true);
    }

    public void disableSendBtn() {
        mSendBtn.setEnabled(false);
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
        mPresenter.onTextChanged();
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
