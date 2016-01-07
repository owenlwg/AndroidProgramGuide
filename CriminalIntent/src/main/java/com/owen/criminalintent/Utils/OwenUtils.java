package com.owen.criminalintent.Utils;

import android.content.Context;

import com.owen.criminalintent.R;

/**
 * Created by Owen on 2016/1/6.
 */
public class OwenUtils {
    private static OwenUtils mOwenUtils;

    private OwenUtils() {
    }

    public static OwenUtils getInstance() {
        if (mOwenUtils == null) {
            mOwenUtils = new OwenUtils();
        }
        return mOwenUtils;
    }

    /**
     * 判断当前设备是手机还是平板
     */
    public boolean isTablet(Context context) {
        return context.getResources().getBoolean(R.bool.isTablet);
    }
}
