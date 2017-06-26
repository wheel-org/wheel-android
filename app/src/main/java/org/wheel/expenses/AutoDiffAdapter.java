package org.wheel.expenses;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class AutoDiffAdapter<VH extends RecyclerView.ViewHolder, T extends AutoDiffItem>
        extends
        RecyclerView.Adapter<VH> {
    private ArrayList<T> mDataSet;

    public AutoDiffAdapter() {
        mDataSet = new ArrayList<>();
    }

    public T getItem(int position) {
        return mDataSet.get(position);
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public void update(ArrayList<T> newDataSet) {
        HashMap<String, T> newDataHash = new HashMap<>();
        for (int i = 0; i < newDataSet.size(); i++) {
            newDataHash.put(newDataSet.get(i).getId(), newDataSet.get(i));
        }
        for (int i = mDataSet.size() - 1; i >= 0; i--) {
            if (!newDataHash.containsKey(mDataSet.get(i).getId())) {
                // Not part of new
                mDataSet.remove(i);
                notifyItemRemoved(i);
            }
        }
        addNew(newDataSet);
    }

    private void addNew(ArrayList<T> newDataSet) {
        // All not matching removed.
        int origIndex = 0;
        int newIndex = 0;
        for (; newIndex < newDataSet.size(); newIndex++) {
            if (origIndex >= mDataSet.size()) {
                break;
            }
            if (newDataSet.get(newIndex).getId().equals(mDataSet.get(origIndex).getId())) {
                origIndex++;
            } else {
                mDataSet.add(origIndex, newDataSet.get(newIndex));
                notifyItemInserted(origIndex++);
            }
        }
        for (; newIndex < newDataSet.size(); newIndex++) {
            mDataSet.add(newDataSet.get(newIndex));
            notifyItemInserted(mDataSet.size());
        }
    }
}
