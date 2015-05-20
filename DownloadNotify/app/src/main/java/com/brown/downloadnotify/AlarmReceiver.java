package com.brown.downloadnotify;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by Brown on 2015/5/19.
 */
public class AlarmReceiver extends BroadcastReceiver{

    private final static String TAG = "AlarmReceiver";

    public static String HOURS = "HOURS";
    public static String MINUTES = "MINUTES";
    public static String INTERVAL = "INTERVAL";
    public static String ENABLE = "ENABLE";
    public static String URL = "URL";
    public static String DIRECTORY_PATHNAME = "DIRECTORY_PATHNAME";

    private SharedPreferences prefs;
    AlarmManager alarmManager;
    PendingIntent pendingIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "I got broadcast intent!");
        long currentTime = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTime);
        prefs = context.getSharedPreferences("myAlarmPrefs", Context.MODE_PRIVATE);
        int hours = prefs.getInt(HOURS, calendar.get(Calendar.HOUR_OF_DAY));
        int minutes = prefs.getInt(MINUTES, calendar.get(Calendar.MINUTE));
        int interval = prefs.getInt(INTERVAL, 10000);
        boolean isChecked = prefs.getBoolean(ENABLE, false);
        Uri url = Uri.parse(prefs.getString(URL, ""));
        String imagePath = prefs.getString(DIRECTORY_PATHNAME, "");
        Log.d(TAG, "Get "+hours+"/"+minutes+" "+interval+" "+isChecked);
        Log.d(TAG, "URL "+url);
        Log.d(TAG, "Path "+imagePath);

        long timeStart = calendar.getTimeInMillis();
        Intent imageIntent = DownloadImagesService.makeIntent(context,
                url,
                imagePath);

        pendingIntent = PendingIntent.getService(context,
                0, imageIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if(isChecked) {
            Log.d(TAG, "Start Alarm!");
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                    timeStart,
                    interval,
                    pendingIntent);
        } else {
            Log.d(TAG, "Cancel Alarm!");
            alarmManager.cancel(pendingIntent);
        }

    }
}
