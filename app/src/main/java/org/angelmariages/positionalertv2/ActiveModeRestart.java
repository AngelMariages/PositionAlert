package org.angelmariages.positionalertv2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ActiveModeRestart extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        U.sendLog("Restarting service");
        context.startService(new Intent(context, ActiveModeService.class));
    }
}
