package org.wheel.expenses;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements
        AdapterView.OnItemClickListener {
    static TransactionListAdapter mTransactionListAdapter;
    public static final String delimiter = "|";
    boolean modifying = false;

    @BindView(R.id.drawer_name)
    TextView mDrawerNameDisp;

    @BindView(R.id.drawer_username)
    TextView mDrawerUsernameDisp;

    @BindView(R.id.drawer_listview)
    ListView mDrawerList;

    @BindView(R.id.main_toolbar)
    Toolbar mToolbar;

    ActionBarDrawerToggle mDrawerToggle;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawer;

    @BindView(R.id.drawer_create_room_btn)
    LinearLayout mCreateRoomButton;

    @BindView(R.id.drawer_user_row)
    LinearLayout mUserRowItem;

    @BindView(R.id.main_loading_layout)
    SwipeRefreshLayout mLoadingRefreshLayout;

    @BindView(R.id.main_loading_layout_wrapper)
    FrameLayout mLoadingRefreshLayoutWrapper;

    @BindView(R.id.splash_loading_text)
    TextView mLoadingText;

    DrawerRoomListAdapter mDrawerListAdapter;

    private MainActivityPresenter mPresenter;

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
        mPresenter = new MainActivityPresenter(this, WheelClient.getInstance(),
                WheelAPI.getInstance());

        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_open_drawer);

        setProgressBarIndeterminateVisibility(false);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawer,
                R.string.drawer_open,
                R.string.drawer_close);
        mDrawer.setDrawerListener(mDrawerToggle);
        mDrawerList.setOnItemClickListener(this);
        mCreateRoomButton.setOnClickListener(view -> {
            mPresenter.onCreateRoomClicked();
            mDrawer.closeDrawers();
        });
        mUserRowItem.setOnClickListener(view -> {
            mPresenter.showDefaultUserFragment();
            mDrawer.closeDrawers();
        });
        mDrawerListAdapter = new DrawerRoomListAdapter(this);
        mDrawerList.setAdapter(mDrawerListAdapter);
        mLoadingRefreshLayout.setOnRefreshListener(() -> mPresenter.loadFailedRefreshTryAgain());
        mPresenter.onCreate();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position,
            long l) {
        DrawerRoomEntry item = (DrawerRoomEntry) adapterView.getItemAtPosition(
                position);
        mPresenter.onDrawerRoomItemClicked(item);
        mDrawer.closeDrawers();
    }

    public void updateDrawerList(ArrayList<DrawerRoomEntry> list) {
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
}
