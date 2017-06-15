package org.wheel.expenses;

import org.wheel.expenses.data.Room;
import org.wheel.expenses.data.User;

public class ApplicationStateManager {
    private static ApplicationStateManager mInstance;
    private User mCurrentUser;
    private Room mCurrentRoom;

    public ApplicationStateManager() {
        mCurrentRoom = null;
        mCurrentUser = null;
    }
    public static void initialize() {
        mInstance = new ApplicationStateManager();
    }

    public static ApplicationStateManager getInstance() {
        return mInstance;
    }


    public User getCurrentUser() {
        return mCurrentUser;
    }

    public void setCurrentUser(User mCurrentUser) {
        this.mCurrentUser = mCurrentUser;
    }

    public Room getCurrentRoom() {
        return mCurrentRoom;
    }

    public void setCurrentRoom(Room mCurrentRoom) {
        this.mCurrentRoom = mCurrentRoom;
    }
}
