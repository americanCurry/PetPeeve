package com.yahoo.americancurry.petpeeve;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nandaja on 2/21/15.
 */
public class CustomParsePushBroadcastReceiver extends BroadcastReceiver {

    public static final String ACTION = "custom";
    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("Push received");
    Toast.makeText(context, "Push received", Toast.LENGTH_SHORT).show();
        Bundle extras = intent.getExtras();
        String jsonData = extras.getString("com.parse.Data");
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsonData);
            String heading = jsonObject.getString("heading");
            String dataString = jsonObject.getString("dataString");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
