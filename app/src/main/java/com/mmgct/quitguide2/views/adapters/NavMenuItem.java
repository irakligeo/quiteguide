package com.mmgct.quitguide2.views.adapters;

/**
 * Created by 35527 on 11/4/2015.
 */
public class NavMenuItem {

    private int imgResId;
    private String itemLabel;

    public NavMenuItem() {}

    public NavMenuItem(int imgResId, String itemLabel) {
        this.imgResId = imgResId;
        this.itemLabel = itemLabel;
    }

    public int getImgResId() {
        return imgResId;
    }

    public void setImgResId(int imgResId) {
        this.imgResId = imgResId;
    }

    public String getItemLabel() {
        return itemLabel;
    }

    public void setItemLabel(String itemLabel) {
        this.itemLabel = itemLabel;
    }
}
