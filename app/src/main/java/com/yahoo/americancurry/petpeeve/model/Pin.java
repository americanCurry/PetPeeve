package com.yahoo.americancurry.petpeeve.model;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.io.File;
import java.io.Serializable;

/**
 * Created by nandaja on 2/21/15.
 */
@ParseClassName("Pin")
public class Pin extends ParseObject implements Serializable {


    public String getText() {
        return getString("text");
    }

    public void setText(String text) {
        put("text", text);
    }

    public String getRecipientName() {
        return getString("recipientName");
    }

    public String getRecipientPhone() {
        return getString("recipientPhone");
    }

    public void setRecipientPhone(String recipientPhone) {
        put("recipientPhone", recipientPhone);
    }

    public void setRecipientName(String recipientName) {
        put("recipientName", recipientName);
    }

    public void setLocationCentreLatitude(double locationCentreLatitude) {
        put("locationCentreLatitude", locationCentreLatitude);
    }

    public Double getLocationCentreLatitude() {
        return getDouble("locationCentreLatitude");
    }

    public void setLocationCentreLongitude(double locationCentreLongitude) {
        put("locationCentreLongitude", locationCentreLongitude);
    }

    public Double getLocationCentreLongitude() {
        return getDouble("locationCentreLongitude");
    }

    public int getLocationRadius() {
        return getInt("locationRadius");
    }

    public void setLocationRadius(int locationRadius) {
        put("locationRadius", locationRadius);
    }

    public ParseFile getParseFile() {
        return getParseFile("mediaFile");
    }

    public void setParseFile(ParseFile f) {
        put("mediaFile", f);
    }
}
