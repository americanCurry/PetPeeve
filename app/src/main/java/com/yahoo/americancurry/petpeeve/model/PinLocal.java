package com.yahoo.americancurry.petpeeve.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Table;

import java.io.Serializable;

/**
 * Created by nandaja on 2/24/15.
 */

@Table(name="Pin")
public class PinLocal extends Model implements Serializable {

    private String text;

    private String pinId;

    private String recipientPhone;

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

    public static PinLocal fromParsePinObject(Pin pin){

        PinLocal localPin = new PinLocal();
        localPin.setText(pin.getText());
        localPin.setRecipientPhone(pin.getRecipientPhone());

        return localPin;
    }
}
