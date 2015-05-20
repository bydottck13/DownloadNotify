package com.brown.downloadnotify;

import android.content.Intent;
import android.os.Environment;

import java.lang.ref.WeakReference;

/**
 * Created by Brown on 2015/5/18.
 */
public class ImageOps {

    private final String TAG = getClass().getSimpleName();

    WeakReference<MainActivity> mActivity;

    public ImageOps(MainActivity activity) {

        mActivity = new WeakReference<>(activity);

        mActivity.get().mDirectoryPathname = Environment.getExternalStoragePublicDirectory
                (Environment.DIRECTORY_DCIM)
                + "/"+mActivity.get().getString(R.string.app_name)+"/";

    }

    public void sendAlarmBroadcast() {

        Utils.hideKeyboard(mActivity.get(),
                mActivity.get().editText.getWindowToken());

        Intent broadcastIntent = new Intent(mActivity.get(), AlarmReceiver.class);
        broadcastIntent.setAction("com.brown.SendBroadcast");
        mActivity.get().sendBroadcast(broadcastIntent);
    }

}
