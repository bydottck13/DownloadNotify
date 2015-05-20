package com.brown.downloadnotify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Brown on 2015/5/20.
 */
public class BootReceiver extends BroadcastReceiver{

    private final static String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Log.d(TAG, "Send Broadcast after reboot!");
            Intent bootIntent = new Intent(context, AlarmReceiver.class);
            bootIntent.setAction("com.brown.SendBroadcast");
            context.sendBroadcast(bootIntent);
        }

    }
}
