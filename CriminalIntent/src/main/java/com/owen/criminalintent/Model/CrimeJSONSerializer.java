package com.owen.criminalintent.Model;

import android.content.Context;
import android.util.Log;

import com.owen.criminalintent.Utils.FileUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

/**
 * Created by Owen on 2015/12/16.
 */
public class CrimeJSONSerializer {

    private Context mContext;
    private String mFilename;


    public CrimeJSONSerializer(Context context, String filename) {
        mContext = context;
        mFilename = filename;
    }

    public void saveCrimes(ArrayList<Crime> crimes) throws IOException, JSONException {
        JSONArray jsonArray = new JSONArray();
        Writer writer = null;
        FileOutputStream fos = null;
        try {
            for (Crime crime : crimes) {
                jsonArray.put(crime.toJson());
            }

            Log.d("owen_d", jsonArray.toString());

            if (FileUtils.getFileUtils(mContext).isExternalSpaceWritable()) {
                //华为荣耀 /storage/emulated/0/Android/data/com.owen.criminalintent/files
                File file = FileUtils.getFileUtils(mContext).getExternalJSONFile(mFilename);
                fos = new FileOutputStream(file);
            } else {
                fos = mContext.openFileOutput(mFilename, Context.MODE_PRIVATE);
            }

            writer = new OutputStreamWriter(fos);
            writer.write(jsonArray.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public ArrayList<Crime> loadCrimes() throws IOException, JSONException {
        ArrayList<Crime> crimes = new ArrayList<Crime>();
        BufferedReader br = null;
        InputStream fis = null;
        try {
            File file = FileUtils.getFileUtils(mContext).getExternalJSONFile(mFilename);
            if (file.exists()) {
                fis = new FileInputStream(file);
            } else {
                fis = mContext.openFileInput(mFilename);
            }

            br = new BufferedReader(new InputStreamReader(fis));
            StringBuilder sb = new StringBuilder();
            String line = "";
            try {
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                Log.d("owen_d", sb.toString());

                //解析json
                JSONArray jsonArray = (JSONArray) new JSONTokener(sb.toString()).nextValue();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    crimes.add(new Crime(jsonObject));
                }

            } finally {
                try {
                    if (br != null) {
                        br.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return crimes;
    }


}
