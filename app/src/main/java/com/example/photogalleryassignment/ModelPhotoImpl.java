package com.example.photogalleryassignment;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ModelPhotoImpl implements ModelPhoto{

    private static final String DELIMITER = "\r";//null char, impossible char to type on keyboard
    private static final int SYS_PATH_INDEX = 0;
    private static final int TIMESTAMP_INDEX = 2;//

    //List<String> photos;
    //int index;

    /*
    public ModelPhotoImpl(MainActivity view) {
        photos = findPhotos(view ,new Date(Long.MIN_VALUE), new Date(), "", 0, 0);
        index = 0;
    }

    public void incIndex() {
        index++;
    }

    public void decIndex() {
        index--;
    }

    public void setIndex(int newindex) {
        index = newindex;
    }

    public int getIndex() {
        return index;
    }
     */

    public List<String> findPhotos(MainActivity view, Date startTimestamp, Date endTimestamp, String keywords, int lon, int lat) {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),
                "/Android/data/" + view.getApplicationContext().getPackageName() + "/files/Pictures");
        List<String> photos = new ArrayList<>();
        File[] fList = file.listFiles();
        if (fList != null) {
            for (File f : fList) {
                boolean isUndated = startTimestamp == null && endTimestamp == null;
                boolean isWithinDateRange = f.lastModified() >= startTimestamp.getTime() && f.lastModified() <= endTimestamp.getTime();
                boolean isKeywordEmpty = keywords == null;
                boolean isKeywordMatch = f.getPath().contains(keywords);
                boolean isLonLatEmpty = lon == 0 && lat == 0;
                boolean isLonLatMatch = f.getPath().contains(lon + DELIMITER + lat);
                if ((isUndated || isWithinDateRange) && (isKeywordEmpty || isKeywordMatch) && (isLonLatEmpty || isLonLatMatch)) {
                    photos.add(f.getPath());
                }
            }
        }
        return photos;
    }

    /*
    public String updatePhoto(String filepath, String caption, String lon, String lat) {
        //we dont care if the original photo had lat lon, we can add that info now
        lon = lon.trim();
        lat = lat.trim();
        String[] data = filepath.split(DELIMITER);
        File from = new File(filepath);
        File to = new File(data[SYS_PATH_INDEX] + DELIMITER + caption + DELIMITER + data[TIMESTAMP_INDEX] + DELIMITER + lon + DELIMITER + lat+ DELIMITER + data[data.length-1]);
        boolean success = from.renameTo(to);
        if (success && photos.size() > 0) {
            photos.set(index, to.getAbsolutePath());
        }
        return success ? to.getAbsolutePath() : filepath;
    }
    */
}
