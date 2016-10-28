package org.angelmariages.positionalertv2;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.GeofencingRequest;

import java.util.List;

public class DestinationHandle extends BroadcastReceiver {

    //public DestinationHandle() {
    //    super(Utils.DESTINATION_SERVICE_NAME);
    //}

    @Override
    public void onReceive(Context context, Intent intent) {
        intent.addCategory(GeofenceTrackService.LOCATION_SERVICE);

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if(geofencingEvent.hasError()) {
            Utils.sendLog("GeofencingIntent has error: " + geofencingEvent.getErrorCode());
            return;
        }

        int geoFenceTransition = geofencingEvent.getGeofenceTransition();

        Utils.sendLog("Geofence transition : " + geoFenceTransition);

        if(geoFenceTransition == GeofencingRequest.INITIAL_TRIGGER_ENTER || geoFenceTransition == GeofencingRequest.INITIAL_TRIGGER_EXIT) {

            List<Geofence> geofenceList = geofencingEvent.getTriggeringGeofences();

            for(Geofence geofence : geofenceList) {
                Utils.sendLog(geofence.getRequestId() + " : " + geoFenceTransition);
            }

        }
    }
}
