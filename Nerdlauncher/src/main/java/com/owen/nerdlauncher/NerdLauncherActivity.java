package com.owen.nerdlauncher;

import android.support.v4.app.Fragment;

/**
 * Created by Owen on 2016/1/8.
 */
public class NerdLauncherActivity extends SingleFragmentActivity{
    @Override
    protected Fragment createFragment() {
        return new NerdLauncherFragment();
    }
}
