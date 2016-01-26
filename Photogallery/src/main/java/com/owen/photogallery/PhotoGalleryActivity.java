package com.owen.photogallery;

import android.app.SearchManager;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;

import java.util.prefs.Preferences;

/**
 * Created by Owen on 2016/1/11.
 */
public class PhotoGalleryActivity extends SingleFragmentActivity{
    @Override
    protected Fragment createFragment() {
        PhotoGalleryFragment fragment = new PhotoGalleryFragment();
        return fragment;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        PhotoGalleryFragment fragment = (PhotoGalleryFragment) getSupportFragmentManager()
                                            .findFragmentById(R.id.fragmentContainer);
        if (intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            PreferenceManager.getDefaultSharedPreferences(this)
                    .edit()
                    .putString(Constant.PREF_SEARCH_QUERY, query)
                    .commit();

            fragment.updateItems();
        }
    }
}
