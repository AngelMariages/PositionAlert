package org.angelmariages.positionalertv2;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

public abstract class U {
    public static final String DESTINATION_SERVICE_NAME = "org.angelmariages.positionalertv2.DESTINATION_SERVICE";
    public static final String SHARED_PREFERENCES_NAME = "org.angelmariages.positionalertv2.SHARED_PREFERENCES_NAME";
    public static final String RINGTONE_PREFERENCE = "org.angelmariages.positionalertv2.RINGTONE_PREFERENCE_";
    public static final int NULL_ID = -1;
    public static final String RINGTONE_TO_ACTIVITY = "org.angelmariages.positionalertv2.STOP_RINGTONE_ACTION";
    public static final String RESTART_SERVICE_INTENT = "org.angelmariages.positionalertv2.RESTART_SERVICE_INTENT";
    public static String geofenceDefaultName = "org.angelmariages.positionalertv2.GEOFENCE_DEFAULT";
    public static final int RINGTONE_SELECT_RESULT = 987;
    private static FirebaseDatabase mFirebaseDatabase = null;

    public static void showSToast(String text, Context context) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void showLToast(String text, Context context) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    public static float getZoomByRadius(int radius, double latitude) {
        double equator = 40075016.686;
        double pixelsWide = 256;

        return (float) Math.abs(log2(Math.abs((radius/pixelsWide * Math.pow(2,8)) / (equator * Math.cos(latitude)))));
    }

    private static double log2(double v) {
        return Math.log(v) / Math.log(2);
    }

    public static int getDPsFromPixels(int pixels, View view) {
        final float scale = view.getResources().getDisplayMetrics().density;
        return (int)(pixels * scale + 0.5f);
    }

    public static int getPixelsFromDPs(int dps, View view) {
        final float scale = view.getResources().getDisplayMetrics().density;
        return (int)((dps - 0.5f) / scale);
    }

    public static int getPixelsFromDPs(int dps, Activity activity) {
        final float scale = activity.getResources().getDisplayMetrics().density;
        U.sendLog("scale:"  + String.valueOf(scale));
        return (int)((dps - 0.5f) / scale);
    }

    public static boolean checkPositionPermissions(Context context) {
        //@TODO Check also for coarse location
        return (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED);
    }

    public static void askPositionPermissions(Activity activity) {
        ActivityCompat.requestPermissions(activity,
            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 0);
    }

    public static void sendLog(String text) {
        if(BuildConfig.DEBUG) {
            Log.d("Biker", text);
        }
    }

    public static FirebaseDatabase getFirebaseDatabase() {
        if(mFirebaseDatabase == null) {
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mFirebaseDatabase.setPersistenceEnabled(true);
        }
        return mFirebaseDatabase;
    }
}
