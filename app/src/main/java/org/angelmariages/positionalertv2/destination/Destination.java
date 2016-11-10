package org.angelmariages.positionalertv2.destination;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Destination implements Serializable {
    private boolean removeOnReach;
    private transient LatLng dLatLng;
    private int dRadius;
    private String name;
    private boolean registered;
    private boolean activated;


    public Destination(LatLng dLatLng, int radius, String name, boolean removeOnReach, boolean registered, boolean activated) {
        this.dLatLng = dLatLng;
        this.dRadius = radius;
        this.removeOnReach = removeOnReach;
        this.name = name;
        this.registered = registered;
        this.activated = activated;
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
                ", registered=" + registered +
                ", activated=" + activated +
                '}';
    }

    public boolean removeOnReach() {
        return removeOnReach;
    }

    public boolean isRegistered() {
        return registered;
    }

    public void setRegistered(boolean registered) {
        this.registered = registered;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeDouble(dLatLng.latitude);
        out.writeDouble(dLatLng.longitude);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        dLatLng = new LatLng(in.readDouble(), in.readDouble());
    }
}
