package org.wheel.expenses;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

public class DrawerRoomListAdapter extends BaseAdapter {
    private ArrayList<DrawerRoomEntry> mDrawerRoomEntryList;
    private Context mContext;

    public DrawerRoomListAdapter(Context context) {
        mContext = context;
    }

    public void update(ArrayList<DrawerRoomEntry> newList) {
        mDrawerRoomEntryList = newList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mDrawerRoomEntryList.size();
    }

    @Override
    public Object getItem(int i) {
        return mDrawerRoomEntryList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return mDrawerRoomEntryList.get(i).getRoomId().hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) mContext).getLayoutInflater().inflate(
                    R.layout.drawer_room_entry, parent, false);
        }
        mDrawerRoomEntryList.get(position).bindData(convertView);
        return convertView;
    }
}
