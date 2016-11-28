package org.angelmariages.positionalertv2.destination;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import org.angelmariages.positionalertv2.LocationApiClient;
import org.angelmariages.positionalertv2.Utils;

import java.util.ArrayList;

public class DestinationManager implements ResultCallback<Status> {
    private final GoogleApiClient mGoogleApiClient;
    private final Context mContext;
    private final LocationApiClient mLocationApiClient;

    public DestinationManager(Activity activity) {
        this.mContext = activity.getApplicationContext();
        mLocationApiClient = new LocationApiClient();
        this.mGoogleApiClient = mLocationApiClient.getApiClient(activity);
        mLocationApiClient.connect();
    }

    public void disconnectApiClient() {
        mLocationApiClient.disconnect();
    }

    public Location getCurrentPosition() {
        if (mLocationApiClient.isConnected()) {
            return mLocationApiClient.getLocation();
        }
        return null;
    }

    public void addDestination(final Destination destination) {
        new CountDownTimer(5000, 500) {
            boolean done;
            @Override
            public void onTick(long l) {
                if (!done && mLocationApiClient.isConnected()) {
                    addGeofence(destination);
                    done = true;
                } else if(!done) {
                    Utils.sendLog("Waiting for api to connect");
                }
            }

            @Override
            public void onFinish() {

            }
        }.start();
    }

    private void addGeofence(Destination destination) {
        try {
            Geofence addedGeofence = new Geofence.Builder()
                    .setRequestId(destination.generateID())
                    .setCircularRegion(destination.getLatitude(),
                            destination.getLongitude(),
                            destination.getRadius())
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                    .setNotificationResponsiveness(1000)
                    .build();

            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    getGeofenceRequest(addedGeofence),
                    getGeofencePendingIntent()
            ).setResultCallback(this);
            Utils.sendLog("Adding geofence["+ destination.generateID() +"] from destination:");
            Utils.sendLog(destination.toString());
        } catch (SecurityException e) {
            Utils.sendLog("Error adding Geofence, SecurityException");
            e.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private PendingIntent getGeofencePendingIntent() {
        return PendingIntent.getService(mContext, 0,
                new Intent(mContext, DestinationHandle.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private GeofencingRequest getGeofenceRequest(Geofence addedGeofence) {
        return new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_EXIT)
                .addGeofence(addedGeofence)
                .build();
    }

    public void removeDestination(final String destinationString) {
        if(!mGoogleApiClient.isConnected()) {
            //TODO: keep trying to remove geofence
            Utils.sendLog("Error removing Geofence, GoogleApiClient not connected");
            return;
        }

        try {
            LocationServices.GeofencingApi.removeGeofences(
                    mGoogleApiClient,
                    new ArrayList<String>(){{
                        add(destinationString);
                    }}
            ).setResultCallback(this);
            Utils.sendLog("Removing geofence[" + destinationString + "]...");
        } catch(SecurityException e) {
            Utils.sendLog("Error removing Geofence, SecurityException");
            e.printStackTrace();
        }
    }

    @Override
    public void onResult(@NonNull Status status) {
        if(status.isSuccess()) {
            Utils.sendLog("Sucess!");
        } else {
            Utils.sendLog("Error: " + status.getStatusMessage());
        }
    }
}
