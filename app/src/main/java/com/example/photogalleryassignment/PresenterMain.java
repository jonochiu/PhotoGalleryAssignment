package com.example.photogalleryassignment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;
import android.view.View;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.List;


public class PresenterMain {
    MainActivity view;
    private ModelPhoto model;

    private static final int DEFAULT_DIMENS = 250;
    private static final String DELIMITER = "\r";//null char, impossible char to type on keyboard
    private static final int CAPTION_INDEX = 1;//
    private static final int TIMESTAMP_INDEX = 2;//
    public static DateFormat displayFormat; //
    private static DateFormat storedFormat; //
    private static final int LON_INDEX = 3;
    private static final int LAT_INDEX = 4;
    //filename.split(delimiter) == 4 -> no lat lon
    private static final int MISSING_LATLON = 4;//

    private List<String> photos;
    private int index = 0;

    public PresenterMain(ModelPhoto model) {
        this.model = model;
    }

    public void bind(MainActivity view) {
        this.view = view;
    }

    public void unbind() {
        view = null;
    }

    //Need to edit out List<string>photos out when i add array back in this class
    public void delete(List<String> photos, int index, ImageView imageview, TextView timestamp,
                       EditText caption, EditText lat, EditText lon) {
        try {
            File imagePath = new File(photos.get(index));
            if(imagePath.delete()) {
                Toast.makeText(view, "Deleted the file: " + imagePath.getName(), Toast.LENGTH_LONG).show();
                photos.remove(index);
                if (photos.size() == 0) {
                    displayPhoto(null, imageview, timestamp, caption, lat, lon);
                } else {
                    index = 0;
                    displayPhoto(photos.get(index), imageview, timestamp, caption, lat, lon);
                }
            }
        } catch (Exception e) {
            Log.e("DeletePhoto","threw:",e);
            Toast.makeText(view, "No photos to delete", Toast.LENGTH_LONG).show();
        }
    }

    public void scroll(View view, ImageView imageview, TextView timestamp,
                        EditText caption, EditText lat, EditText lon) {
        if (photos.size() == 0) {
            return;
        }
        int oldIndex = index;
        switch (view.getId()) {
            case R.id.navLeftBtn:
                if (index > 0) {
                    index--;
                }
                break;
            case R.id.navRightBtn:
                if (index < (photos.size() - 1)) {
                    index++;
                }
                break;
            default:
                break;
        }
        if (index != oldIndex) {
            displayPhoto(photos.get(index), imageview, timestamp, caption, lat, lon);
        }
    }

    private void displayPhoto(String filepath, ImageView imageview, TextView timestamp,
                              EditText caption, EditText lat, EditText lon) {
        //ImageView image = (ImageView) findViewById(R.id.galleryImage);
        //TextView timestamp = (TextView) findViewById(R.id.imageTimestamp);
        //EditText caption = (EditText) findViewById(R.id.editImageCaption);
        //EditText lat = (EditText) findViewById(R.id.latitudeDisplay);
        //EditText lon = (EditText) findViewById(R.id.longitudeDisplay);

        if (filepath == null || filepath.length() == 0) {
            imageview.setImageResource(R.mipmap.ic_launcher_round);
            imageview.setContentDescription("ic_launcher_round");
            caption.setText("");
            timestamp.setText("");
            lat.setText("");
            lon.setText("");
        } else {
            imageview.setImageBitmap(getOptimizedBitmap(filepath, imageview));
            imageview.setContentDescription(filepath);
            String[] photoData = filepath.split(DELIMITER);
            caption.setText(photoData[CAPTION_INDEX]);
            String timestampTxt = photoData[TIMESTAMP_INDEX];
            try {
                timestampTxt = displayFormat.format(storedFormat.parse(timestampTxt));
            } catch (ParseException e) {
            }
            timestamp.setText(timestampTxt);
            if ( photoData.length == MISSING_LATLON) {
                lat.setText("");
                lon.setText("");
            } else {
                lon.setText(photoData[LON_INDEX]);
                lat.setText(photoData[LAT_INDEX]);
            }
        }
    }

    private Bitmap getOptimizedBitmap(String filepath, ImageView imageView) {
        //Set image into image gallery
        //ImageView imageView = (ImageView) findViewById(R.id.galleryImage);

        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(filepath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        view.sendBroadcast(mediaScanIntent);
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();
        targetW = targetW == 0 ? DEFAULT_DIMENS : targetW;
        targetH = targetH == 0 ? DEFAULT_DIMENS : targetH;
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(filepath, bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.max(1, Math.min(photoW / targetW, photoH / targetH));

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        return BitmapFactory.decodeFile(filepath, bmOptions);
    }



}
