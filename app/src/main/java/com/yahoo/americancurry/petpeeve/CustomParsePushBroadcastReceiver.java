package com.yahoo.americancurry.petpeeve;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by nandaja on 2/21/15.
 */
public class CustomParsePushBroadcastReceiver extends BroadcastReceiver {

    public static final String ACTION = "custom";
    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("Push received");
    Toast.makeText(context, "Push received", Toast.LENGTH_SHORT).show();
    }


}
