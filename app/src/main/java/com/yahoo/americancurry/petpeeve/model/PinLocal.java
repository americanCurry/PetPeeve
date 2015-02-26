package com.yahoo.americancurry.petpeeve.model;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.parse.ParseException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.UUID;


/**
 * Created by nandaja on 2/24/15.
 */

@Table(name="Pin")
public class PinLocal extends Model implements Serializable {

    @Column(name = "text")
    private String text;

    @Column(name = "pinId")
    private String pinId;

    @Column(name = "recipientPhone")
    private String recipientPhone;

    @Column(name = "recipientName")
    private String recipientName;

    @Column(name = "locationRadius")
    private int locationRadius;

    @Column(name = "mediaURL")
    private String mediaURL;

    @Column(name = "locationCentreLatitude")
    private Double locationCentreLatitude;

    @Column(name = "locationCentreLongitude")
    private Double locationCentreLongitude;

    @Column(name="notified")
    private boolean isNotified;

    public boolean isNotified() {
        return isNotified;
    }

    public void setNotified(boolean isNotified) {
        this.isNotified = isNotified;
    }

    public Double getLocationCentreLatitude() {
        return locationCentreLatitude;
    }

    public void setLocationCentreLatitude(Double locationCentreLatitude) {
        this.locationCentreLatitude = locationCentreLatitude;
    }

    public Double getLocationCentreLongitude() {
        return locationCentreLongitude;
    }

    public void setLocationCentreLongitude(Double locationCentreLongitude) {
        this.locationCentreLongitude = locationCentreLongitude;
    }

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

    public int getLocationRadius() {
        return locationRadius;
    }

    public void setLocationRadius(int locationRadius) {
        this.locationRadius = locationRadius;
    }

    public String getPinId() {
        return pinId;
    }

    public void setPinId(String pinId) {
        this.pinId = pinId;
    }

    public String getMediaURL() {
        return mediaURL;
    }

    public void setMediaURL(String mediaURL) {
        Log.i("DEBUG", "Gott media Url " + mediaURL);
        this.mediaURL = mediaURL;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public static PinLocal fromParsePinObject(Pin pin, final Context context){

        final PinLocal localPin = new PinLocal();
        localPin.setText(pin.getText());
        localPin.setRecipientPhone(pin.getRecipientPhone());
        localPin.setRecipientName(pin.getRecipientName());
        localPin.setLocationRadius(pin.getLocationRadius());
        localPin.setLocationCentreLatitude(pin.getLocationCentreLatitude());
        localPin.setLocationCentreLongitude(pin.getLocationCentreLongitude());


        if(pin.getParseFile()!=null) {
            try {
                byte[] bytes = pin.getParseFile().getData();
                String imageURL = saveImage(bytes);
                localPin.setMediaURL(imageURL);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return localPin;
    }

    private static String saveImage(byte[] jpeg) {
        File photo=new File(Environment.getExternalStorageDirectory() + "/Pictures", UUID.randomUUID() + "_photo.jpg");

        if (photo.exists()) {
            photo.delete();
        }

        try {
            FileOutputStream fos=new FileOutputStream(photo.getPath());
            fos.write(jpeg);
            fos.close();
        }
        catch (java.io.IOException e) {
            Log.e("PictureDemo", "Exception in photoCallback", e);
        }
        return photo.getPath();
    }
}
