package org.wheel.expenses;

import org.wheel.expenses.Util.WheelUtil;
import org.wheel.expenses.data.RoomDisplayUserInfo;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class RoomDisplayUserListItemViewHolder extends RecyclerView.ViewHolder {

    private final TextView mTextDisplay;
    private final ProgressBar mProgressBar;

    public RoomDisplayUserListItemViewHolder(View itemView) {
        super(itemView);
        mTextDisplay = (TextView) itemView.findViewById(R.id.user_entry_text);
        mProgressBar = (ProgressBar) itemView.findViewById(R.id.user_entry_progressbar);
    }

    public void update(RoomDisplayUserInfo roomDisplayUserInfo) {
        mTextDisplay.setText(
                roomDisplayUserInfo.getName() + " (" +
                WheelUtil.getStringFromPrice(roomDisplayUserInfo.getBalance()) + ")");
        mProgressBar.setMax(roomDisplayUserInfo.getMax());
        mProgressBar.setProgress(roomDisplayUserInfo.getBalance());
    }

}
