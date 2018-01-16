package org.wheel.expenses;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.wheel.expenses.data.RoomInfo;
import org.wheel.expenses.util.RecyclerViewUtil;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.support.v4.widget.DrawerLayout.LOCK_MODE_LOCKED_OPEN;
import static android.support.v4.widget.DrawerLayout.LOCK_MODE_UNLOCKED;
import static org.wheel.expenses.util.WheelUtil.setUserProfilePicture;


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

    @BindView(R.id.drawer_profile_image)
    ImageView mProfilePicture;

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

    private final static int RESULT_SELECT_IMAGE = 100;

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
            openChangeProfilePictureDialog();
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

    private void openChangeProfilePictureDialog() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                                                    "Select Picture"), RESULT_SELECT_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_SELECT_IMAGE) {
            if (resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
                Uri selectedImageUri = data.getData();
                CropImage.activity(selectedImageUri)
                         .setGuidelines(CropImageView.Guidelines.ON)
                         .setInitialCropWindowPaddingRatio(0)
                         .setFixAspectRatio(true)
                         .setAspectRatio(1, 1)
                         .setRequestedSize(256, 256, CropImageView.RequestSizeOptions.RESIZE_INSIDE)
                         .start(this);
            }
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            try {
                if (resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri();
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),
                                                                      resultUri);
                    mPresenter.updateUserImage(bitmap);
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    throw result.getError();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
        if (mDrawer.getDrawerLockMode(Gravity.LEFT) != LOCK_MODE_UNLOCKED) {
            mIsDrawerLocked = false;
            mNavigationView.setLayoutParams(new DrawerLayout.LayoutParams(
                    DrawerLayout.LayoutParams.WRAP_CONTENT,
                    DrawerLayout.LayoutParams.WRAP_CONTENT, Gravity.LEFT));
            mDrawer.setDrawerLockMode(LOCK_MODE_UNLOCKED);
        }
    }

    public void closeDrawerIfNotLocked() {
        if (!mIsDrawerLocked) {
            mDrawer.closeDrawers();
        }
    }

    public void hideRoomFragment() {
        mPresenter.hideRoomFragment();
    }

    @Override
    public void onBackPressed() {
        if (mIsDrawerLocked) {
            super.onBackPressed();
        }
        else if (mDrawer.isDrawerOpen(Gravity.LEFT)) {
            closeDrawerIfNotLocked();
        }
        else {
            showLargeDrawer();
        }
    }

    public void setDrawerPicture(String drawerPicture) {
        setUserProfilePicture(mProfilePicture, drawerPicture);
    }
}
