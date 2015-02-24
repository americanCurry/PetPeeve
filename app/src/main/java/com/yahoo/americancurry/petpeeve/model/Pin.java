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

    private String text;

    private String recipientPhone;

    private LatLng locationCentre;

    private int locationRadius;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getRecipientPhone() {
        return recipientPhone;
    }

    public void setRecipientPhone(String recipientPhone) {
        this.recipientPhone = recipientPhone;
    }

    public LatLng getLocationCentre() {
        return locationCentre;
    }

    public void setLocationCentre(LatLng locationCentre) {
        this.locationCentre = locationCentre;
    }

    public int getLocationRadius() {
        return locationRadius;
    }

    public void setLocationRadius(int locationRadius) {
        this.locationRadius = locationRadius;
    }
}
