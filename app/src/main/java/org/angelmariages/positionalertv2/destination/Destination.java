package org.angelmariages.positionalertv2.destination;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Destination implements Serializable {
    private int databaseID;
    private boolean removeOnReach;
    private transient LatLng latLng;
    private int radius;
    private String name;
    private boolean registered;
    private boolean activated;


    public Destination(int databaseID, String name, LatLng latLng, int radius, boolean activated, boolean removeOnReach, boolean registered) {
        this.databaseID = databaseID;
        this.name = name;
        this.latLng = latLng;
        this.radius = radius;
        this.activated = activated;
        this.removeOnReach = removeOnReach;
        this.registered = registered;
    }

    @Override
    public String toString() {
        return "Destination{" +
                "databaseID=" + databaseID +
                ", removeOnReach=" + removeOnReach +
                ", latLng=" + latLng +
                ", radius=" + radius +
                ", name='" + name + '\'' +
                ", registered=" + registered +
                ", active=" + activated +
                '}';
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeDouble(latLng.latitude);
        out.writeDouble(latLng.longitude);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        latLng = new LatLng(in.readDouble(), in.readDouble());
    }

    public int getDatabaseID() {
        return databaseID;
    }

    public void setDatabaseID(int databaseID) {
        this.databaseID = databaseID;
    }

    public boolean removeOnReach() {
        return removeOnReach;
    }

    public void setRemoveOnReach(boolean removeOnReach) {
        this.removeOnReach = removeOnReach;
    }

    public LatLng getdLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng dLatLng) {
        this.latLng = dLatLng;
    }

    public double getLatitude() {
        return this.latLng.latitude;
    }

    public double getLongitude() {
        return this.latLng.longitude;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int dRadius) {
        this.radius = dRadius;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean registered() {
        return registered;
    }

    public void setRegistered(boolean registered) {
        this.registered = registered;
    }

    public boolean active() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }
}
