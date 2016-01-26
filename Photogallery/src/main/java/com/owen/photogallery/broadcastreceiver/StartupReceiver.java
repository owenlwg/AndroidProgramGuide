package com.owen.photogallery.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.owen.photogallery.Constant;
import com.owen.photogallery.service.PollIntentService;

public class StartupReceiver extends BroadcastReceiver {
    private static final String TAG = "StartupReceiver";
    public StartupReceiver() {
    }
    
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "StartupReceiver onReceive resultCode:" + getResultCode());
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isOn = sp.getBoolean(Constant.PREF_IS_ALARM_ON, false);
        PollIntentService.setServiceAlarm(context, isOn);
    }
}
