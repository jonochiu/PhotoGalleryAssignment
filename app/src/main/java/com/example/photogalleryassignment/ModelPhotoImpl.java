package com.example.photogalleryassignment;

import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ModelPhotoImpl implements ModelPhoto{
    private static final String DELIMITER = "\r";//null char, impossible char to type on keyboard

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

}
