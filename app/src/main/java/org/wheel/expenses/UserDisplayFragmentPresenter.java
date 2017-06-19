package org.wheel.expenses;

import org.wheel.expenses.data.User;

public class UserDisplayFragmentPresenter implements ActivityLifecycleHandler {

    private UserDisplayFragment mFragment;
    private WheelClient mWheelClient;
    private User mUserToDisplay;

    public UserDisplayFragmentPresenter(UserDisplayFragment fragment,
            WheelClient wheelClient, User userToDisplay) {

        mFragment = fragment;
        mWheelClient = wheelClient;
        mUserToDisplay = userToDisplay;
    }

    @Override
    public void onCreate() {
        mFragment.setUsernameDisplay(mUserToDisplay.getUsername());
        mFragment.setNameDisplay(mUserToDisplay.getName());
    }
}
