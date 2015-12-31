package com.owen.criminalintent.Model;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Owen on 2015/12/11.
 */
public class CrimeLab {
    public static CrimeLab sCrimeLab;
    private ArrayList<Crime> mCrimes;
    private Context mAppContext;
    private CrimeJSONSerializer mJSONSerializer;
    private static final String FILE_NAME = "crimes_json";

    public static CrimeLab getCrimeLab(Context context) {
        if(sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context.getApplicationContext());  //使用应用层面的ApplicationContext
        }
        return sCrimeLab;
    }

    private CrimeLab(Context context) {
        mAppContext = context;
        mJSONSerializer = new CrimeJSONSerializer(context, FILE_NAME);
        try {
            mCrimes = mJSONSerializer.loadCrimes();
        } catch (Exception e) {
            e.printStackTrace();
            mCrimes = new ArrayList<>();
        }
//        for (int i = 0; i < 100; i++) {
//            Crime c = new Crime();
//            c.setTitle("Crime #" + i);
//            c.setSolved(i % 2 ==0);
//            mCrimes.add(c);
//        }
    }

    public void addCrime(Crime crime) {
        mCrimes.add(crime);
    }

    public void deleteCrime(Crime crime) {
        mCrimes.remove(crime);
    }

    public ArrayList<Crime> getCrimes() {
        return  mCrimes;
    }

    public Crime getCrime(UUID uuid) {
        for (Crime crime:mCrimes) {
            if (crime.getId().equals(uuid)) {
                return crime;
            }
        }
        return null;
    }

    public boolean saveCrime() {
        try {
            mJSONSerializer.saveCrimes(mCrimes);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
