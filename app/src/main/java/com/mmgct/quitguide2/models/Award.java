package com.mmgct.quitguide2.models;

import android.util.Log;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents an award record.
 *
 * Created by 35527 on 10/26/2015.
 *
 */
@DatabaseTable
public class Award {

    private static final String TAG = "Award";
    public static final String AWARD_ID_KEY = "awardID";
    public static final String AWARD_DESC_KEY = "awardDescription";
    public static final String AWARD_REQ_KEY = "reqDescription";
    public static final String AWARD_NAME_KEY = "awardName";
    public static final String AWARD_IMG_KEY = "imgName";
    public static final String AWARD_DIS_IMG_KEY = "disabledImgName";
    public static final String AWARD_EARNED_KEY = "awarded";
    public static final String AWARD_REQ_NUM_KEY = "reqNumber";

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private int key;

    @DatabaseField
    private String awardDesc;

    @DatabaseField
    private String reqDesc;

    @DatabaseField
    private String awardName;

    @DatabaseField
    private String imgName;

    @DatabaseField
    private String disabledImgName;

    @DatabaseField
    private boolean awarded;

    @DatabaseField
    private int reqNumber;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getAwardDesc() {
        return awardDesc;
    }

    public void setAwardDesc(String awardDesc) {
        this.awardDesc = awardDesc;
    }

    public String getReqDesc() {
        return reqDesc;
    }

    public void setReqDesc(String reqDesc) {
        this.reqDesc = reqDesc;
    }

    public String getAwardName() {
        return awardName;
    }

    public void setAwardName(String awardName) {
        this.awardName = awardName;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }

    public String getDisabledImgName() {
        return disabledImgName;
    }

    public void setDisabledImgName(String disabledImgName) {
        this.disabledImgName = disabledImgName;
    }

    public boolean isAwarded() {
        return awarded;
    }

    public void setAwarded(boolean awarded) {
        this.awarded = awarded;
    }

    public int getReqNumber() {
        return reqNumber;
    }

    public void setReqNumber(int reqNumber) {
        this.reqNumber = reqNumber;
    }

    @Override
    public String toString() {
        return "Award{" +
                "id=" + id +
                ", key=" + key +
                ", awardDesc='" + awardDesc + '\'' +
                ", reqDesc='" + reqDesc + '\'' +
                ", awardName='" + awardName + '\'' +
                ", imgName='" + imgName + '\'' +
                ", disabledImgName='" + disabledImgName + '\'' +
                ", awarded=" + awarded +
                ", reqNumber=" + reqNumber +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Award award = (Award) o;

        if (id != award.id) return false;
        if (key != award.key) return false;
        if (awarded != award.awarded) return false;
        if (reqNumber != award.reqNumber) return false;
        if (awardDesc != null ? !awardDesc.equals(award.awardDesc) : award.awardDesc != null)
            return false;
        if (reqDesc != null ? !reqDesc.equals(award.reqDesc) : award.reqDesc != null) return false;
        if (awardName != null ? !awardName.equals(award.awardName) : award.awardName != null)
            return false;
        if (imgName != null ? !imgName.equals(award.imgName) : award.imgName != null) return false;
        return !(disabledImgName != null ? !disabledImgName.equals(award.disabledImgName) : award.disabledImgName != null);

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + key;
        result = 31 * result + (awardDesc != null ? awardDesc.hashCode() : 0);
        result = 31 * result + (reqDesc != null ? reqDesc.hashCode() : 0);
        result = 31 * result + (awardName != null ? awardName.hashCode() : 0);
        result = 31 * result + (imgName != null ? imgName.hashCode() : 0);
        result = 31 * result + (disabledImgName != null ? disabledImgName.hashCode() : 0);
        result = 31 * result + (awarded ? 1 : 0);
        result = 31 * result + reqNumber;
        return result;
    }

    public static Award awardFromJSONObject(JSONObject jsonObject) {
        Award award = new Award();

        try {
            award.setKey(jsonObject.getInt(AWARD_ID_KEY));
            award.setAwardDesc(jsonObject.getString(AWARD_DESC_KEY));
            award.setReqDesc(jsonObject.getString(AWARD_REQ_KEY));
            award.setAwardName(jsonObject.getString(AWARD_NAME_KEY));
            award.setImgName(jsonObject.getString(AWARD_IMG_KEY));
            award.setDisabledImgName(jsonObject.getString(AWARD_DIS_IMG_KEY));
            award.setAwarded(jsonObject.getBoolean(AWARD_EARNED_KEY));
            award.setReqNumber(jsonObject.getInt(AWARD_REQ_NUM_KEY));

        } catch (JSONException e) {
            Log.e(TAG, "Error occured while ingesting content.", e);
        }

        return award;
    }
}
