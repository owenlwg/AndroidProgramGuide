package com.owen.photogallery.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import com.owen.photogallery.FlickrFetcher;
import com.owen.photogallery.utils.FileUtils;
import com.owen.photogallery.utils.LruCache.MemoryCache;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * LruCache + disk cache
 * Created by Owen on 2016/1/12.
 */
public class ThumbnailDownloader extends HandlerThread {
    private static final String TAG = "ThumbnailDownloader";

    private static final int MESSAGE_DOWNLOAD = 1;

    private Handler mMainHandler;
    private Handler mHandler;
    private Map<ImageView, String> mImageViewsMap =
//            new ConcurrentHashMap<ImageView, String>();
            Collections.synchronizedMap(new HashMap<ImageView, String>());
    private Context mContext;
    private MemoryCache mMemoryCache;

    public ThumbnailDownloader(Context context, Handler mainHandler) {
        super(TAG);
        mContext = context;
        mMainHandler = mainHandler;
        mMemoryCache = MemoryCache.getInstance();
    }

    public void queueThumbnail(ImageView imageview, String url) {
        mImageViewsMap.put(imageview, url);
        Log.e(TAG, "mImageViewsMap size:" + mImageViewsMap.size());
        mHandler.obtainMessage(MESSAGE_DOWNLOAD, imageview)
                .sendToTarget();
    }

    @SuppressWarnings("handlerLeak")
    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_DOWNLOAD) {
                    ImageView imageview = (ImageView) msg.obj;
                    Log.e(TAG, "image url:" + mImageViewsMap.get(imageview));
                    handleRequest(imageview);
                }
            }
        };
    }

    private void handleRequest(final ImageView imageView) {
        final String url = mImageViewsMap.get(imageView);
        if (url == null) {
            return;
        }

        try {
            if (mMemoryCache.getBitmapFromCache(url) == null) {
                Bitmap bitmap = getImage(url);
                mMemoryCache.addBitmapToCache(url, bitmap);
            }

            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (url != mImageViewsMap.get(imageView)) {
                        return;
                    }
//                    mImageViewsMap.remove(imageView);
                    imageView.setImageBitmap(mMemoryCache.get(url));
                    Log.e(TAG, "memory cache size:" + mMemoryCache.maxSize() + "M");
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private Bitmap getImage(String url) throws IOException {
        Bitmap bitmap = null;
        String[] urls = url.split("/");
        String picName = urls[urls.length - 1];
        Log.e("picName:", picName);
        if (FileUtils.getFileUtils(mContext).isPicExists(picName)) {
            Log.e(TAG, "get bitmap from sdcard!");
            String picPath = FileUtils.getFileUtils(mContext).getExternalPictruePath(picName);
            bitmap = BitmapFactory.decodeFile(picPath);
        } else {
            Log.e(TAG, "get bitmap from http!");
            byte[] bitmapBytes = new FlickrFetcher().getUrlBytes(url);
            bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
            FileUtils.getFileUtils(mContext).savePic(bitmap, picName);
        }

        return bitmap;
    }
}
