package org.wheel.expenses;

import org.wheel.expenses.data.Transaction;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class TransactionListItemViewHolder extends RecyclerView.ViewHolder {

    private final ImageView mUserT;
    private final TextView mPriceT;
    private final TextView mDescT;
    private final TextView mDateT;
    private RoomDisplayFragmentPresenter mRoomDisplayFragmentPresenter;


    public TransactionListItemViewHolder(View itemView,
                                         RoomDisplayFragmentPresenter roomDisplayFragmentPresenter) {
        super(itemView);
        mUserT = (ImageView) itemView.findViewById(R.id.userDisplay);
        mPriceT = (TextView) itemView.findViewById(R.id.priceDisplay);
        mDescT = (TextView) itemView.findViewById(R.id.descDisplay);
        mDateT = (TextView) itemView.findViewById(R.id.dateDisplay);
        mRoomDisplayFragmentPresenter = roomDisplayFragmentPresenter;
    }

    public void bindData(Transaction transaction) {
        mPriceT.setText(transaction.getAmount());
        mDescT.setText(transaction.getDescription());
        mDateT.setText(transaction.getDateString());
        itemView.setOnClickListener((view) -> mRoomDisplayFragmentPresenter.promptDeleteTransaction(
                transaction));
    }
}
