package com.yahoo.americancurry.petpeeve;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
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
public class PinCardsApplication extends com.activeandroid.app.Application {

    public static final String YOUR_APPLICATION_ID = "1lQMfY46mBGGVOipdkeQIC7qBaRwdf8gqVQPC0s5";
    public static final String YOUR_CLIENT_KEY = "lQjC9ARhg3QN963qysCGUNi6EqrSEIwQMt34cDsj";

    @Override
    public void onCreate() {
        super.onCreate();

        // Add your initialization code here
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, YOUR_APPLICATION_ID, YOUR_CLIENT_KEY);

        ParseObject.registerSubclass(Pin.class);
        //Subscribe to PARSE push service
        ParsePush.subscribeInBackground("", new SaveCallback() {
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
        getPhoneNumber();
    }

    public void  getPhoneNumber(){
        TelephonyManager tMgr = (TelephonyManager)(getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE));
        String mPhoneNumber = tMgr.getLine1Number();
        SharedPreferences pref =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor edit = pref.edit();
        edit.putString("phoneNumber", mPhoneNumber);

        edit.commit();


    }
}
