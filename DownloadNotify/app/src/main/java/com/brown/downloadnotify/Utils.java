package com.brown.downloadnotify;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by Brown on 2015/5/18.
 */
public class Utils {

    private final static String TAG = "Utils";
    private static String image_fileName = "downImage.png";

    public static Uri downloadImage(Context context,
                                    Uri url,
                                    String directoryPathname) {
        if (!isExternalStorageWritable()) {
            Log.d(TAG,
                    "external storage is not writable");
            return null;
        }

        // Download the contents at the URL, which should
        // reference an image.
        try {
            InputStream inputStream =
                    (InputStream) new URL(url.toString()).getContent();
            // Create an output file and save the image into it.
            return Utils.createDirectoryAndSaveFile
                    (context,
                            // Decode the InputStream into a Bitmap image.
                            BitmapFactory.decodeStream(inputStream),
                            image_fileName,
                            directoryPathname);
        } catch (Exception e) {
            Log.e(TAG,
                    "Exception while downloading -- returning null."
                            + e.toString());
            return null;
        }
    }

    private static Uri createDirectoryAndSaveFile(Context context,
                                                  Bitmap imageToSave,
                                                  String fileName,
                                                  String directoryPathname) {
        // Bail out of we get an invalid bitmap.
        if (imageToSave == null)
            return null;

        // Try to open a directory.
        File directory =
                new File(directoryPathname);

        // If the directory doesn't exist already then create it.
        if (!directory.exists()) {
            // File newDirectory =
            // new File(directory.getAbsolutePath());
            directory.mkdirs();
        }

        File file = new File(directory, fileName);
        // Delete the file if it already exists.
        if (file.exists())
            file.delete();

        // Save the image to the output file.
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            imageToSave.compress(Bitmap.CompressFormat.JPEG,
                    100,
                    outputStream);outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String absolutePathToImage = file.getAbsolutePath();

        Log.d(TAG,
                "absolute path to image file is "
                        + absolutePathToImage);

        return Uri.parse(absolutePathToImage);
    }

    private static boolean isExternalStorageWritable() {
        return Environment.MEDIA_MOUNTED.equals
                (Environment.getExternalStorageState());
    }

    public static void hideKeyboard(Activity activity,
                                    IBinder windowToken) {
        InputMethodManager mgr =
                (InputMethodManager) activity.getSystemService
                        (Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken,
                0);
    }

    public static void showNotify(Context context, NotificationManager mNotificationManager,
                              int notification_id) {
        Log.d(TAG, "Show Notification!");
        long[] tVibrate = {0,100,200,300};
        Notification.Builder notificationBuilder = new Notification.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentIntent(MainActivity.makeIntent(context))
                .setLights(0xff00ff00, 300, 1000)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setTicker("Notification!!")
                .setContentText("Download Image!")
                .setVibrate(tVibrate);

        mNotificationManager.notify(notification_id, notificationBuilder.build());

    }
}
