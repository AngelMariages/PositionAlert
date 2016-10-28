package org.angelmariages.positionalertv2;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

public class MainService extends Service {
    private DestinationManager mDestinationManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Utils.sendLog("MainService created");
        mDestinationManager = new DestinationManager(this);

        mDestinationManager.addDestination(new Destination(
                new LatLng(37.4239595086973, -122.0816706866026),
                500,
                Utils.geofenceDefaultName
        ));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Utils.sendLog("MainService onStartCommand");
        return START_STICKY;
    }
}
