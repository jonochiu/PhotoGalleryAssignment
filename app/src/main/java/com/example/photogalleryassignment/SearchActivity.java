package com.example.photogalleryassignment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity {

    //Date Selector Code
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
            showCalendar(startDateListener);
        }
    };
    private View.OnClickListener endCalendarClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            showCalendar(endDateListener);
        }
    };
    private View.OnFocusChangeListener startCalendarChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if (hasFocus) {
                showCalendar(startDateListener);
            }
        }
    };
    private View.OnFocusChangeListener endCalendarChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if (hasFocus) {
                showCalendar(endDateListener);
            }
        }
    };
    private void showCalendar(DatePickerDialog.OnDateSetListener dateSetListener) {
        Calendar cal = Calendar.getInstance();
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
    private void setDate(EditText editText, int year, int month, int day) {
        month = month+1;
        String date = year+"-"+month+"-"+day;;
        editText.setText(date);
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
            DateFormat format = new SimpleDateFormat("yyyy‐MM‐dd");
            Date now = calendar.getTime();
            String todayStr = new SimpleDateFormat("yyyy‐MM‐dd", Locale.getDefault()).format(now);
            Date today = format.parse((String) todayStr);
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            String tomorrowStr = new SimpleDateFormat("yyyy‐MM‐dd", Locale.getDefault()).format(calendar.getTime());
            Date tomorrow = format.parse((String) tomorrowStr);
            ((EditText) findViewById(R.id.etFromDateTime)).setText(new SimpleDateFormat( "yyyy‐MM‐ddHH:mm:ss", Locale.getDefault()).format(today));
            ((EditText) findViewById(R.id.etToDateTime)).setText(new SimpleDateFormat( "yyyy‐MM‐ddHH:mm:ss", Locale.getDefault()).format(tomorrow));
        } catch (Exception ex) { }
    }

    public void go(final View v) {
        Intent i = new Intent();
        EditText from = (EditText) findViewById(R.id.etFromDateTime);
        EditText to = (EditText) findViewById(R.id.etToDateTime);
        EditText keywords = (EditText) findViewById(R.id.etKeywords);
        i.putExtra("STARTTIMESTAMP", from.getText() != null ? from.getText().toString() : "");
        i.putExtra("ENDTIMESTAMP", to.getText() != null ? to.getText().toString() : "");
        i.putExtra("KEYWORDS", keywords.getText() != null ?
        keywords.getText().toString() : "");
        setResult(RESULT_OK, i);
        finish();
    }

    public void cancel(final View v) {
        finish();
    }
}