package org.wheel.expenses;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.wheel.expenses.data.RoomInfo;

public class DrawerRoomListAdapter extends
        AutoDiffAdapter<DrawerRoomEntryViewHolder, RoomInfo> {

    private MainActivityPresenter mMainFragmentPresenter;

    public DrawerRoomListAdapter(MainActivityPresenter mainFragmentPresenter) {
        mMainFragmentPresenter = mainFragmentPresenter;
    }

    @Override
    public DrawerRoomEntryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DrawerRoomEntryViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.drawer_room_entry,
                        parent,
                        false), mMainFragmentPresenter);
    }

    @Override
    public void onBindViewHolder(DrawerRoomEntryViewHolder holder, int position) {
        holder.update(getItem(position));
    }
}
