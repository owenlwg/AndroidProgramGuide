package com.owen.criminalintent.Model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Owen on 2015/12/29.
 */
public class Photo implements Serializable{
    private static final String JSON_PICNAME = "picName";
    private String mPicName;

    public Photo(String picName) {
        mPicName = picName;
    }

    public Photo(JSONObject json) throws JSONException {
        mPicName = json.getString(JSON_PICNAME);
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JSON_PICNAME, mPicName);
        return  jsonObject;
    }

    public String getPicName() {
        return mPicName;
    }

    public void setPicName(String picName) {
        mPicName = picName;
    }
}
