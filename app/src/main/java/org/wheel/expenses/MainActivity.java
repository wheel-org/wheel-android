package org.wheel.expenses;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import org.wheel.expenses.data.RoomInfo;
import org.wheel.expenses.data.Transaction;
import org.wheel.expenses.data.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    static TransactionListAdapter mTransactionListAdapter;
    public static final String delimiter = "|";
    boolean modifying = false;

    TextView mDrawerNameDisp;
    TextView mDrawerUsernameDisp;

    ListView mDrawerList;
    Button sendBtn;
    EditText priceInput;
    EditText descInput;
    TextView userWelcome;
    HorizontalBarChart dispChart;
    android.support.v4.widget.SwipeRefreshLayout swipeRefreshLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawer;

    public void submitTransaction(View v) {
        // Pull data to make a transaction.
        double price = getPriceFromString(priceInput.getText().toString());
        String desc = descInput.getText().toString();
        if (desc.trim().isEmpty()) {
            WheelAPI.getInstance().ShowToast("Description cannot be empty!");
        } else if (price == 0) {
            WheelAPI.getInstance().ShowToast("Price cannot be 0!");
        } else {
            sendBtn.setEnabled(false);
            Map<String, String> params = new HashMap<>();
            params.put("person", String.valueOf(""));
            params.put("amount", String.format(Locale.CANADA, "%.2f", price));
            params.put("desc", desc);
            /*WheelAPI.getInstance().makeApiRequest(WheelAPI.ApiCall
            .AddTransaction, params, new WheelAPI.WheelAPIListener() {
                @Override
                public void onError(String ) {
                    WheelAPI.getInstance().ShowToast("Transaction logged!");
                    sendBtn.setEnabled(true);
                    priceInput.setText("");
                    descInput.setText("");
                    getData();
                }

                @Override
                public void onSuccess(Object response) {
                    sendBtn.setEnabled(true);
                    WheelAPI.getInstance().ShowToast(WheelAPI.CONNECTION_FAIL);
                }
            });*/
        }
    }

    public void getData() {
        /*WheelAPI.getInstance().makeApiRequest(WheelAPI.ApiCall.RoomRequest,
         null,
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
                        mTransactionListAdapter.updateData(transactions);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        swipeRefreshLayout.setRefreshing(false);
                        WheelAPI.getInstance().ShowToast(WheelAPI
                        .CONNECTION_FAIL);
                    }
                });*/
    }

    public void delete(String deleteID) {
        Map<String, String> params = new HashMap<>();
        params.put("id", deleteID);
       /* WheelAPI.getInstance().makeApiRequest(WheelAPI.ApiCall
       .DeleteTransaction, params,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        WheelAPI.getInstance().ShowToast("Deleted!");
                        getData();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        WheelAPI.getInstance().ShowToast(WheelAPI
                        .CONNECTION_FAIL);
                    }});*/
    }

    private double getPriceFromString(String charSeq) {
        String digits = "";
        for (int j = 0; j < charSeq.length(); j++) {
            if (Character.isDigit(charSeq.charAt(j))) {
                digits += charSeq.charAt(j);
            }
        }

        double price = 0.0;
        double power = 0.01;
        for (int j = digits.length() - 1; j >= 0; j--) {
            price += power * (digits.charAt(j) - '0');
            power *= 10;
        }
        return price;
    }

    private void updateActivity() {
        if (ApplicationStateManager.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        // Setup User Stuff
        DrawerRoomListAdapter drawerRoomListAdapter = new DrawerRoomListAdapter(
                this, R.layout.drawer_room_entry, createDrawerRoomList());
        mDrawerList.setAdapter(drawerRoomListAdapter);

        if (ApplicationStateManager.getInstance().getCurrentRoom() == null) {
            sendBtn.setVisibility(GONE);
            priceInput.setVisibility(GONE);
            descInput.setVisibility(GONE);
            swipeRefreshLayout.setVisibility(GONE);
            dispChart.setVisibility(GONE);
        } else {
            getData();
            dispChart.setVisibility(VISIBLE);
            sendBtn.setVisibility(VISIBLE);
            priceInput.setVisibility(VISIBLE);
            descInput.setVisibility(VISIBLE);
            swipeRefreshLayout.setVisibility(VISIBLE);
            List<BarEntry> entries = new ArrayList<>();
            Map<User, Double> users =
                    ApplicationStateManager.getInstance().getCurrentRoom()
                            .getUsers();
            int i = 0;
            final ArrayList<String> usernames = new ArrayList<>();
            for (Map.Entry<User, Double> entry : users.entrySet()) {
                User key = entry.getKey();
                usernames.add(key.getName() + " (" + key.getUsername() + ")");
                Double value = entry.getValue();
                entries.add(new BarEntry(i, value.floatValue()));
            }
            IAxisValueFormatter formatter = new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return usernames.get((int) value);
                }
            };

            BarDataSet set = new BarDataSet(entries, "Users");
            XAxis axis = dispChart.getXAxis();
            axis.setGranularity(1f);
            axis.setValueFormatter(formatter);
            dispChart.setData(new BarData(set));
        }
    }

    private ArrayList<DrawerRoomEntry> createDrawerRoomList() {
        ArrayList<RoomInfo> activeRooms =
                ApplicationStateManager.getInstance().getCurrentUser()
                        .getActiveRooms();
        ArrayList<DrawerRoomEntry> result = new ArrayList<>();
        for (int i = 0; i < activeRooms.size(); i++) {
            result.add(new DrawerRoomEntry(activeRooms.get(i)));
        }
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        sendBtn = (Button) findViewById(R.id.send_transaction_btn);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(
                R.id.swipe_container);
        dispChart = (HorizontalBarChart) findViewById(R.id.disp_chart);
        descInput = (EditText) findViewById(R.id.desc_input);
        priceInput = (EditText) findViewById(R.id.price_input);
        userWelcome = (TextView) findViewById(R.id.intro_text);
        mDrawerNameDisp = (TextView) findViewById(R.id.drawer_name);
        mDrawerUsernameDisp = (TextView) findViewById(R.id.drawer_username);
        mDrawerList = (ListView) findViewById(R.id.drawer_listview);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.setNavigationIcon(R.drawable.ic_open_drawer);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawer,
                R.string.drawer_open,
                R.string.drawer_close) {

            /** Called when a drawer has settled in a completely closed state
             * . */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        mDrawerUsernameDisp.setText(
                getString(R.string.drawer_logged_in,
                        ApplicationStateManager.getInstance().getCurrentUser
                                ().getUsername()));
        mDrawerNameDisp.setText(
                ApplicationStateManager.getInstance().getCurrentUser()
                        .getName());

        // Set the drawer toggle as the DrawerListener
        mDrawer.setDrawerListener(mDrawerToggle);

        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        updateActivity();
                    }
                });
        userWelcome.setText(
                ApplicationStateManager.getInstance().getCurrentUser()
                        .getName());

        priceInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                priceInput.setSelection(priceInput.getText().length());
            }
        });

        priceInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i,
                    int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1,
                    int i2) {
                if (modifying) {
                    modifying = false;
                    return;
                }

                // Extract only digits.
                double price = getPriceFromString(charSequence.toString());
                if (price == 0) {
                    modifying = true;
                    priceInput.setText("");
                } else {
                    modifying = true;
                    priceInput.setText(
                            "$" + String.format(Locale.CANADA, "%.2f", price));
                    priceInput.setSelection(priceInput.getText().length());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mTransactionListAdapter = new TransactionListAdapter(
                getApplicationContext());
        ListView listView = (ListView) findViewById(R.id.past_transactions);
        listView.setAdapter(mTransactionListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,
                    int i, long l) {
                final Transaction clicked =
                        (Transaction) adapterView.getAdapter().getItem(i);
                AlertDialog al = new AlertDialog.Builder(
                        MainActivity.this).create();
                al.setTitle("Delete Transaction?");
                al.setMessage("Are you sure you want to delete transaction for "
                        + clicked.getPrice() + " on " + clicked.getDate()
                        + ".");
                al.setIcon(getResources().getDrawable(R.mipmap.delete_alert));
                al.setButton(AlertDialog.BUTTON_POSITIVE, "Delete",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface,
                                    int i) {
                                //delete(clicked.getId());
                            }
                        });
                al.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface,
                                    int i) {
                                dialogInterface.dismiss();
                            }
                        });
                al.show();
            }
        });
        updateActivity();
    }
}
