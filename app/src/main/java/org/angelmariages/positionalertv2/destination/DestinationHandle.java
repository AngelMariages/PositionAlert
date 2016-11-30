package org.angelmariages.positionalertv2.destination;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.GeofencingRequest;

import org.angelmariages.positionalertv2.MainActivity;
import org.angelmariages.positionalertv2.R;
import org.angelmariages.positionalertv2.U;

import java.util.Arrays;
import java.util.List;

public class DestinationHandle extends IntentService {

    private DestinationDBHelper dbHelper;

    public DestinationHandle() {
        super(U.DESTINATION_SERVICE_NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        dbHelper = new DestinationDBHelper(this);

        if(geofencingEvent.hasError()) {
            U.sendLog("GeofencingIntent has error: " + geofencingEvent.getErrorCode());
            return;
        }

        int geoFenceTransition = geofencingEvent.getGeofenceTransition();

        if(geoFenceTransition == GeofencingRequest.INITIAL_TRIGGER_ENTER) {

            List<Geofence> geofenceList = geofencingEvent.getTriggeringGeofences();

            for(int i = 0, geofenceListSize = geofenceList.size(); i < geofenceListSize; i++) {
                Geofence geofence = geofenceList.get(i);
                U.sendLog(geofence.getRequestId() + " : " + geoFenceTransition);

                parseGeofenceRequest(getGeofenceArguments(geofence.getRequestId()), i == 0);
            }
        }
    }

    private void parseGeofenceRequest(String[] geofenceArguments, boolean playSound) {
        if(geofenceArguments == null) {
            U.sendLog("Null geofence arguments!");
            return;
        }
        createGeofenceNotification(Integer.parseInt(geofenceArguments[0]), geofenceArguments[1]);
        if(Boolean.parseBoolean(geofenceArguments[2])) {
            dbHelper.deleteDestination(Integer.parseInt(geofenceArguments[0]));

            dbHelper.close();
        }

        String ringtoneSaved = getSharedPreferences(U.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
                .getString(U.RINGTONE_PREFERENCE, null);

        U.sendLog("SAVED:" + ringtoneSaved);

        if(playSound)
            startRingtoneActivity(ringtoneSaved);
    }

    private void createGeofenceNotification(int geofenceID, String geofenceName) {
        NotificationManager notificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Reached destination!")
                .setContentText(geofenceName)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build();
        notificationManager.notify(geofenceID, notification);
    }

    private static String[] getGeofenceArguments(String geofenceName) {
        String[] arguments = null;
        if(geofenceName != null && !geofenceName.isEmpty()) {
            String[] splited = geofenceName.split("\\|@\\|");
            System.out.println("Biker: " + splited.length);
            System.out.println("Biker: " + Arrays.toString(splited));
            if(splited.length == 3) {
                arguments = new String[3];
                arguments[0] = splited[0];
                arguments[1] = splited[1];
                arguments[2] = splited[2];
            }
        }
        return arguments;
    }

    private void startRingtoneActivity(String ringtone) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(U.RINGTONE_TO_ACTIVITY, ringtone);
        startActivity(intent);
    }
}
