package org.wheel.expenses;

import android.view.View;
import android.widget.TextView;

import org.wheel.expenses.data.RoomInfo;

class DrawerRoomEntry {
    private RoomInfo mRoomInfo;

    public DrawerRoomEntry(RoomInfo roomInfo) {
        mRoomInfo = roomInfo;
    }

    public void bindData(View v) {
        TextView roomName = (TextView) v.findViewById(
                R.id.drawer_room_entry_name);
        TextView roomAmount = (TextView) v.findViewById(
                R.id.drawer_room_entry_amount);
        roomName.setText(mRoomInfo.getName());
        roomAmount.setText(v.getContext().getString(R.string.price_display,
                mRoomInfo.getBalanceByUser()));
    }
}
