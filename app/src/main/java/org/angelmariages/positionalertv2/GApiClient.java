package org.angelmariages.positionalertv2;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

public class GApiClient implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static GoogleApiClient mGoogleApiClient;

    public static GoogleApiClient getInstance(Context context) {
        if(mGoogleApiClient == null) {
            mGoogleApiClient = new GApiClient().getApiClient(context);
            mGoogleApiClient.connect();
        }
        return mGoogleApiClient;
    }

    public static void removeInstance() {
        mGoogleApiClient.disconnect();
        mGoogleApiClient = null;
    }

    private GApiClient() { }

    private GoogleApiClient getApiClient(Context context) {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder()
                .requestIdToken("282382340280-ul3pelbsur6heeoi89jaa942fsbabfjd.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();
        return mGoogleApiClient;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        U.sendLog("GApiClient connected!");
    }

    @Override
    public void onConnectionSuspended(int i) {
        U.sendLog("GApiClient connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        U.sendLog("GApiClient connection failed: " + connectionResult.getErrorMessage());
    }
}
