package me.felixguo.expenses;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Felix on 5/3/2017.
 */

class Transaction {
    int User;
    double Price;
    String Description;
    String CreatedDate;
    String Id;

    static DateFormat df = new SimpleDateFormat("EEE, MMMM dd 'at' hh:mma", Locale.US);
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
    int getUser() {
        return User;
    }
    String getPrice() {
        return "$" + String.format(Locale.CANADA, "%.2f", Price);
    }
    String getDescription() {
        return Description;
    }
    String getDate() {
        return CreatedDate;
    }
    String getId() { return Id; }
}
