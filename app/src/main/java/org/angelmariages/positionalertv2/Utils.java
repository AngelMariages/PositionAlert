package org.angelmariages.positionalertv2;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public abstract class Utils {
    public static final String DESTINATION_SERVICE_NAME = "org.angelmariages.positionalertv2.DESTINATION_SERVICE";
    public static final String SHARED_PREFERENCES_NAME = "org.angelmariages.positionalertv2.SHARED_PREFERENCES_NAME";
    public static final String RINGTONE_PREFERENCE = "org.angelmariages.positionalertv2.RINGTONE_PREFERENCE_";
    public static String geofenceDefaultName = "org.angelmariages.positionalertv2.GEOFENCE_DEFAULT";
    public static int RINGTONE_SELECT_RESULT = 987;

    public static void showSToast(String text, Context context) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void showLToast(String text, Context context) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    public static void sendLog(String text) {
        Log.d("Biker", text);
    }
}
