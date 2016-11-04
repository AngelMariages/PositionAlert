package org.angelmariages.positionalertv2;

import com.google.android.gms.maps.model.LatLng;

public class Destination  {
    private boolean removeOnReach;
    private LatLng dLatLng;
    private int dRadius;
    private String requestId;


    public Destination(LatLng dLatLng, int radius, String requestId, boolean removeOnReach) {
        this.dLatLng = dLatLng;
        this.dRadius = radius;
        this.removeOnReach = removeOnReach;
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

    public int getRadius() {
        return dRadius;
    }

    public String getRequestId() {
        return requestId;
    }

    @Override
    public String toString() {
        return "Destination{" +
                "removeOnReach=" + removeOnReach +
                ", dLatLng=" + dLatLng +
                ", dRadius=" + dRadius +
                ", requestId='" + requestId + '\'' +
                '}';
    }

    public boolean isRemoveOnReach() {
        return removeOnReach;
    }
}
