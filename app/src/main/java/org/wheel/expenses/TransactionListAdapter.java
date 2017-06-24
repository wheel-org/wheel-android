package org.wheel.expenses;

import org.wheel.expenses.data.Transaction;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

public class TransactionListAdapter extends RecyclerView.Adapter<TransactionListItemViewHolder> {
    private ArrayList<Transaction> mTransactions;
    private RoomDisplayFragmentPresenter mRoomDisplayPresenter;

    public TransactionListAdapter(RoomDisplayFragmentPresenter roomDisplayPresenter) {
        mRoomDisplayPresenter = roomDisplayPresenter;
        this.mTransactions = new ArrayList<>();
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
        holder.bindData(mTransactions.get(position));
    }

    @Override
    public int getItemCount() {
        return mTransactions.size();
    }

    public void update(ArrayList<Transaction> values) {
        this.mTransactions.clear();
        this.mTransactions.addAll(values);
        this.notifyDataSetChanged();
    }

}
