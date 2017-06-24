package org.wheel.expenses;

import org.wheel.expenses.Util.WheelUtil;
import org.wheel.expenses.data.RoomInfo;

import android.view.View;
import android.widget.TextView;

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
        roomAmount.setText(WheelUtil.getStringFromPrice(mRoomInfo.getBalanceByUser()));
    }

    public String getRoomId() {
        return mRoomInfo.getId();
    }

    public RoomInfo getRoomInfo() { return mRoomInfo; }
}
