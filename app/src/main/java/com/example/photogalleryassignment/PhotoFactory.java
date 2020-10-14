package com.example.photogalleryassignment;

import android.annotation.SuppressLint;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.android.gms.common.util.Strings;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static android.content.ContentValues.TAG;

public class PhotoFactory {
    private static final String DELIMITER = "\r";//null char, impossible char to type on keyboard
    private static final int SYS_PATH_INDEX = 0;
    private static final int CAPTION_INDEX = 1;
    private static final int TIMESTAMP_INDEX = 2;
    private static final int LON_INDEX = 3;
    private static final int LAT_INDEX = 4;
    private static final int MISSING_LATLON = 4;
    private static DateFormat storedFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());

    @SuppressLint("NewApi")
    private <T1> T1 firstOrDefault(Stream<T1> items, T1 defaultValue, Predicate<T1> predicate) {
        Optional<T1> item = items.filter(predicate).findFirst();
        if (item.isPresent())
            return item.get();
        return defaultValue;
    }

    @SuppressLint("NewApi")
    public Photo getPhoto(String path) {
        return firstOrDefault(getPhotos().stream(), null, item -> item.getPath() == path);
    }

    @SuppressLint("NewApi")
    public List<Photo> getPhotos(String caption, Date startTimestamp, Date endTimestamp, float lon, float lat) {
        return getPhotos().stream()
                .filter(item -> Strings.isEmptyOrWhitespace(caption) || item.getCaption().contains((caption)))
                .filter(item -> startTimestamp == null || item.getTimestamp().compareTo(startTimestamp) > 0)
                .filter(item -> endTimestamp == null || item.getTimestamp().compareTo(endTimestamp) < 0)
                .filter(item -> lon == 0 || Math.abs(item.getLongitude() - lon) < 1)
                .filter(item -> lat == 0 || Math.abs(item.getLatitude() - lat) < 1)
                .collect(Collectors.toList());
    }

    public List<Photo> getPhotos() {
        File[] fList = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),
                "/Android/data/" + MainActivity.PACKAGE_NAME + "/files/Pictures").listFiles();
        List<Photo> photos = new ArrayList<>();
        if (fList != null) {
            for (File f : fList) {
                Photo photo = new PhotoEntry(f.getPath());
                String[] seg = f.getName().split(DELIMITER);
                try {
                    photo.setTimestamp(storedFormat.parse( seg[TIMESTAMP_INDEX]));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                photo.setCaption(seg[CAPTION_INDEX]);
                try {
                    photo.setLongitude(Float.parseFloat(seg[LON_INDEX]));
                    photo.setLatitude(Float.parseFloat(seg[LAT_INDEX]));
                }
                catch (Exception e) {
                    // Index out of boundary, no lon/lat
                }
                photos.add(photo);
            }
        }
        return photos;
    }
}
