package org.wheel.expenses;

import android.animation.ValueAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.wheel.expenses.data.RoomDisplayUserInfo;
import org.wheel.expenses.util.WheelUtil;

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
                roomDisplayUserInfo.getName() + "\n(" +
                        WheelUtil.getStringFromPrice(roomDisplayUserInfo.getBalance()) + ")");
        ValueAnimator valueAnimatorMax = ValueAnimator.ofInt(mProgressBar.getMax(),
                roomDisplayUserInfo.getMax());
        valueAnimatorMax.setDuration(1000);
        valueAnimatorMax.addUpdateListener(
                v -> mProgressBar.setMax((int) v.getAnimatedValue()));
        valueAnimatorMax.start();
        ValueAnimator valueAnimatorProgress = ValueAnimator.ofInt(mProgressBar.getProgress(),
                roomDisplayUserInfo.getBalance());
        valueAnimatorProgress.setDuration(1000);
        valueAnimatorProgress.addUpdateListener(
                v -> mProgressBar.setProgress((int) v.getAnimatedValue()));
        valueAnimatorProgress.start();
    }
}
