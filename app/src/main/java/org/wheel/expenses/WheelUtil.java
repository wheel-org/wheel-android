package org.wheel.expenses;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class WheelUtil {
    public static String getStringFromPrice(int price) {
        return "$" + (price / 100) + "." + (price % 100);
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
}
