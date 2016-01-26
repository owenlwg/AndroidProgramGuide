package com.owen.photogallery.broadcastreceiver;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.owen.photogallery.Constant;
import com.owen.photogallery.service.PollIntentService;

public class NotificationReceiver extends BroadcastReceiver {
    private static final String TAG = "NotificationReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "received result: " + getResultCode());

        if (getResultCode() != Activity.RESULT_OK) {
            return;
        }

        int requestCode = intent.getIntExtra("request_code", 0);
        Notification notification = intent.getParcelableExtra("notification");

        NotificationManager nm = (NotificationManager)
                                         context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(requestCode, notification);
    }
}
