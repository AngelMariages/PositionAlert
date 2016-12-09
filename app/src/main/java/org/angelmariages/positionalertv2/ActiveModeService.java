package org.angelmariages.positionalertv2;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class ActiveModeService extends Service implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    @Override
    public void onCreate() {
        super.onCreate();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mGoogleApiClient.connect();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setFastestInterval(500);
        mLocationRequest.setInterval(1000);

        new CountDownTimer(5000, 500) {
            boolean done;
            @Override
            public void onTick(long l) {
                if (!done && mGoogleApiClient.isConnected()) {
                    try {
                        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, ActiveModeService.this);
                    } catch (SecurityException e) {
                        //@TODO Catch this
                        e.printStackTrace();
                    }
                } else if(!done) {
                    U.sendLog("Waiting for api to connect");
                }
            }

            @Override
            public void onFinish() {

            }
        }.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent broadcastIntent = new Intent(U.RESTART_SERVICE_INTENT);
        sendBroadcast(broadcastIntent);
        if(mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            U.sendLog("STOPPED LOCATION UPDATES");
            mGoogleApiClient.disconnect();
            U.sendLog("DISCONNECTED");
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        U.sendLog("CONNECTED");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        U.sendLog("LOCATION:" + location);
    }
}
