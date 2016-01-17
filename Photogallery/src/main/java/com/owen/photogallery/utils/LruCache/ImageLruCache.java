package com.owen.photogallery.utils.LruCache;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.util.LruCache;

import com.owen.photogallery.FlickrFetcher;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * LruCache + DiskLruCache
 */
public class ImageLruCache {
    private LruCache<String, Bitmap> mLruCache;
    private DiskLruCachePlus mDiskLruCache;
    private final Object mDiskCacheLock = new Object();


    public ImageLruCache(Context context) {
        if (mLruCache == null) {
            final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
            Log.e("owen", "maxMemory:" + maxMemory / 1024 + "MB");
            final int cacheSize = maxMemory / 8;
            Log.e("owen", "cacheSize:" + cacheSize / 1024 + "MB");
            mLruCache = new LruCache<String, Bitmap>(cacheSize) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    return bitmap.getByteCount() / 1024;
                }
            };
        }
        if (mDiskLruCache == null) {
            mDiskLruCache = new DiskLruCachePlus(context);
        }
    }

    public void addBitmapToCache(String key, byte[] bitmapBytes) {
        if (bitmapBytes != null) {
            if (mLruCache != null && mLruCache.get(key) == null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
                mLruCache.put(key, bitmap);
            }
            synchronized (mDiskCacheLock) {
                if (mDiskLruCache != null && mDiskLruCache.get(key) == null) {
                    mDiskLruCache.put(key, bitmapBytes);
                }
            }
        }
    }

    public Bitmap getBitmapFromCache(String key) {
        Bitmap bitmap = mLruCache.get(key);
        Log.e("owen", "LruCache bitmap is " + (bitmap == null ?"null":"not null"));
        Log.e("owen", "LruCache size:" + mLruCache.size());
        if (bitmap == null) {
            synchronized (mDiskCacheLock) {
                bitmap = mDiskLruCache.get(key);
            }
            if (bitmap != null) {
                mLruCache.put(key, bitmap);
            }
        }
        return bitmap;
    }

}
