package com.example.photogalleryassignment;


import android.location.Location;

import androidx.annotation.Nullable;

import java.util.Date;


public class PhotoEntry implements Photo {
    private String caption;
    private String filepath;
    private Date timestamp;
    private float longitude;
    private float latitude;

    public PhotoEntry(String fp) {
        filepath = fp;
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
}
