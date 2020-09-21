package com.example.photogalleryassignment;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;

import androidx.appcompat.app.AppCompatActivity;

public class SearchActivity extends AppCompatActivity{

    private EditText startDateDispl;
    private EditText endDateDispl;
    private DatePickerDialog.OnDateSetListener startDateListener;
    private DatePickerDialog.OnDateSetListener endDateListener;
    private View.OnClickListener calendarClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            showCalendar();
        }
    };
    private View.OnFocusChangeListener calendarChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if (hasFocus) {
                showCalendar();
            }
        }
    };

    private void showCalendar() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                SearchActivity.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                startDateListener,
                year,month,day);
        dialog.getWindow().setBackgroundDrawable((new ColorDrawable(Color.TRANSPARENT)));
        dialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        startDateDispl = (EditText)findViewById(R.id.startDate);
        endDateDispl = (EditText)findViewById(R.id.endDate);

        //Listener for setting startdate
        startDateDispl.setOnClickListener(calendarClickListener);
        startDateDispl.setOnFocusChangeListener(calendarChangeListener);
        startDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month+1;
                String date = year+"-"+month+"-"+day;;
                startDateDispl.setText(date);
            }
        };

        //Listener for setting enddate
        endDateDispl.setOnClickListener(calendarClickListener);
        endDateDispl.setOnFocusChangeListener(calendarChangeListener);
        endDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month+1;
                String date = year+"-"+month+"-"+day;;
                endDateDispl.setText(date);
            }
        };
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
