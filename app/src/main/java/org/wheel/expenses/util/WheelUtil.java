package org.wheel.expenses.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Locale;

public class WheelUtil {

    public static String getRoomShareUrl(String roomID) {
        final String roomShareUrl = "http://wheel-app.herokuapp.com/app/room/%s";
        return String.format(roomShareUrl, roomID);
    }
    public static String getStringFromPrice(int price) {
        return "$" + (price / 100) + "." + String.format(Locale.ENGLISH, "%02d", price % 100);
    }

    public static String hashPassword(String password) {
        return getSHA256Password(password);
    }

    private static String getSHA256Password(String password) {
        String generatedPassword = null;

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            md.update(password.getBytes(
                    "UTF-8")); // Change this to "UTF-16" if needed
            byte[] digest = md.digest();
            generatedPassword = String.format("%064x",
                                              new java.math.BigInteger(1, digest));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }

    public static int getPriceFromString(String price) {
        if (price.isEmpty()) return 0;
        return Integer.parseInt(price.replaceAll("[^\\d]", ""));
    }

    public static String getFriendlyDateString(Date date) {
        return FriendlyDateFormatter.createFriendlyDate(date);
    }


}
