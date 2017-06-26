package org.wheel.expenses;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.wheel.expenses.data.Transaction;

public class TransactionListAdapter extends
        AutoDiffAdapter<TransactionListItemViewHolder, Transaction> {
    private RoomDisplayFragmentPresenter mRoomDisplayPresenter;

    public TransactionListAdapter(RoomDisplayFragmentPresenter roomDisplayPresenter) {
        mRoomDisplayPresenter = roomDisplayPresenter;
    }

    @Override
    public TransactionListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TransactionListItemViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transaction_layout,
                        parent,
                        false),
                mRoomDisplayPresenter);
    }

    @Override
    public void onBindViewHolder(TransactionListItemViewHolder holder, int position) {
        holder.bindData(getItem(position));
    }

}
