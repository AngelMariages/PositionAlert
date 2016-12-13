package org.angelmariages.positionalertv2;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

public class LocationApiClient {

    private GoogleApiClient mGoogleApiClient;
    private Context mContext;

    //@TODO Review this class
    public LocationApiClient() {
    }

    public GoogleApiClient getApiClient(Context context) {
        if (mContext == null) {
            mContext = context;
        }
        mGoogleApiClient = GApiClient.getInstance(mContext);
        return mGoogleApiClient;
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
}
