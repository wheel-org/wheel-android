package org.wheel.expenses;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.mikephil.charting.charts.HorizontalBarChart;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RoomDisplayFragment extends Fragment {

    @BindView(R.id.room_display_send_transaction_btn)
    Button sendBtn;

    @BindView(R.id.room_display_price_input)
    EditText priceInput;

    @BindView(R.id.room_display_desc_input)
    EditText descInput;

    @BindView(R.id.room_display_intro_text)
    TextView userWelcome;

    @BindView(R.id.room_display_disp_chart)
    HorizontalBarChart dispChart;

    @BindView(R.id.room_display_past_transactions)
    android.support.v4.widget.SwipeRefreshLayout swipeRefreshLayout;

    private RoomDisplayFragmentPresenter mPresenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        mPresenter = new RoomDisplayFragmentPresenter();
        View v = inflater.inflate(R.layout.fragment_room_display, container, false);
        ButterKnife.bind(this, v);
        mPresenter.onCreate();
        return v;
    }
}
