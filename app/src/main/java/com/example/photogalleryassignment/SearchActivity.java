package com.example.photogalleryassignment;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class SearchActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
//        String[] projection = new String[] {
//                MediaStore.Images.Media._ID,
//                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
//                MediaStore.Images.Media.DATE_TAKEN
//        };
//        Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//        Cursor cur = getContentResolver().query(images, projection, null, null, null);
//        Log.i("ListingImages"," query count=" + cur.getCount());
//
//        if (cur.moveToFirst()) {
//            String bucket;
//            String date;
//            int bucketColumn = cur.getColumnIndex(
//                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
//
//            int dateColumn = cur.getColumnIndex(
//                    MediaStore.Images.Media.DATE_TAKEN);
//
//            do {
//                // Get the field values
//                bucket = cur.getString(bucketColumn);
//                date = cur.getString(dateColumn);
//
//                // Do something with the values.
//                Log.i("ListingImages", " bucket=" + bucket
//                        + "  date_taken=" + date);
//            } while (cur.moveToNext());
//
//        }
    }

}
