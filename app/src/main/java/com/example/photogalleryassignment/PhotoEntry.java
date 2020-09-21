package com.example.photogalleryassignment;


import androidx.annotation.Nullable;

import java.util.Date;


public class PhotoEntry {

    private String caption;
    private String filepath;
    private Date timestamp;

    public PhotoEntry(String cap, String fp, Date time) {
        caption = cap;
        filepath = fp;
        timestamp = time;
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

    public void setCaption(String newCaption) {
        caption = newCaption;
    }
}
