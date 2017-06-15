package org.wheel.expenses;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class DrawerRoomListAdapter extends ArrayAdapter<DrawerRoomEntry> {
    private ArrayList<DrawerRoomEntry> mDrawerRoomEntryList;
    private Context mContext;

    public DrawerRoomListAdapter(Context context, int resource,
            ArrayList<DrawerRoomEntry> drawerRoomEntryList) {
        super(context, resource, drawerRoomEntryList);
        mContext = context;
        mDrawerRoomEntryList = drawerRoomEntryList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) mContext).getLayoutInflater().inflate(
                    R.layout.drawer_room_entry, parent);
        }
        mDrawerRoomEntryList.get(position).bindData(convertView);
        return convertView;
    }
}
