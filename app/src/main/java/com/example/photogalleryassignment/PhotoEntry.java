package com.example.photogalleryassignment;


import android.location.Location;

import androidx.annotation.Nullable;

import java.util.Date;


public class PhotoEntry {

    private String caption;
    private String filepath;
    private Date timestamp;
    private String longitude;
    private String latitude;

    public PhotoEntry(String cap, String fp, Date time, String photoLongitude, String photoLatitude) {
        caption = cap;
        filepath = fp;
        timestamp = time;
        longitude = photoLongitude;
        latitude = photoLatitude;
    }

    public String getCaption(){
        return caption;
    }

    public String getFilepath(){
        return filepath;
    }

    public Date getTimestamp(){
        return timestamp;
    }

    public String getLongitude() { return longitude;}

    public String getLatitude() { return latitude;}

    public void setCaption(String newCaption) {
        caption = newCaption;
    }
}
