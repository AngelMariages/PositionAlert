package org.angelmariages.positionalertv2;

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

import java.util.ArrayList;

public class DestinationManager implements ResultCallback<Status> {
    private ArrayList<Geofence> mGeofences = new ArrayList<>();
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
                    addGeofences(destination);
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

    private void addGeofences(Destination destination) {
        try {
            mGeofences.add(new Geofence.Builder()
                    .setRequestId(destination.getRequestId())
                    .setCircularRegion(destination.getLatitude(),
                            destination.getLongitude(),
                            destination.getdRadius())
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)
                    .setNotificationResponsiveness(500)
                    .build());

            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    getGeofenceRequest(),
                    getGeofencePendingIntent()
            ).setResultCallback(this);
            Utils.sendLog("Succes!");
        } catch (SecurityException e) {
            Utils.sendLog("Error adding Geofence, SecurityException");
            e.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private PendingIntent getGeofencePendingIntent() {
        return PendingIntent.getService(mContext, 0,
                //new Intent(mContext, DestinationHandle.class),
                new Intent(Utils.DESTINATION_SERVICE_NAME),
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private GeofencingRequest getGeofenceRequest() {
        return new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofences(mGeofences)
                .build();
    }

    public void removeDestination(final String destinationRequestId) {
        if(!mGoogleApiClient.isConnected()) {
            Utils.sendLog("Error removing Geofence, GoogleApiClient not connected");
            return;
        }

        try {
            LocationServices.GeofencingApi.removeGeofences(
                    mGoogleApiClient,
                    new ArrayList<String>(){{
                        add(destinationRequestId);
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
