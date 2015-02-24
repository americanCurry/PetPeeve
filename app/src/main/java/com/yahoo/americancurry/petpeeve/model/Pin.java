package com.yahoo.americancurry.petpeeve.model;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.io.Serializable;

/**
 * Created by nandaja on 2/21/15.
 */
@ParseClassName("Pin")
public class Pin extends ParseObject implements Serializable{


    public String getText() {
        getString("text");
    }

    public void setText(String text) {
        put("text", text);
    }

    public String getRecipientPhone() {
        getString("recipientPhone");
    }

    public void setRecipientPhone(String recipientPhone) {
        put("recipientPhone", recipientPhone);
    }

    public LatLng getLocationCentre() {
        getString("locationCentre");
    }

    public void setLocationCentre(LatLng locationCentre) {
        put("locationCentre", locationCentre);
    }

    public int getLocationRadius() {
        getInt("locationRadius");
    }

    public void setLocationRadius(int locationRadius) {
        put("locationCentre", locationRadius);
    }
}
