package com.owen.criminalintent.Utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by Owen on 2015/12/24.
 */
public class FileUtils {

    private Context mContext;
    private static FileUtils sFileUtils;
    //5MB
    private static final long FREE_SPACE_LIMIT = 5 * 1024 * 1024;
    private static final String JSON_FILE_DIR = "json_file";
    private static final String PICTURES_DIR = "pictures";

    private FileUtils(Context context) {
        mContext = context;
    }

    public static FileUtils getFileUtils(Context context) {
        if (sFileUtils == null) {
            sFileUtils = new FileUtils(context.getApplicationContext());
        }
        return sFileUtils;
    }

    public boolean isExternalSpaceWritable() {
        if (isExternalStorageWritable() && hasFreeSpace()) {
            return true;
        }
        return false;
    }

    public File getExternalJSONFile(String filename) {
        File file = new File(getExternalJSONFileDir(), filename);
        return file;
    }

    public File getExternalPictrue(String picname) {
        File file = new File(getExternalPicturesDir(), picname);
        return file;
    }

    public String getExternalPictruePath(String picname) {
        File file = new File(getExternalPicturesDir(), picname);
        if (file.exists()) {
            return file.getAbsolutePath();
        } else {
            return "";
        }
    }

    private File getExternalJSONFileDir() {
        File file = new File(mContext.getExternalFilesDir(null), JSON_FILE_DIR);
        if (!file.exists()) {
            file.mkdir();
        }
        return file;
    }

    private File getExternalPicturesDir() {
        File file = new File(mContext.getExternalFilesDir(null), PICTURES_DIR);
        if (!file.exists()) {
            file.mkdir();
        }
        return file;
    }


    private boolean hasFreeSpace() {
        ///华为荣耀 storage/emulated/0
        File file = Environment.getExternalStorageDirectory();
        if (file != null) {
            Log.d("owen_d", "freeSpace: " + (file.getUsableSpace() / (1024*1024)) + "MB");
            Log.d("owen_d", "totalSpace: " + (file.getTotalSpace() / (1024*1024)) + "MB");
            Log.d("owen_d", "usableSpace: " + (file.getUsableSpace() / (1024*1024)) + "MB");

            long usableSpace = file.getUsableSpace(); //bytes
            if (usableSpace > FREE_SPACE_LIMIT) {
                return true;
            }
        }

        return false;
    }

    /* Checks if external storage is available for read and write */
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    private boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                    Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

}
