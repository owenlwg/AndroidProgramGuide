package com.owen.criminalintent.Utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.widget.ImageView;


/**
 * Created by Owen on 2015/12/29.
 */
public class PictureUtils {
    /**
     * 对bitmap进行压缩处理
     * (最好是将bitmap压缩至需要显示的大小，然而由于无法获取ImageView的尺寸，稳妥的办法是
     * 缩放图片至设备的默认显示屏大小)
     */
    public static Drawable getScaledDrawable(Activity activity, String path) {
        if (TextUtils.isEmpty(path)) {
            return new ColorDrawable(Color.WHITE);
        }

        Display display = activity.getWindowManager().getDefaultDisplay();
        int desWidth = display.getWidth();
        int destHeight = display.getHeight();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        int srcWidth = options.outWidth;
        int srcHeight = options.outHeight;

        int inSampleSize = 1;
        if (srcHeight > destHeight || srcWidth > desWidth) {
            if (srcWidth > srcHeight) {
                inSampleSize = Math.round(srcHeight / destHeight);
            } else {
                inSampleSize = Math.round(srcWidth / desWidth);
            }
        }

        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        return new BitmapDrawable(activity.getResources(), bitmap);
    }

    /**
     * 清理ImageView的BitmapDrawable
     */
    public static void cleanImageView(ImageView imageView) {
        if (!(imageView.getDrawable() instanceof BitmapDrawable)) {
            return;
        }

        BitmapDrawable bitmap = (BitmapDrawable) imageView.getDrawable();
        //Android文档暗示不需要调用此方法，但实际上需要
        //Bitmap.recycle()释放了bitmap占用的原始存储空间
        //如果不主动调用recycle()方法释放内存，它会在将来某个时点的finalizer中清理，而不是在bitmap自身的垃圾回收时清理
        bitmap.getBitmap().recycle();
        Log.d("owen_d", "------bitmap recycle--------");
        imageView.setImageDrawable(null);
    }
}
