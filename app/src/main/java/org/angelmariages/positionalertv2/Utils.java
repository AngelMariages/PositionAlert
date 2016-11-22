package org.angelmariages.positionalertv2;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public abstract class Utils {
    public static final String DESTINATION_SERVICE_NAME = "org.angelmariages.positionalertv2.DESTINATION_SERVICE";
    public static final String SHARED_PREFERENCES_NAME = "org.angelmariages.positionalertv2.SHARED_PREFERENCES_NAME";
    public static final String RINGTONE_PREFERENCE = "org.angelmariages.positionalertv2.RINGTONE_PREFERENCE_";
    public static final int NULL_ID = -1;
    public static String geofenceDefaultName = "org.angelmariages.positionalertv2.GEOFENCE_DEFAULT";
    public static final int RINGTONE_SELECT_RESULT = 987;

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

    public static double log2(double v) {
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
        Utils.sendLog("scale:"  + String.valueOf(scale));
        return (int)((dps - 0.5f) / scale);
    }

    public static void sendLog(String text) {
        Log.d("Biker", text);
    }
}
