package org.wheel.expenses;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.wheel.expenses.data.Transaction;

import java.util.ArrayList;

/**
 * Created by felixg on 5/5/17.
 */

public class TransactionListAdapter extends BaseAdapter {
    private final Context context;
    private ArrayList<Transaction> transactions;
    public TransactionListAdapter(Context context) {
        super();
        this.context = context;
        this.transactions = new ArrayList<>();
    }
    public int getCount() {
        return transactions.size();
    }
    public Transaction getItem(int arg0) {
        return transactions.get(arg0);
    }
    public long getItemId(int position) {
        return position;
    }
    public void updateData(ArrayList<Transaction> values) {
        this.transactions.clear();
        this.transactions.addAll(values);
        Log.v("Values: ", String.valueOf(values.size()));
        this.notifyDataSetChanged();
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.transaction_layout, parent, false);
        }

        ImageView userT = (ImageView) convertView.findViewById(R.id.userDisplay);
        TextView priceT = (TextView) convertView.findViewById(R.id.priceDisplay);
        TextView descT = (TextView) convertView.findViewById(R.id.descDisplay);
        TextView dateT = (TextView) convertView.findViewById(R.id.dateDisplay);
        Transaction curr = getItem(position);
        userT.setImageDrawable(context.getResources().getDrawable(curr.getUser() == 0 ? R.mipmap.felix_icon : R.mipmap.michael_icon));
        priceT.setText(curr.getPrice());
        descT.setText(curr.getDescription());
        dateT.setText(curr.getDate());

        return convertView;
    }
}
