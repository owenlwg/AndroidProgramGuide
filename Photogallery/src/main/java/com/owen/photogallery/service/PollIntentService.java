package com.owen.photogallery.service;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.owen.photogallery.Constant;
import com.owen.photogallery.FlickrFetcher;
import com.owen.photogallery.PhotoGalleryActivity;
import com.owen.photogallery.R;
import com.owen.photogallery.model.GalleryItem;
import com.owen.uitls.CommonUtils;

import java.util.ArrayList;

/**
 *
 */
public class PollIntentService extends IntentService {
    private static final String TAG = "PollIntentService";
    private static final int POLL_INTERVAL = 5 * 1000; //15s


    public PollIntentService() {
        super(TAG);
    }
    
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "Received an intent: " + intent);
        if (!CommonUtils.isNetworkAvailable(this)) {
            return;
        }

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String query = sp.getString(Constant.PREF_SEARCH_QUERY, null);
        String lastResultId = sp.getString(Constant.PREF_LAST_RESULT_ID, null);

        ArrayList<GalleryItem> items;
        if (query != null) {
            items = new FlickrFetcher().search(this, query);
        } else {
            items = new FlickrFetcher().fetchItems(this);
        }

        if (items.size() == 0) {
            return;
        }

        String resultId = items.get(0).getId();

        if (!resultId.equals(lastResultId)) {
            Log.i(TAG, "Got a new result: " + resultId);

            PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this, PhotoGalleryActivity.class), 0);

            Notification notification = new NotificationCompat.Builder(this)
                                         .setTicker(getResources().getString(R.string.new_pic_title))
                                         .setSmallIcon(android.R.drawable.ic_menu_report_image)
                                         .setContentTitle(getResources().getString(R.string.new_pic_title))
                                         .setContentText(getResources().getString(R.string.new_pic_text))
                                         .setContentIntent(pi)
                                         .setAutoCancel(true)
                                         .build();

            showBackgroundNotification(0, notification);
        } else {
            Log.i(TAG, "Got a old result: " + resultId);
        }

        sp.edit()
          .putString(Constant.PREF_LAST_RESULT_ID, resultId)
          .commit();
    }

    private void showBackgroundNotification(int requestcode, Notification notification) {
        Intent intent = new Intent(Constant.ACTION_SHOW_NOTIFICATION);
        intent.putExtra("request_code", requestcode);
        intent.putExtra("notification", notification);

        sendOrderedBroadcast(intent, Constant.PERMISSION_PRIVATE, null, null, Activity.RESULT_OK,
                                    null, null);

    }

    public static void setServiceAlarm(Context context, boolean isOn) {
        Intent intent = new Intent(context, PollIntentService.class);
        PendingIntent pi = PendingIntent.getService(context, 0, intent, 0);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (isOn) {
            am.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), POLL_INTERVAL, pi);
        } else {
            am.cancel(pi);
            pi.cancel();
        }

        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(Constant.PREF_IS_ALARM_ON, isOn)
                .commit();
    }

    public static boolean isServiceAlarmOn(Context context) {
        Intent intent = new Intent(context, PollIntentService.class);
        PendingIntent pi = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_NO_CREATE);
        return pi != null;
    }
}
