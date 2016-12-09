package org.angelmariages.positionalertv2;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

public class LocationApiClient implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private Context mContext;
    private boolean mConnected;

    //@TODO Review this class
    public LocationApiClient() {
    }

    public GoogleApiClient getApiClient(Context context) {
        if (mContext == null) {
            mContext = context;
        }
        if (mGoogleApiClient != null) {
            return mGoogleApiClient;
        } else {
            mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        return mGoogleApiClient;
    }

    public void connect() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    public void disconnect() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
            mConnected = false;
            U.sendLog("LocationApiClient: Api disconnected");
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mConnected = true;
        U.sendLog("GoogleApiClient connected!");
    }

    public Location getLocation() {
        try {
            return LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
        } catch (SecurityException e) {
            U.sendLog("LocationApiClient error: can't get permissions to ");
            return null;
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        U.sendLog("GoogleApiClient connection suspended");
        mConnected = false;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        U.sendLog("GoogleApiClient connection failed: " + connectionResult.getErrorMessage());
        mConnected = false;
    }

    public boolean isConnected() {
        return mConnected;
    }
}
