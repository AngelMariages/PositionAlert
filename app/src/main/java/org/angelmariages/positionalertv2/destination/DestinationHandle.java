package org.angelmariages.positionalertv2.destination;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.GeofencingRequest;

import org.angelmariages.positionalertv2.R;
import org.angelmariages.positionalertv2.Utils;

import java.io.IOException;
import java.util.List;

public class DestinationHandle extends IntentService {

    public DestinationHandle() {
        super(Utils.DESTINATION_SERVICE_NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if(geofencingEvent.hasError()) {
            Utils.sendLog("GeofencingIntent has error: " + geofencingEvent.getErrorCode());
            return;
        }

        int geoFenceTransition = geofencingEvent.getGeofenceTransition();

        Utils.sendLog("Geofence transition : " + geoFenceTransition);

        if(geoFenceTransition == GeofencingRequest.INITIAL_TRIGGER_ENTER) {

            List<Geofence> geofenceList = geofencingEvent.getTriggeringGeofences();

            for(Geofence geofence : geofenceList) {
                Utils.showLToast("You've reached your destination!", this);
                Utils.sendLog(geofence.getRequestId() + " : " + geoFenceTransition);
            }

            String ringtoneSaved = getSharedPreferences(Utils.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
                    .getString(Utils.RINGTONE_PREFERENCE, null);
            if(ringtoneSaved == null) {
                ringtoneSaved = RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI;
            }

            NotificationManager notificationManager =
                    (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

            Notification notification = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Reached destination!")
                    .setContentText("Geofence!")
                    .setStyle(new NotificationCompat.BigTextStyle().bigText("LAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .build();
            notificationManager.notify(0, notification);

            /*try {
                MediaPlayer mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(this, Uri.parse(ringtoneSaved));
                mediaPlayer.prepare();
                mediaPlayer.setLooping(false);
                mediaPlayer.start();
                mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                    @Override
                    public void onSeekComplete(MediaPlayer mediaPlayer) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                    }
                });
            } catch(IOException e) {
                e.printStackTrace();
            }*/

        }
    }
}
