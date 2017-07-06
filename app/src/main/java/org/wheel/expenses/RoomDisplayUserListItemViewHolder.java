package org.wheel.expenses;

import org.wheel.expenses.data.UserInfo;
import org.wheel.expenses.util.WheelUtil;

import android.animation.ValueAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class RoomDisplayUserListItemViewHolder extends RecyclerView.ViewHolder {

    private final TextView mTextDisplay;
    private final ProgressBar mProgressBar;
    private final ImageView mProfilePicture;

    public RoomDisplayUserListItemViewHolder(View itemView) {
        super(itemView);
        mTextDisplay = (TextView) itemView.findViewById(R.id.user_entry_text);
        mProgressBar = (ProgressBar) itemView.findViewById(R.id.user_entry_progressbar);
        mProfilePicture = (ImageView) itemView.findViewById(R.id.user_entry_profile_picture);
    }

    public void update(UserInfo roomDisplayUserInfo) {
        WheelUtil.setUserProfilePicture(mProfilePicture, roomDisplayUserInfo.getProfilePicture());
        mTextDisplay.setText(
                roomDisplayUserInfo.getName() + "\n(" +
                        WheelUtil.getStringFromPrice(roomDisplayUserInfo.getBalance()) + ")");
        ValueAnimator valueAnimatorMax = ValueAnimator.ofInt(mProgressBar.getMax(),
                roomDisplayUserInfo.getMaxBalance());
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
