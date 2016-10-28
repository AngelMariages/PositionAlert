package org.angelmariages.positionalertv2;

import com.google.android.gms.maps.model.LatLng;

public class Destination  {
    public boolean added;
    private LatLng dLatLng;
    private int dRadius;
    private String requestId;


    public Destination(LatLng dLatLng, int radius, String requestId) {
        this.dLatLng = dLatLng;
        this.dRadius = radius;
        this.requestId = requestId;
    }

    public LatLng getdLatLng() {
        return dLatLng;
    }

    public double getLatitude() {
        return dLatLng.latitude;
    }

    public double getLongitude() {
        return dLatLng.longitude;
    }

    public int getdRadius() {
        return dRadius;
    }

    public String getRequestId() {
        return requestId;
    }
}
