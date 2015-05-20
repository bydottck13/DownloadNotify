package com.brown.downloadnotify;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

/**
 * Created by Brown on 2015/5/18.
 */
public class DownloadImagesService extends IntentService {

    private final String TAG = getClass().getSimpleName();

    private static final String DIRECTORY_PATHNAME = "DIRECTORY_PATHNAME";

    private int notification_id = 1;

    public DownloadImagesService() {
        super("DownloadImageService");
    }

    public static Intent makeIntent(Context context,
                                    Uri url,
                                    String directoryPathname) {
        Intent serviceIntent = new Intent(context, DownloadImagesService.class);
        serviceIntent.setData(url);
        serviceIntent.putExtra(DIRECTORY_PATHNAME, directoryPathname);
        return serviceIntent;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Uri url = intent.getData();
        Log.d(TAG, "Download url " + url);

        String pathname = (String) intent.getExtras().get(DIRECTORY_PATHNAME);
        Log.d(TAG, "Download pathname " + pathname);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);


        Uri image = Utils.downloadImage(DownloadImagesService.this, url, pathname);
        Log.d(TAG, "Download image "+image);

        Utils.showNotify(this, mNotificationManager, notification_id);

    }

}
