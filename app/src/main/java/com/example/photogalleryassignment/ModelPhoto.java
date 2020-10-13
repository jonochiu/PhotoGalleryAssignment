package com.example.photogalleryassignment;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public interface ModelPhoto {
    void incIndex();
    void decIndex();
    void setIndex(int newIndex);
    int getIndex();

    List<String> findPhotos(MainActivity view, Date startTimestamp, Date endTimestamp, String keywords, int lon, int lat);
    //File createImageFile() throws IOException;
    String updatePhoto(String filepath, String caption, String lon, String lat);
}
