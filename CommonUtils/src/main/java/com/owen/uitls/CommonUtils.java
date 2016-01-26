package com.owen.uitls;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;


/**
 * 提供了一些常用的公共方法
 */
public class CommonUtils {

    /**
     * 判断当前设备是手机还是平板
     */
    public static boolean isTablet(Context context) {
        return context.getResources().getBoolean(R.bool.isTablet);
    }

    /**
     * 判断网络是否可用
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager)
                                         context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getBackgroundDataSetting() && cm.getActiveNetworkInfo() != null;
    }

    /**
     * 判断某个PendingIntent是否已存在
     */
    public static boolean isServiceAlarmOn(Context context) {
        Intent intent = new Intent(context, Service.class); //Service需要替换
        PendingIntent pi = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_NO_CREATE);
        return pi != null;
    }






}
