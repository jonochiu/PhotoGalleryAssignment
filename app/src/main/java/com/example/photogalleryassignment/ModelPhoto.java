package com.example.photogalleryassignment;

import java.util.Date;
import java.util.List;

public interface ModelPhoto {
    List<String> findPhotos(MainActivity view, Date startTimestamp, Date endTimestamp, String keywords, int lon, int lat);
}
