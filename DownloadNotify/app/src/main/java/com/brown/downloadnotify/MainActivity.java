package com.brown.downloadnotify;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;


public class MainActivity extends LifecycleLoggingActivity {

    private ImageOps mImageOps;
    public PendingIntent pendingIntent;
    public AlarmManager alarmManager;
    public long timeStart;
    public int timeInterval;

    private CheckBox checkBox;
    private TimePicker timePicker;
    public EditText editText;
    private Calendar calendar;
    private SharedPreferences prefs;

    private ComponentName receiver;
    PackageManager pm;

    public static String HOURS = "HOURS";
    public static String MINUTES = "MINUTES";
    public static String INTERVAL = "INTERVAL";
    public static String ENABLE = "ENABLE";
    public static String URL = "URL";
    public static String DIRECTORY_PATHNAME = "DIRECTORY_PATHNAME";

    public String mDirectoryPathname = null;
    public Uri mDefaultUrl = Uri.parse("http://www.dre.vanderbilt.edu/~schmidt/robot.png");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageOps = new ImageOps(this);

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        receiver = new ComponentName(this, BootReceiver.class);
        pm = this.getPackageManager();

        timePicker = (TimePicker) findViewById(R.id.timePicker);
        editText = (EditText) findViewById(R.id.editText);
        checkBox = (CheckBox) findViewById(R.id.checkBox);
        prefs = getSharedPreferences("myAlarmPrefs", MODE_PRIVATE);

        timeStart = System.currentTimeMillis();
        timeInterval = 5 * 1000; // 5 seconds
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStart);

        // Set Default Value
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        boolean isChecked = prefs.getBoolean(ENABLE, false);
        if(isChecked) {
            timePicker.setCurrentHour(prefs.getInt(HOURS, hours));
            timePicker.setCurrentMinute(prefs.getInt(MINUTES, minutes));
        } else {
            timePicker.setCurrentHour(hours);
            timePicker.setCurrentMinute(minutes);
        }
        editText.setText(String.valueOf(prefs.getInt(INTERVAL, timeInterval)));
        checkBox.setChecked(isChecked);

        checkBox.setOnCheckedChangeListener(checkedChangeListener);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Cancel alarm");
        alarmManager.cancel(pendingIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private CheckBox.OnCheckedChangeListener checkedChangeListener =
            new CheckBox.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    int hour = timePicker.getCurrentHour();
                    int minutes = timePicker.getCurrentMinute();
                    Log.d(TAG, hour + "/" + minutes);
                    calendar.set(Calendar.HOUR_OF_DAY, hour);
                    calendar.set(Calendar.MINUTE, minutes);
                    timeStart = calendar.getTimeInMillis();
                    Log.d(TAG, "Start with " + timeStart);

                    if(editText.getText().length()>0) {
                        timeInterval = Integer.parseInt(String.valueOf(editText.getText()));
                    }
                    Log.d(TAG, timeInterval+" milliseconds");

                    boolean isChecked = checkBox.isChecked();
                    mImageOps.sendAlarmBroadcast();

                    enableBootReceiver(isChecked);
                    saveData(hour, minutes, timeInterval, isChecked);
                }
            };

    protected void saveData(int hour, int minutes, int timeInterval, boolean isChecked) {
        Log.d(TAG, "saveData()");
        SharedPreferences.Editor editor = prefs.edit();
        Log.d(TAG, "Save "+hour+"/"+minutes+" "+timeInterval+" "+isChecked);
        editor.putBoolean(ENABLE, isChecked);
        editor.putInt(HOURS, hour);
        editor.putInt(MINUTES, minutes);
        editor.putInt(INTERVAL, timeInterval);
        editor.putString(URL, mDefaultUrl.toString());
        editor.putString(DIRECTORY_PATHNAME, mDirectoryPathname);
        editor.commit();
    }

    private void enableBootReceiver(boolean enable) {
        if(enable) {
            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
        } else {
            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);
        }
    }

    public static PendingIntent makeIntent(Context context) {
        // Notification
        Intent mNotificationIntent = new Intent(context, MainActivity.class);
        // back to activity without new intent
        mNotificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent mContentIntent = PendingIntent.getActivity(context, 0,
                mNotificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        return mContentIntent;
    }
}
