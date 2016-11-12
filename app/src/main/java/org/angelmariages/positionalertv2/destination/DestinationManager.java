package org.angelmariages.positionalertv2.destination;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import org.angelmariages.positionalertv2.LocationApiClient;
import org.angelmariages.positionalertv2.Utils;

import java.util.ArrayList;

public class DestinationManager implements ResultCallback<Status> {
    private GoogleApiClient mGoogleApiClient;
    private Context mContext;
    private LocationApiClient mLocationApiClient;

    public DestinationManager(Context context) {
        this.mContext = context;
        mLocationApiClient = new LocationApiClient();
        this.mGoogleApiClient = mLocationApiClient.getApiClient(context);
        mLocationApiClient.connect();
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
                    .setRequestId(String.valueOf(destination.hashCode()))
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
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofence(addedGeofence)
                .build();
    }

    public void removeDestination(final int destinationHashcode) {
        if(!mGoogleApiClient.isConnected()) {
            //TODO: keep trying to remove geofence
            Utils.sendLog("Error removing Geofence, GoogleApiClient not connected");
            return;
        }

        try {
            LocationServices.GeofencingApi.removeGeofences(
                    mGoogleApiClient,
                    new ArrayList<String>(){{
                        add(String.valueOf(destinationHashcode));
                    }}
            ).setResultCallback(this);
        } catch(SecurityException e) {
            Utils.sendLog("Error removing Geofence, SecurityException");
            e.printStackTrace();
        }
    }

    @Override
    public void onResult(@NonNull Status status) {
        if(status.isSuccess()) {
            Utils.sendLog("Sucess:" + status.getStatusMessage());
        } else {
            Utils.sendLog("Error: " + status.getStatusMessage());
        }
    }
}
