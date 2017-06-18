package org.wheel.expenses;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
        mPresenter = new MainActivityPresenter(this, WheelClient.getInstance(), WheelAPI.getInstance());
        mPresenter.onCreate();

        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_open_drawer);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawer,
                R.string.drawer_open,
                R.string.drawer_close);
        mDrawer.setDrawerListener(mDrawerToggle);

        mCreateRoomButton.setOnClickListener(view -> mPresenter.onCreateRoomClicked());

        mDrawerListAdapter = new DrawerRoomListAdapter(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position,
            long l) {
        DrawerRoomEntry item = (DrawerRoomEntry) adapterView.getItemAtPosition(
                position);
        mPresenter.onDrawerRoomItemClicked(item);
    }

    public void updateDrawerList(ArrayList<DrawerRoomEntry> list) {
        mDrawerListAdapter.update(list);
    }
}
