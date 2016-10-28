package org.angelmariages.positionalertv2;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public abstract class Utils {
    public static final String DESTINATION_SERVICE_NAME = "org.angelmariages.positionalertv2.DESTINATION_SERVICE";
    public static String geofenceDefaultName = "org.angelmariages.positionalertv2.GEOFENCE_DEFAULT";
    public static void showToast(String text, int duration, Context context) {
        Toast.makeText(context,text,duration).show();
    }

    public static void sendLog(String text) {
        Log.d("Biker", text);
    }
}
