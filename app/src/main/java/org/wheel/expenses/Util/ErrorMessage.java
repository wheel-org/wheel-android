package org.wheel.expenses.Util;

public class ErrorMessage {
    public final static int NOT_YOUR_TRANSACTION_ERROR = 10;
    private static String[] Messages = {
            "Your request could not be completed, please try again.",
            "Internal Error: API Request was missing data.",
            "Authentication failed! Check your username or password!",
            "Username is already taken! Please try another username.",
            "You do not have permission to access this room.",
            "Room password incorrect! Make sure you have the right room!",
            "The requested room was not found!",
            "You are already in this room!",
            "The requested transaction was not found!",
            "You are not the admin of this room!",
            "You cannot delete transactions that are not yours!"
    };

    public static String from(int errorCode) {
        return Messages[errorCode];
    }
}
