package com.mmgct.quitguide2.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Holds information related to the applications state. There should only be one record for the app.
 *
 * Created by 35527 on 10/26/2015.
 */
@DatabaseTable
public class State {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private boolean introMode;

    @DatabaseField
    private boolean inTutorial;

    @DatabaseField
    private boolean quitDateSet;

    @DatabaseField
    private boolean ingested;

    @DatabaseField
    private long lastUpdate;

    @DatabaseField
    private long joinedDate;

    @DatabaseField
    private boolean allowNotification;

    @DatabaseField
    private long appLastOpenedTimestamp;

    @DatabaseField
    private int appVersion;

    @DatabaseField
    private boolean showWhatsNewScreen;

    @DatabaseField
    private int totalNumberOfDaysUsed;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isIntroMode() {
        return introMode;
    }

    public void setIntroMode(boolean introMode) {
        this.introMode = introMode;
    }

    public boolean isQuitDateSet() {
        return quitDateSet;
    }

    public void setQuitDateSet(boolean quitDateSet) {
        this.quitDateSet = quitDateSet;
    }

    public boolean isInTutorial() {
        return inTutorial;
    }

    public void setInTutorial(boolean inTutorial) {
        this.inTutorial = inTutorial;
    }

    public boolean isIngested() {
        return ingested;
    }

    public void setIngested(boolean contentIngested) {
        this.ingested = contentIngested;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public long getJoinedDate() {
        return joinedDate;
    }

    public void setJoinedDate(long joinedDate) {
        this.joinedDate = joinedDate;
    }

    public boolean isAllowNotification() {
        return allowNotification;
    }

    public void setAllowNotification(boolean allowNotification) {
        this.allowNotification = allowNotification;
    }

    public long getAppLastOpenedTimestamp() {
        return appLastOpenedTimestamp;
    }

    public void setAppLastOpenedTimestamp(long appLastOpenedTimestamp) {
        this.appLastOpenedTimestamp = appLastOpenedTimestamp;
    }

    public int getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(int appVersion) {
        this.appVersion = appVersion;
    }

    public boolean isShowWhatsNewScreen() {
        return showWhatsNewScreen;
    }

    public void setShowWhatsNewScreen(boolean showWhatsNewScreen) {
        this.showWhatsNewScreen = showWhatsNewScreen;
    }

    public int getTotalNumberOfDaysUsed() {
        return totalNumberOfDaysUsed;
    }

    public void setTotalNumberOfDaysUsed(int totalNumberOfDaysUsed) {
        this.totalNumberOfDaysUsed = totalNumberOfDaysUsed;
    }
}
