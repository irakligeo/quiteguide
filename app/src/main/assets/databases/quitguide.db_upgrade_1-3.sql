CREATE TABLE GeoTag
(
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    lat REAL,
    lon REAL,
    dateCreated INTEGER,
    address TEXT
);

-- Creating table Notification from scratch (simple ALTER TABLE is not enough)

CREATE TABLE temp_Notification_534011718
(
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    [key] INTEGER,
    message TEXT,
    detail TEXT,
    daysRelativeToQuitDate INTEGER,
    daysRelativeToProgramStart INTEGER,
    daysRelativeToLastActivity INTEGER,
    daysRelativeToNoProfileSet INTEGER,
    timeOfDay TEXT,
    openToScreen TEXT,
    reminderDate TEXT,
    notificationHistory_id INTEGER,
    recurringNotification_id INTEGER,
    FOREIGN KEY (recurringNotification_id) REFERENCES RecurringNotification
);

-- Copying rows from original table to the new table

INSERT INTO temp_Notification_534011718 (id,[key],message,detail,daysRelativeToQuitDate,daysRelativeToProgramStart,daysRelativeToLastActivity,daysRelativeToNoProfileSet,timeOfDay,openToScreen,reminderDate,notificationHistory_id,recurringNotification_id) SELECT id,[key],message,detail,daysRelativeToQuitDate,daysRelativeToProgramStart,daysRelativeToLastActivity,daysRelativeToNoProfileSet,timeOfDay,openToScreen,reminderDate,notificationHistory_id,NULL AS recurringNotification_id FROM Notification;

-- Droping the original table and renaming the temporary table

DROP TABLE Notification;
ALTER TABLE temp_Notification_534011718 RENAME TO Notification;

-- Creating table ReasonForQuitting from scratch (simple ALTER TABLE is not enough)

CREATE TABLE temp_ReasonForQuitting_1655911537
(
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    imagePath TEXT,
    message TEXT
);

-- Copying rows from original table to the new table

INSERT INTO temp_ReasonForQuitting_1655911537 (id,message,imagePath) SELECT id,message,NULL AS imagePath FROM ReasonForQuitting;

-- Droping the original table and renaming the temporary table

DROP TABLE ReasonForQuitting;
ALTER TABLE temp_ReasonForQuitting_1655911537 RENAME TO ReasonForQuitting;

CREATE TABLE RecurringNotification
(
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    geoTag_id BLOB,
    tip_id BLOB,
    notification_id BLOB,
    active INTEGER,
    useRandTip INTEGER,
    FOREIGN KEY (geoTag_id) REFERENCES GeoTag,
    FOREIGN KEY (tip_id) REFERENCES Tip,
    FOREIGN KEY (notification_id) REFERENCES Notification
);

-- Adding missing table columns

ALTER TABLE State ADD COLUMN appVersion INTEGER;
ALTER TABLE State ADD COLUMN showWhatsNewScreen INTEGER;
ALTER TABLE State ADD COLUMN totalNumberOfDaysUsed INTEGER;

CREATE TABLE Tip
(
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    userCreated INTEGER,
    content TEXT,
    imgFilePath TEXT
);