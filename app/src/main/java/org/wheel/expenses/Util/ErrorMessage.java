package org.wheel.expenses.Util;

public class ErrorMessage {
    private static String[] Messages = {
            "Your request could not be completed, please try again.",
            "Internal Error: API Request was missing data.",
            "Authentication failed! Check your username or password!",
            "Username is already taken! Please try another username.",
            "You do not have permission to access this room.",
            "Room password incorrect! Make sure you have the right room!",
            "The request room was not found!",
            "You are already in this room!"
    };

    public static String from(int errorCode) {
        return Messages[errorCode];
    }
}
