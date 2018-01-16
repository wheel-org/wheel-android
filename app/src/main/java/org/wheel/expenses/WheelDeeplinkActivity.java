package org.wheel.expenses;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

/* This activity has no content, it is used to delegate deeplinks */
public class WheelDeeplinkActivity extends Activity {
    public static final String KEY_ACTION = "link.key";
    public static final String KEY_DATA = "link.data";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Uri data = intent.getData();
        
        String dataString = data.getPath().replaceAll("/", "");
        dataString = dataString.replaceAll("wheel:", "");
        dataString = dataString.replaceAll("room", "");
        
        Intent mIntent = new Intent(this, SplashScreenActivity.class);
        mIntent.putExtra(KEY_ACTION, data.getHost());
        mIntent.putExtra(KEY_DATA, dataString);
        startActivity(mIntent);
        finish();
    }
}
