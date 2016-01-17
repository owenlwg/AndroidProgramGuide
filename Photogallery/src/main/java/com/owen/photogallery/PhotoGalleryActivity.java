package com.owen.photogallery;

import android.support.v4.app.Fragment;

/**
 * Created by Owen on 2016/1/11.
 */
public class PhotoGalleryActivity extends SingleFragmentActivity{
    @Override
    protected Fragment createFragment() {
        PhotoGalleryFragment fragment = new PhotoGalleryFragment();
        return fragment;
    }
}
