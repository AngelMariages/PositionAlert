package org.angelmariages.positionalertv2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ActiveModeRestart extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        U.sendLog("Restarting service");
        boolean isActive = context.getSharedPreferences(U.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
                .getBoolean(U.ACTIVE_MODE_PREFERENCE, false);
        if(isActive) context.startService(new Intent(context, ActiveModeService.class));
        else {
            U.sendLog("Not restarting the service because it should be stopped");
        }
    }
}
