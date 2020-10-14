package com.example.photogalleryassignment;

import java.util.Date;

public interface Photo {
    String getCaption();
    String getPath();
    float getLongitude();
    float getLatitude();
    Date getTimestamp();

    void setCaption(String caption);
    void setTimestamp(Date timestamp);
    void setLongitude(float lon);
    void setLatitude(float lat);
}