package org.wheel.expenses;

import static android.support.v4.widget.DrawerLayout.LOCK_MODE_LOCKED_OPEN;
import static android.support.v4.widget.DrawerLayout.LOCK_MODE_UNLOCKED;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.wheel.expenses.data.RoomInfo;
import org.wheel.expenses.util.RecyclerViewUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {

    @BindView(R.id.drawer_name)
    TextView mDrawerNameDisp;

    @BindView(R.id.drawer_username)
    TextView mDrawerUsernameDisp;

    @BindView(R.id.drawer_listview)
    RecyclerView mDrawerList;

    @BindView(R.id.main_toolbar)
    Toolbar mToolbar;

    ActionBarDrawerToggle mDrawerToggle;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawer;

    @BindView(R.id.drawer_create_room_btn)
    LinearLayout mCreateRoomButton;

    @BindView(R.id.drawer_join_room_btn)
    LinearLayout mJoinRoomButton;

    @BindView(R.id.drawer_log_out_btn)
    LinearLayout mLogoutBtn;

    @BindView(R.id.drawer_user_row)
    LinearLayout mUserRowItem;

    @BindView(R.id.main_loading_layout)
    SwipeRefreshLayout mLoadingRefreshLayout;

    @BindView(R.id.main_loading_layout_wrapper)
    FrameLayout mLoadingRefreshLayoutWrapper;

    @BindView(R.id.drawer_swipe_refresh)
    SwipeRefreshLayout mDrawerRefreshLayout;

    @BindView(R.id.splash_loading_text)
    TextView mLoadingText;

    @BindView(R.id.navigation_view)
    NavigationView mNavigationView;

    private DrawerRoomListAdapter mDrawerListAdapter;

    private MainActivityPresenter mPresenter;

    private boolean mIsDrawerLocked = false;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return true;
    }

    public void setDrawerText(String name, String username) {
        mDrawerUsernameDisp.setText(username);
        mDrawerNameDisp.setText(name);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mPresenter = new MainActivityPresenter(this,
                WheelClient.getInstance(),
                WheelAPI.getInstance(),
                StoredPreferencesManager.getInstance());

        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_open_drawer);
        showLargeDrawer();
        setProgressBarIndeterminateVisibility(false);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawer,
                R.string.drawer_open,
                R.string.drawer_close) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        mDrawer.setDrawerListener(mDrawerToggle);
        mCreateRoomButton.setOnClickListener(view -> {
            mPresenter.onCreateRoomClicked();
            closeDrawerIfNotLocked();
        });
        mJoinRoomButton.setOnClickListener(view -> {
            mPresenter.onJoinRoomClicked();
            closeDrawerIfNotLocked();
        });
        mUserRowItem.setOnClickListener(view -> {
            closeDrawerIfNotLocked();
        });
        mLogoutBtn.setOnClickListener(view -> {
            mPresenter.onLogoutClicked();
            closeDrawerIfNotLocked();
        });
        mDrawerListAdapter = new DrawerRoomListAdapter(mPresenter);
        mDrawerList.setLayoutManager(new LinearLayoutManager(this));
        mDrawerList.setAdapter(mDrawerListAdapter);
        RecyclerViewUtil.Setup(mDrawerList);
        mLoadingRefreshLayout.setOnRefreshListener(() -> mPresenter.loadFailedRefreshTryAgain());
        mDrawerRefreshLayout.setOnRefreshListener(() -> mPresenter.updateActivity());
        mPresenter.onCreate();
    }


    public void updateDrawerList(ArrayList<RoomInfo> list) {
        mDrawerListAdapter.update(list);
    }

    public void showLoading() {
        mLoadingRefreshLayoutWrapper.setVisibility(View.VISIBLE);
        mLoadingRefreshLayout.setRefreshing(true);
        mLoadingText.setText(R.string.main_loading_text);
    }

    public void hideLoading() {
        mLoadingRefreshLayoutWrapper.setVisibility(View.GONE);
        mLoadingRefreshLayout.setRefreshing(false);
    }

    public void errorLoading() {
        mLoadingText.setText(R.string.splash_swipe_to_try_again);
        mLoadingRefreshLayout.setRefreshing(false);
    }

    public void updateActivity() {
        mPresenter.updateActivity();
    }

    public void setDrawerRefreshing(boolean drawerRefreshing) {
        mDrawerRefreshLayout.setRefreshing(drawerRefreshing);
    }

    public void showLargeDrawer() {
        mIsDrawerLocked = true;
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        DrawerLayout.LayoutParams params =
                (DrawerLayout.LayoutParams) mNavigationView.getLayoutParams();
        params.width = metrics.widthPixels;
        mNavigationView.setLayoutParams(params);
        mDrawer.setDrawerLockMode(LOCK_MODE_LOCKED_OPEN);
    }

    public void showSmallDrawer() {
        mIsDrawerLocked = false;
        mNavigationView.setLayoutParams(new DrawerLayout.LayoutParams(
                DrawerLayout.LayoutParams.WRAP_CONTENT,
                DrawerLayout.LayoutParams.WRAP_CONTENT, Gravity.LEFT));
        mDrawer.setDrawerLockMode(LOCK_MODE_UNLOCKED);
    }

    public void closeDrawerIfNotLocked() {
        if (!mIsDrawerLocked) mDrawer.closeDrawers();
    }

    public void hideRoomFragment() {
        mPresenter.hideRoomFragment();
    }

    @Override
    public void onBackPressed() {
        if (mIsDrawerLocked) {
            super.onBackPressed();
        } else {
            showLargeDrawer();
        }
    }
}
