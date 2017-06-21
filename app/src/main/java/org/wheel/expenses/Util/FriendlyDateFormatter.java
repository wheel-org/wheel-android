package org.wheel.expenses.Util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FriendlyDateFormatter {
    private static final String JUST_NOW = "Just Now";
    private static final String MINUTE = "%d minute ago";
    private static final String MINUTES = "%d minutes ago";
    private static final String HOUR = "1 hour ago";
    private static final String HOURS = "%d hours ago";
    private static final String YESTERDAY = "Yesterday at %s";

    public static String createFriendlyDate(Date date) {
        DateFormat thisWeekFormat = new SimpleDateFormat("EEE 'at' hh:mma", Locale.US);
        DateFormat lastWeekFormat = new SimpleDateFormat("'Last' EEE 'at' hh:mma", Locale.US);
        DateFormat fullDateFormat = new SimpleDateFormat("EEE, MMMM dd 'at' hh:mma", Locale.US);
        DateFormat timeFormat = new SimpleDateFormat("hh:mma", Locale.US);

        Date now = new Date();
        long diff = now.getTime() - date.getTime();
        diff /= 1000;
        if (diff < 60) {
            return JUST_NOW;
        } else if (diff < 3600) {
            int output = Math.round(diff / 60);
            return String.format(output == 1 ? MINUTE : MINUTES, output);
        } else if (diff < 3600 * 24) {
            int output = Math.round(diff / 3600);
            return String.format(output == 1 ? HOUR : HOURS, output);
        } else if (diff < 3600 * 24 * 2) {
            return String.format(YESTERDAY, timeFormat.format(date));
        } else if (diff < 3600 * 24 * 7) {
            return thisWeekFormat.format(date);
        } else if (diff < 3600 * 24 * 7 * 2) {
            return lastWeekFormat.format(date);
        } else {
            return fullDateFormat.format(date);
        }
    }
}
