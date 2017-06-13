package org.wheel.expenses.data;

import org.json.JSONObject;
import org.wheel.expenses.MainActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Felix on 5/3/2017.
 */

public class Transaction {
    private int User;
    private double Price;
    private String Description;
    private String CreatedDate;
    private String Id;

    static DateFormat df = new SimpleDateFormat("EEE, MMMM dd 'at' hh:mma", Locale.US);
    Transaction(JSONObject object) {

    }
    Transaction(String id, int user, double price, String description, String date) {
        Id = id;
        User = user;
        Price = price;
        Description = description;
        CreatedDate = date;

    }
    Transaction(String formatString) {
        String[] split = formatString.split("\\" + MainActivity.delimiter);
        Id = split[0];
        User = Integer.parseInt(split[1]);
        Price = Double.parseDouble(split[2]);
        try
        {
            long timeSince = Long.parseLong(split[3]);
            Date created = new Date(timeSince);
            TimeZone tf = TimeZone.getDefault();
            df.setTimeZone(tf);
            CreatedDate = df.format(created);
        }
        catch(NumberFormatException e)
        {
            CreatedDate = split[3];
        }

        Description = split[4];
    }
    public int getUser() {
        return User;
    }
    public String getPrice() {
        return "$" + String.format(Locale.CANADA, "%.2f", Price);
    }
    public String getDescription() {
        return Description;
    }
    public String getDate() {
        return CreatedDate;
    }
    public String getId() { return Id; }
}
