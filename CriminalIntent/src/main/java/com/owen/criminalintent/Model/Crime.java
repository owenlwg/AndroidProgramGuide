package com.owen.criminalintent.Model;

import android.text.format.DateFormat;

import com.owen.criminalintent.Utils.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Owen on 2015/12/9.
 */
public class Crime {
    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;
    private Photo mPhoto;
    private String mSuspect;

    private static final String JSON_ID = "id";
    private static final String JSON_TITLE = "title";
    private static final String JSON_DATE = "date";
    private static final String JSON_RESOLVED = "solved";
    private static final String JSON_PHOTO = "photo";
    private static final String JSON_SUSPECT = "suspect";

    public Crime() {
        mId=UUID.randomUUID();
        mDate = new Date();
    }

    public Crime(JSONObject jsonObject) throws JSONException {
        mId = UUID.fromString(jsonObject.getString(JSON_ID));
        if (jsonObject.has(JSON_TITLE)) {
            mTitle = jsonObject.getString(JSON_TITLE);
        }
        if (jsonObject.has(JSON_PHOTO)) {
            mPhoto = new Photo(jsonObject.getJSONObject(JSON_PHOTO));
        }
        if (jsonObject.has(JSON_SUSPECT)) {
            mSuspect = jsonObject.getString(JSON_SUSPECT);
        }
        mDate = new Date(jsonObject.getLong(JSON_DATE));
        mSolved = jsonObject.getBoolean(JSON_RESOLVED);
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }

    public Photo getPhoto() {
        return mPhoto;
    }

    public void setPhoto(Photo photo) {
        mPhoto = photo;
    }

    public String getSuspect() {
        return mSuspect;
    }

    public void setSuspect(String suspect) {
        mSuspect = suspect;
    }

    public String getDateString() {
        return DateFormat.format(Constant.CRIME_DATE_FORMAT, getDate()).toString();
    }

    public JSONObject toJson() throws JSONException {
        JSONObject object = new JSONObject();
        object.put(JSON_ID, mId.toString());
        object.put(JSON_TITLE, mTitle);
        object.put(JSON_DATE, mDate.getTime());
        object.put(JSON_RESOLVED, mSolved);
        object.put(JSON_SUSPECT, mSuspect);
        if (mPhoto != null) {
            object.put(JSON_PHOTO, mPhoto.toJSON());
        }
        return object;
    }

}
