package com.example.photogalleryassignment;

import android.util.Log;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.test.espresso.contrib.PickerActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class UITest {
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    private void setDatetime(int year, int month, int day, int hour, int minute) {
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(year,month,day));
        onView(withText("OK")).perform(click());

        onView(withClassName(Matchers.equalTo(TimePicker.class.getName()))).perform(PickerActions.setTime(hour, minute));
        onView(withText("OK")).perform(click());
    }

    private int getCurrentTime(int dateUnitElement) {
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd/HH/mm/ss");
        String strDate = dateFormat.format(date);

        String[] tokens = strDate.split("/");

        return Integer.parseInt(tokens[dateUnitElement]);
    }

    @Test
    public void testSearch() {

        onView(withId(R.id.btn_search)).perform(click());

        onView(withId(R.id.etFromDateTime)).perform(click());
        setDatetime(
                getCurrentTime(0),
                getCurrentTime(1),
                getCurrentTime(2),
                getCurrentTime(3),
                getCurrentTime(4));


        onView(withId(R.id.etToDateTime)).perform(click());
        setDatetime(
                getCurrentTime(0),
                getCurrentTime(1),
                getCurrentTime(2),
                getCurrentTime(3),
                getCurrentTime(4));


        onView(withId(R.id.etKeywords)).perform(typeText("caption"), closeSoftKeyboard());

        onView(withId(R.id.go)).perform(click());
    }

}
