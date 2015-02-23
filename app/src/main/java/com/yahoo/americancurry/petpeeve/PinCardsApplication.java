package com.yahoo.americancurry.petpeeve;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.SaveCallback;
import com.yahoo.americancurry.petpeeve.model.Pin;

/**
 * Created by nandaja on 2/20/15.
 */
public class PinCardsApplication extends Application {

    public static final String YOUR_APPLICATION_ID = "NKazbKlKRGU9tBWFi11GaTexVy6HqhHHoBSy8FFk";
    public static final String YOUR_CLIENT_KEY = "6bhxZmyp6nTMYTpCBRAJm8hn1RwaPEaKyLc5xLi8";

    @Override
    public void onCreate() {
        super.onCreate();

        // Add your initialization code here
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, YOUR_APPLICATION_ID, YOUR_CLIENT_KEY);

        ParseObject.registerSubclass(Pin.class);
        //Subscribe to PARSE push service
        ParsePush.subscribeInBackground("PinApplication", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
                } else {
                    Log.e("com.parse.push", "failed to subscribe for push", e);
                }
            }
        });
        Parse.setLogLevel(Parse.LOG_LEVEL_VERBOSE);
    }

}
