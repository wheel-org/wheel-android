package org.wheel.expenses;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.wheel.expenses.data.User;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserDisplayFragment extends Fragment implements MainActivityContentFragment {

    @BindView(R.id.user_display_username)
    TextView usernameDisplay;

    @BindView(R.id.user_display_name)
    TextView nameDisplay;

    private UserDisplayFragmentPresenter mPresenter;
    private User mUserToDisplay;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user_display, container, false);
        ButterKnife.bind(this, v);
        mPresenter = new UserDisplayFragmentPresenter(this, WheelClient.getInstance(), mUserToDisplay);
        mPresenter.onCreate();
        return v;
    }

    public void setUsernameDisplay(String username) {
        usernameDisplay.setText(username);
    }

    public void setNameDisplay(String name) {
        nameDisplay.setText(name);
    }

    public void setUserToDisplay(User user) {
        mUserToDisplay = user;
    }
}
