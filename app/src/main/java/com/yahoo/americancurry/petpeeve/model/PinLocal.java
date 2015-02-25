package com.yahoo.americancurry.petpeeve.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import com.activeandroid.Model;
import com.activeandroid.annotation.Table;
import com.parse.GetDataCallback;
import com.parse.ParseException;

import java.io.Serializable;

/**
 * Created by nandaja on 2/24/15.
 */

@Table(name="Pin")
public class PinLocal extends Model implements Serializable {

    private String text;

    private String pinId;

    private String recipientPhone;

    private String recipientName;

    private int locationRadius;

    private Bitmap mediaBitmap;

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

    public Bitmap getMediaBitmap() {
        return mediaBitmap;
    }

    public void setMediaBitmap(Bitmap mediaBitmap) {
        this.mediaBitmap = mediaBitmap;
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
        if(pin.getParseFile()!=null) {
            pin.getParseFile().getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, ParseException e) {
                    Toast.makeText(context,"Image fetched from Parse ", Toast.LENGTH_SHORT).show();
                    localPin.setMediaBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                }
            });
        }

        return localPin;
    }
}
