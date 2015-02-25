package com.yahoo.americancurry.petpeeve.utils;

import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import java.text.MessageFormat;

/**
 * Created by netram on 2/21/15.
 */
public class GoogleMapsUtil {

    private static AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

    private static final String SEARCH_LOCATION_API_FORMAT = "http://maps.googleapis.com/maps/api/geocode/json?address={0}&sensor=true";

    private static final String REVERSE_GEOCODING_API_FORMAT = "https://maps.googleapis.com/maps/api/geocode/json?latlng={0}";

    public static void getLocationForAddress(String address, JsonHttpResponseHandler jsonHttpResponseHandler) {

        asyncHttpClient.get(MessageFormat.format(SEARCH_LOCATION_API_FORMAT, address), jsonHttpResponseHandler);
    }
    public static void getReverseGeocoding(Double lat, Double longitude, JsonHttpResponseHandler jsonHttpResponseHandler) {
        asyncHttpClient.get(MessageFormat.format(REVERSE_GEOCODING_API_FORMAT, lat+","+longitude), jsonHttpResponseHandler);
    }
}
