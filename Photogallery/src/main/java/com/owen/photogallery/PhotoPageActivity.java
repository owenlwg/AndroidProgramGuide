package com.owen.photogallery;

import android.app.SearchManager;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;

public class PhotoPageActivity extends SingleFragmentActivity{
    @Override
    protected Fragment createFragment() {
        PhotoPageFragment fragment = new PhotoPageFragment();
        return fragment;
    }

}
