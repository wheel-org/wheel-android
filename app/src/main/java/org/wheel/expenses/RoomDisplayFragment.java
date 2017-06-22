package org.wheel.expenses;

import org.wheel.expenses.Util.WheelUtil;
import org.wheel.expenses.data.Room;
import org.wheel.expenses.data.Transaction;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
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
                                                             TextWatcher,
                                                             AdapterView.OnItemClickListener {

    @BindView(R.id.room_display_send_transaction_btn)
    Button mSendBtn;

    @BindView(R.id.room_display_share_link_btn)
    Button mShareBtn;

    @BindView(R.id.room_display_price_input)
    EditText mPriceInput;

    @BindView(R.id.room_display_desc_input)
    EditText mDescInput;

    @BindView(R.id.room_display_intro_text)
    TextView mUserWelcome;

    @BindView(R.id.room_display_id)
    TextView mRoomID;

    @BindView(R.id.room_display_user_list)
    LinearLayout mUserList;

    @BindView(R.id.swipe_container)
    android.support.v4.widget.SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.room_display_past_transactions)
    ListView mTransactionsListView;

    @BindView(R.id.room_display_max_amount)
    TextView mMaxAmountTextView;

    @BindView(R.id.room_display_min_amount)
    TextView mMinAmountTextView;

    TransactionListAdapter mTransactionListAdapter;

    private RoomDisplayFragmentPresenter mPresenter;
    private Room mRoomToDisplay;
    private StringBuilder mPriceInputText = new StringBuilder();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mPresenter = new RoomDisplayFragmentPresenter(
                (MainActivity) this.getActivity(),
                this,
                mRoomToDisplay,
                WheelClient.getInstance(),
                WheelAPI.getInstance());
        View v = inflater.inflate(R.layout.fragment_room_display, container, false);
        ButterKnife.bind(this, v);

        mTransactionListAdapter = new TransactionListAdapter(this.getActivity());
        mTransactionsListView.setAdapter(mTransactionListAdapter);
        mTransactionsListView.setOnItemClickListener(this);
        mSwipeRefreshLayout.setOnRefreshListener(mPresenter);
        mSendBtn.setOnClickListener(view -> mPresenter.onSendTransactionClicked());
        mShareBtn.setOnClickListener(view -> mPresenter.onShareLinkClicked());
        mPriceInput.setCursorVisible(false);
        mPriceInput.setOnKeyListener((v1, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                char uchar = (char) event.getUnicodeChar();
                if (keyCode == KEYCODE_DEL && mPriceInputText.length() > 0) {
                    mPriceInputText.deleteCharAt(mPriceInputText.length() - 1);
                } else if (uchar >= '0' && uchar <= '9') {
                    mPriceInputText.append(uchar);
                }
                int stripped = WheelUtil.getPriceFromString(mPriceInputText.toString());
                String result = WheelUtil.getStringFromPrice(stripped);
                mPriceInput.setText(result);
            }
            return true;
        });
        mPriceInput.addTextChangedListener(this);
        mDescInput.addTextChangedListener(this);
        mSendBtn.setEnabled(false);
        mPresenter.onCreate();
        return v;
    }

    public void setShareBtnDisplayString(String s) {
        mShareBtn.setText(s);
    }

    public void setIdDisplayString(String s) {
        mRoomID.setText(s);
    }

    public void setRoomToDisplay(Room roomToDisplay) {
        mRoomToDisplay = roomToDisplay;
    }

    public void setRoomHeaderText(String headerText) {
        mUserWelcome.setText(headerText);
    }

    public void setUserList(Map<String, Integer> userList) {
        int max = (int) Math.pow(10, Math.ceil(Math.log10(Collections.max(userList.values()))));
        mMaxAmountTextView.setText(WheelUtil.getStringFromPrice(max));
        mMinAmountTextView.setText(WheelUtil.getStringFromPrice(0));
        LayoutInflater layoutInflater = LayoutInflater.from(this.getActivity());
        mUserList.removeAllViews();
        for (Map.Entry<String, Integer> entry : userList.entrySet()) {
            View v = layoutInflater.inflate(R.layout.room_user_entry, mUserList, false);
            TextView text = (TextView) v.findViewById(R.id.user_entry_text);
            FrameLayout.LayoutParams layoutParams =
                    (FrameLayout.LayoutParams) text.getLayoutParams();
            FrameLayout root = (FrameLayout) v.findViewById(R.id.user_entry_root);
            ProgressBar progressBar = (ProgressBar) v.findViewById(R.id.user_entry_progress);
            text.setText(
                    entry.getKey() + " (" + WheelUtil.getStringFromPrice(entry.getValue()) + ")");
            progressBar.setMax(max);
            progressBar.setProgress(entry.getValue());

            mUserList.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v,
                                           int left,
                                           int top,
                                           int right,
                                           int bottom,
                                           int oldLeft,
                                           int oldTop,
                                           int oldRight,
                                           int oldBottom) {
                    int maxWidth = mUserList.getMeasuredWidth();
                    text.measure(0, 0);
                    int textWidth = text.getMeasuredWidth();
                    int padding =
                            (int) (maxWidth * (entry.getValue() / (double) max)) - (textWidth / 2);
                    if (padding + (textWidth / 2) > maxWidth - 20) {
                        layoutParams.gravity = Gravity.RIGHT;
                        text.setPadding(0, 0, 0, 0);
                    } else {
                        layoutParams.gravity = Gravity.LEFT;
                        text.setPadding(Math.max(0, padding), 0, 0, 0);
                        text.requestLayout();
                    }
                    root.requestLayout();
                    mUserList.removeOnLayoutChangeListener(this);
                }
            });

            mUserList.addView(v);
        }
        mUserList.requestLayout();
    }

    public void setTransactionList(ArrayList<Transaction> list) {
        Collections.sort(list, (t1, t2) -> t2.getDate().compareTo(t1.getDate()));
        mTransactionListAdapter.update(list);
    }

    public void enableSendBtn() {
        mSendBtn.setEnabled(true);
    }

    public void disableSendBtn() {
        mSendBtn.setEnabled(false);
    }

    public void enableTransactionListInteraction() {
        mTransactionsListView.setEnabled(true);
    }

    public void disableTransactionListInteraction() {
        mTransactionsListView.setEnabled(false);
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

    public void resetTextFields() {
        mDescInput.setText("");
        mPriceInputText.setLength(0);
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

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
        mPresenter.promptDeleteTransaction(mTransactionListAdapter.getItem(pos));
    }
}
