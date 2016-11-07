package org.angelmariages.positionalertv2;

import com.google.android.gms.maps.model.LatLng;

public class Destination  {
    private boolean removeOnReach;
    private LatLng dLatLng;
    private int dRadius;
    private String name;
    private boolean registered;


    public Destination(LatLng dLatLng, int radius, String name, boolean removeOnReach, boolean registered) {
        this.dLatLng = dLatLng;
        this.dRadius = radius;
        this.removeOnReach = removeOnReach;
        this.name = name;
        this.registered = registered;
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

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Destination{" +
                "removeOnReach=" + removeOnReach +
                ", dLatLng=" + dLatLng +
                ", dRadius=" + dRadius +
                ", name='" + name + '\'' +
                '}';
    }

    public boolean isRemoveOnReach() {
        return removeOnReach;
    }

    public boolean isRegistered() {
        return registered;
    }

    public void setRegistered(boolean registered) {
        this.registered = registered;
    }
}
