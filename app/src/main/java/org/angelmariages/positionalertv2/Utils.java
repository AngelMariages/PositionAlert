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

    public static float getZoomByRadius(int radius, Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);

        int heightPixels = displayMetrics.heightPixels;
        return getZoomByRadius(radius, heightPixels);
    }

    public static float getZoomByRadius(int radius, double latitude) {
        //double equator = 40075016.686;

        //return (float) Math.abs(log2((radius * Math.pow(2,8)) / (equator * Math.cos(latitude))));

        if(radius <= 100)
            return 17.0f;
        else if(radius <= 100)
            return 16.0f;
        else if(radius <= 200)
            return 15.0f;
        else if(radius <= 300)
            return 14.0f;
        else if(radius <= 400)
            return 14.0f;
        else if(radius <= 600)
            return 13.0f;
        else
            return 12.0f;
    }

    public static double log2(double v) {
        return Math.log(v) / Math.log(2);
    }

    public static int getDPsFromPixels(int pixels, View view) {
        final float scale = view.getResources().getDisplayMetrics().density;
        return (int)(pixels * scale + 0.5f);
    }

    public static void sendLog(String text) {
        Log.d("Biker", text);
    }
}
