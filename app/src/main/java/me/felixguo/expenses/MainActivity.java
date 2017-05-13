package me.felixguo.expenses;

import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.http.AndroidHttpClient;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;


public class MainActivity extends AppCompatActivity {
    // 0 is felix 1 is michael
    static int user = 0;
    static double felixTotal = 0F;
    static double michaelTotal = 0F;
    static boolean modifying = false;
    static TransactionListAdapter transactionListAdapter;
    public static String delimiter = "|";

    Button sendBtn;
    EditText priceInput;
    EditText descInput;
    TextView felixPriceView;
    TextView michaelPriceView;
    ProgressBar felixProgress;
    ProgressBar michaelProgress;
    android.support.v4.widget.SwipeRefreshLayout swipeRefreshLayout;

    public void submitTransaction(View v) {

        // Pull data to make a transaction.
        double price = getPriceFromString(priceInput.getText().toString());
        String desc = descInput.getText().toString();
        if (desc.trim().isEmpty()) {
            WheelAPI.getInstance().ShowToast("Description cannot be empty!");
        }
        else if (price == 0) {
            WheelAPI.getInstance().ShowToast("Price cannot be 0!");
        }
        else {
            sendBtn.setEnabled(false);
            Map<String,String> params = new HashMap<>();
            params.put("person", String.valueOf(user));
            params.put("amount", String.format(Locale.CANADA, "%.2f", price));
            params.put("desc", desc);
            WheelAPI.getInstance().makeStringRequest(WheelAPI.ApiCall.AddTransaction, params,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            WheelAPI.getInstance().ShowToast("Transaction logged!");
                            sendBtn.setEnabled(true);
                            priceInput.setText("");
                            descInput.setText("");
                            getData();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            sendBtn.setEnabled(true);
                            WheelAPI.getInstance().ShowToast(WheelAPI.CONNECTION_FAIL);
                        }});
        }
    }
    public void getData() {
        WheelAPI.getInstance().makeStringRequest(WheelAPI.ApiCall.RoomRequest, null,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String data = response;
                        Log.v("Heroku Server Response", data);
                        Scanner s = new Scanner(data);
                        ArrayList<Transaction> transactions = new ArrayList<>();
                        String[] amounts = s.nextLine().split("\\" + delimiter);
                        felixTotal = Double.parseDouble(amounts[0]);
                        michaelTotal = Double.parseDouble(amounts[1]);
                        updateValues();
                        while (s.hasNextLine()) {
                            transactions.add(new Transaction(s.nextLine()));
                        }
                        transactionListAdapter.updateData(transactions);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        swipeRefreshLayout.setRefreshing(false);
                        WheelAPI.getInstance().ShowToast(WheelAPI.CONNECTION_FAIL);
                    }
                });
    }
    public void delete(String deleteID) {
        Map<String,String> params = new HashMap<>();
        params.put("id", deleteID);
        WheelAPI.getInstance().makeStringRequest(WheelAPI.ApiCall.DeleteTransaction, params,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        WheelAPI.getInstance().ShowToast("Deleted!");
                        getData();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        WheelAPI.getInstance().ShowToast(WheelAPI.CONNECTION_FAIL);
                    }});
    }
    private double getPriceFromString(String charSeq) {
        String digits = "";
        for (int j = 0; j < charSeq.length(); j++)
            if (Character.isDigit(charSeq.charAt(j))) digits += charSeq.charAt(j);

        double price = 0.0;
        double power = 0.01;
        for (int j = digits.length() - 1; j >= 0; j--) {
            price += power * (digits.charAt(j) - '0');
            power *= 10;
        }
        return price;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String android_id = Secure.getString(getApplicationContext().getContentResolver(),
                Secure.ANDROID_ID);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WheelAPI.initialize(getApplicationContext());
        sendBtn = (Button)findViewById(R.id.sendTransactionBtn);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_container);
        felixPriceView = (TextView)findViewById(R.id.felixLabel);
        michaelPriceView = (TextView)findViewById(R.id.michaelLabel);
        felixProgress = (ProgressBar)findViewById(R.id.leftProgress);
        michaelProgress = (ProgressBar)findViewById(R.id.rightProgress);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });
        TextView userWelcome = (TextView)findViewById(R.id.introText);
        if (android_id.equals("d6e1491dba4d62bf")) {
            user = 0;
            userWelcome.setText("Welcome Felix!");
        }
        else {
            user = 1;
            userWelcome.setText("Welcome Michael!");
        }
        descInput = (EditText)findViewById(R.id.descInput);
        priceInput = (EditText)findViewById(R.id.priceInput);
        priceInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                priceInput.setSelection(priceInput.getText().length());
            }
        });
        priceInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (modifying) {
                    modifying = false;
                    return;
                }

                // Extract only digits.
                double price = getPriceFromString(charSequence.toString());
                    if (price == 0) {
                        modifying = true;
                        priceInput.setText("");
                    }
                    else {
                        modifying = true;
                        priceInput.setText("$" + String.format(Locale.CANADA, "%.2f", price));
                        priceInput.setSelection(priceInput.getText().length());
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        transactionListAdapter = new TransactionListAdapter(getApplicationContext());
        ListView listView = (ListView)findViewById(R.id.pastTransactions);
        listView.setAdapter(transactionListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Transaction clicked = (Transaction)adapterView.getAdapter().getItem(i);
                AlertDialog al = new AlertDialog.Builder(MainActivity.this).create();
                al.setTitle("Delete Transaction?");
                al.setMessage("Are you sure you want to delete transaction for " + clicked.getPrice() + " on " + clicked.getDate() + ".");
                al.setIcon(getResources().getDrawable(R.mipmap.delete_alert));
                al.setButton(AlertDialog.BUTTON_POSITIVE, "Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        delete(clicked.getId());
                    }
                });
                al.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                al.show();
            }
        });
        getData();

    }
    public void updateValues() {
        felixPriceView.setText("Felix\n" + String.format(Locale.CANADA, "$%.2f", felixTotal));
        michaelPriceView.setText("Michael\n" + String.format(Locale.CANADA, "$%.2f", michaelTotal));
        // Calculate difference
        double difference = Math.abs(felixTotal - michaelTotal);
        int max = (int)Math.pow(10, Math.ceil(Math.log10(Math.max(felixTotal, michaelTotal))));
        felixProgress.setMax(max);
        michaelProgress.setMax(max);
        felixProgress.setProgress((int)felixTotal);
        michaelProgress.setProgress((int)michaelTotal);
        TextView leftDisp = (TextView)findViewById(R.id.leftDisp);
        TextView rightDisp = (TextView)findViewById(R.id.rightDisp);
        leftDisp.setText("$" + max);
        rightDisp.setText("$" + max);
        /*if (felixTotal > michaelTotal) {
            felixProgress.setProgress((int)difference);
            michaelProgress.setProgress(0);
        }
        else if (felixTotal < michaelTotal) {
            michaelProgress.setProgress((int)difference);
            felixProgress.setProgress(0);
        }
        else {
            michaelProgress.setProgress(0);
            felixProgress.setProgress(0);
        }*/
    }
}
