package org.wheel.expenses;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.wheel.expenses.data.RoomInfo;
import org.wheel.expenses.util.WheelUtil;

public class DrawerRoomEntryViewHolder extends RecyclerView.ViewHolder {

    private final TextView mRoomName;
    private final TextView mRoomAmount;
    private MainActivityPresenter mMainActivityPresenter;

    public DrawerRoomEntryViewHolder(View itemView, MainActivityPresenter mainActivityPresenter) {
        super(itemView);
        mRoomName = (TextView) itemView.findViewById(
                R.id.drawer_room_entry_name);
        mRoomAmount = (TextView) itemView.findViewById(
                R.id.drawer_room_entry_amount);
        mMainActivityPresenter = mainActivityPresenter;
    }

    public void update(RoomInfo roomEntry) {
        mRoomName.setText(roomEntry.getName());
        mRoomAmount.setText(WheelUtil.getStringFromPrice(roomEntry.getBalanceByUser()));
        itemView.setOnClickListener((view) -> {
            mMainActivityPresenter.loadRoom(roomEntry.getId());
            mMainActivityPresenter.closeDrawerIfNotLocked();
        });
    }

}
