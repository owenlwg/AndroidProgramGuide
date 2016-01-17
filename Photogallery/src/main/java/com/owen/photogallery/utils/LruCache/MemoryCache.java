package com.owen.photogallery.utils.LruCache;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

import java.lang.String;

/**
 * Created by Owen on 2016/1/12.
 */
public class MemoryCache extends LruCache<String, Bitmap>{

    private static MemoryCache sMemoryCache;

    public static MemoryCache getInstance() {
        if (sMemoryCache == null) {
            final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
            Log.e("owen", "maxMemory:" + maxMemory / 1024 + "MB");
            final int cacheSize = maxMemory / 8;
            Log.e("owen", "cacheSize:" + cacheSize / 1024 + "MB");
            sMemoryCache = new MemoryCache(cacheSize);
        }
        return sMemoryCache;
    }

    public MemoryCache(int maxSize) {
        super(maxSize);
    }

    public void addBitmapToCache(String key, Bitmap bitmap) {
        if (getBitmapFromCache(key) == null) {
            put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromCache(String key) {
        return get(key);
    }

}
