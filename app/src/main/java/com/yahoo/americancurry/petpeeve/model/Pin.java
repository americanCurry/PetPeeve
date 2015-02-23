package com.yahoo.americancurry.petpeeve.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by nandaja on 2/21/15.
 */
@ParseClassName("Pin")
public class Pin extends ParseObject {

    private String text;

    private String recipientPhone;

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
}
