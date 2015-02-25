package com.yahoo.americancurry.petpeeve;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.yahoo.americancurry.petpeeve.model.Pin;
import com.yahoo.americancurry.petpeeve.model.PinLocal;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by nandaja on 2/21/15.
 */
public class CustomParsePushBroadcastReceiver extends BroadcastReceiver {

    public static final String ACTION = "custom";

    @Override
    public void onReceive(final Context context, Intent intent) {
        System.out.println("Push received");
        Toast.makeText(context, "Push received", Toast.LENGTH_SHORT).show();
        Bundle extras = intent.getExtras();
        String jsonData = extras.getString("com.parse.Data");
        System.out.println(jsonData);
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsonData);

            final String objectId = jsonObject.getString("objectId");
            /*Get object from parse using objectId  - examine recipient phone
            matches local phone number and then save to local SQLLite*/

            ParseQuery<ParseObject> query = ParseQuery.getQuery("Pin");
            query.whereEqualTo("objectId",objectId);
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        // row of Object Id
                        Pin pin = (Pin) objects.get(0);
                        //if(pin.getReciepient == localPhoneNumber)


                        PinLocal localPin = PinLocal.fromParsePinObject(pin, context);
                        localPin.setPinId(objectId);
                        System.out.println("### AMERICAN CURRY #### " + localPin.getPinId());
                        localPin.save();

                    } else {
                        // error
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
