package com.example.photogalleryassignment;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity {

    //Date Selector Code
    private static DateFormat displayFormat = MainActivity.presenter.getDisplayFormat();

    private EditText startDateDispl;
    private EditText endDateDispl;
    private DatePickerDialog.OnDateSetListener startDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            setDate(startDateDispl, year, month, day);
        }
    };
    private DatePickerDialog.OnDateSetListener endDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            setDate(endDateDispl, year, month, day);
        }
    };
    private View.OnClickListener startCalendarClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            showCalendar(startDateDispl, startDateListener);

        }
    };
    private View.OnClickListener endCalendarClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            showCalendar(endDateDispl, endDateListener);

        }
    };
    private View.OnFocusChangeListener startCalendarChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if (hasFocus) {
                showCalendar(startDateDispl, startDateListener);

            }
        }
    };
    private View.OnFocusChangeListener endCalendarChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if (hasFocus) {

                showCalendar(endDateDispl,endDateListener);
            }
        }
    };
    private Calendar dateStringToCalendar(String date) {
        Date selectedDate = new Date();
        try {
            selectedDate = displayFormat.parse(date);
        } catch (ParseException e) { }
        Calendar cal = Calendar.getInstance();
        cal.setTime(selectedDate);
        return cal;
    }
    private void showCalendar(EditText view, DatePickerDialog.OnDateSetListener dateSetListener) {
        Calendar cal = dateStringToCalendar(view.getText().toString());

        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                SearchActivity.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                dateSetListener,
                year,month,day);
        dialog.getWindow().setBackgroundDrawable((new ColorDrawable(Color.TRANSPARENT)));
        dialog.show();
    }

    private void setDate(final EditText editText, int year, int month, int day) {
        month++;
        final String date = String.format(Locale.getDefault(), "%d-%02d-%02d", year, month, day);
        Calendar cal = dateStringToCalendar(editText.getText().toString());
        TimePickerDialog subDialog = new TimePickerDialog(
            editText.getContext(),
            new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hour, int min) {
                    String datetime = String.format(Locale.getDefault(), "%s %02d:%02d:00", date, hour, min);
                    editText.setText(datetime);
                }
            },
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            false
        );
        subDialog.show();
        //hide keyboard
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getApplicationWindowToken(), 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        startDateDispl = (EditText)findViewById(R.id.etFromDateTime);
        endDateDispl = (EditText)findViewById(R.id.etToDateTime);

        //Listener for setting startdate
        startDateDispl.setOnClickListener(startCalendarClickListener);
        startDateDispl.setOnFocusChangeListener(startCalendarChangeListener);

        //Listener for setting enddate
        endDateDispl.setOnClickListener(endCalendarClickListener);
        endDateDispl.setOnFocusChangeListener(endCalendarChangeListener);


        try {
            Calendar calendar = Calendar.getInstance();
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date now = calendar.getTime();
            String todayStr = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(now);
            Date today = format.parse((String) todayStr);
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            String tomorrowStr = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());
            Date tomorrow = format.parse((String) tomorrowStr);
            ((EditText) findViewById(R.id.etFromDateTime)).setText(displayFormat.format(today));
            ((EditText) findViewById(R.id.etToDateTime)).setText(displayFormat.format(tomorrow));
        } catch (Exception ex) { }

    }

    public void go(final View v) {
        Intent i = new Intent();
        EditText from = (EditText) findViewById(R.id.etFromDateTime);
        EditText to = (EditText) findViewById(R.id.etToDateTime);
        EditText keywords = (EditText) findViewById(R.id.etKeywords);
        EditText longitude = (EditText) findViewById(R.id.longitudeSearch);
        EditText latitude = (EditText) findViewById(R.id.latitudeSearch);
        i.putExtra("STARTTIMESTAMP", from.getText() != null ? from.getText().toString() : "");
        i.putExtra("ENDTIMESTAMP", to.getText() != null ? to.getText().toString() : "");
        i.putExtra("KEYWORDS", keywords.getText() != null ?
        keywords.getText().toString() : "");
        i.putExtra("LONGITUDE", longitude.getText() != null ? longitude.getText().toString() : "" );
        i.putExtra("LATITUDE", latitude.getText() != null ? latitude.getText().toString() : "" );
        setResult(RESULT_OK, i);
        finish();
    }

    public void cancel(final View v) {
        finish();
    }
}