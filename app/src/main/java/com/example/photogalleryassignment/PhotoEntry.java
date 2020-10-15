package com.example.photogalleryassignment;


import android.location.Location;

import androidx.annotation.Nullable;

import java.io.File;
import java.util.Date;


public class PhotoEntry implements Photo {
    private String caption;
    private String filepath;
    private String absolutePath;
    private Date timestamp;
    private float longitude;
    private float latitude;



    public PhotoEntry(String fp, String ap) {
        filepath = fp;
        absolutePath = ap;
    }

    @Override
    public String getCaption() {
        return caption;
    }

    @Override
    public String getPath() {
        return filepath;
    }

    @Override
    public float getLongitude() {
        return longitude;
    }

    @Override
    public float getLatitude() {
        return latitude;
    }

    @Override
    public Date getTimestamp() {
        return timestamp;
    }

    @Override
    public void setCaption(String caption) {
        this.caption = caption;
    }

    @Override
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public void setLongitude(float lon) {
        this.longitude = lon;
    }

    @Override
    public void setLatitude(float lat) {
        this.latitude = lat;
    }

    private static final String DELIMITER = "\r";//null char, impossible char to type on keyboard
    private static final int SYS_PATH_INDEX = 0;
    private static final int TIMESTAMP_INDEX = 2;

    @Override
    public void Save() {
        String[] data = filepath.split(DELIMITER);
        String newpath = data[SYS_PATH_INDEX] + DELIMITER + caption + DELIMITER + data[TIMESTAMP_INDEX] + DELIMITER + longitude + DELIMITER + latitude + DELIMITER + data[data.length - 1];
        File target = new File(newpath);
        new File(filepath).renameTo(target);
        absolutePath = target.getAbsolutePath();
        filepath = target.getPath();
    }
}
