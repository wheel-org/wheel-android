package org.wheel.expenses;

import org.wheel.expenses.data.UserInfo;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

public class RoomDisplayUserListAdapter
        extends RecyclerView.Adapter<RoomDisplayUserListItemViewHolder> {
    private ArrayList<UserInfo> mUserList;

    public RoomDisplayUserListAdapter() {
        mUserList = new ArrayList<>();
    }

    @Override
    public RoomDisplayUserListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RoomDisplayUserListItemViewHolder(LayoutInflater.from(parent.getContext())
                                                                   .inflate(R.layout.room_user_entry,
                                                                            parent,
                                                                            false));
    }

    @Override
    public void onBindViewHolder(RoomDisplayUserListItemViewHolder holder, int position) {
        holder.update(mUserList.get(position));
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    public void update(ArrayList<UserInfo> userList) {
        mUserList.clear();
        mUserList.addAll(userList);
        notifyDataSetChanged();
    }
}
