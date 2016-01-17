package com.owen.photogallery.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.owen.photogallery.FlickrFetcher;
import com.owen.photogallery.R;
import com.owen.photogallery.utils.LruCache.DiskLruCache;
import com.owen.photogallery.utils.LruCache.ImageLruCache;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.IllegalFormatCodePointException;
import java.util.Map;

/**
 * LruCache+DiskLruCache
 *
 * Created by Owen on 2016/1/15.
 */
public class ImageDownloader {

    private Context mContext;
    private ImageLruCache mImageLruCache;
    private Map<ImageView, String> mImageViewsMap =
//            new ConcurrentHashMap<ImageView, String>();
            Collections.synchronizedMap(new HashMap<ImageView, String>());

    public ImageDownloader(Context context) {
        mContext = context;
        mImageLruCache = new ImageLruCache(context);
    }

    public void loadImage(ImageView imageview, String url) {
        mImageViewsMap.put(imageview, url);
        Bitmap bitmap = mImageLruCache.getBitmapFromCache(url);
        if (bitmap == null) {
            BitmapWorkerTask task = new BitmapWorkerTask();
            task.execute(imageview);
        } else {
            imageview.setImageBitmap(bitmap);
        }
    }

    class BitmapWorkerTask extends AsyncTask<ImageView, Void, String> {
        ImageView iamgeView;
        @Override
        protected String doInBackground(ImageView... params) {
            iamgeView = params[0];
            String imageUrl = mImageViewsMap.get(iamgeView);
            if (imageUrl == null) {
                return null;
            }

            Bitmap bitmap = mImageLruCache.getBitmapFromCache(imageUrl);

            if (bitmap == null) {
                byte[] bitmapBytes = downloadBitmap(imageUrl);
                mImageLruCache.addBitmapToCache(imageUrl, bitmapBytes);
            }

            return imageUrl;
        }

        @Override
        protected void onPostExecute(String imageUrl) {
            if (mImageViewsMap.get(iamgeView) != imageUrl) {
                return;
            }

            iamgeView.setImageBitmap(mImageLruCache.getBitmapFromCache(imageUrl));
        }
    }

    private byte[] downloadBitmap(String url) {
        byte[] bitmapBytes = null;
        try {
            bitmapBytes = new FlickrFetcher().getUrlBytes(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmapBytes;
    }

}
