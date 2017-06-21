package org.wheel.expenses;

public class WheelDeeplinkBundle {

    enum Actions {
        NONE, ROOM
    }

    private Actions mAction;
    private String mData;

    public WheelDeeplinkBundle(String action, String data) {
        mAction = Actions.NONE;
        if (action != null) {
            if (action.equals("room")) {
                mAction = Actions.ROOM;
            }
        }

        mData = data;
    }

    public Actions getAction() {
        return mAction;
    }

    public String getData() {
        return mData;
    }

}
