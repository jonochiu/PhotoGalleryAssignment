package com.example.photogalleryassignment;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.contrib.PickerActions;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.espresso.matcher.ViewMatchers;
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
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class UITest {
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);
    @Rule
    public IntentsTestRule<MainActivity> intentsRule = new IntentsTestRule<>(MainActivity.class);

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

    @Test
    public void cameraSnapPhotoTest() {
        Activity activity = intentsRule.getActivity();
        String imgDesc1 = ((ImageView)activity.findViewById(R.id.galleryImage)).getContentDescription().toString();
        String timestamp1 = ((TextView)activity.findViewById(R.id.imageTimestamp)).getText().toString();
        String caption1 = ((EditText)activity.findViewById(R.id.editImageCaption)).getText().toString();

        Instrumentation.ActivityResult result = createImageCaptureActivityResultStub();
        intending(IntentMatchers.hasAction(MediaStore.ACTION_IMAGE_CAPTURE)).respondWith(result);

        onView(withId(R.id.btn_snap)).perform(click());
        onView(withId(R.id.galleryImage)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        String imgDesc2 = ((ImageView)activity.findViewById(R.id.galleryImage)).getContentDescription().toString();
        String timestamp2 = ((TextView)activity.findViewById(R.id.imageTimestamp)).getText().toString();
        String caption2 = ((EditText)activity.findViewById(R.id.editImageCaption)).getText().toString();

        assert !imgDesc1.equals(imgDesc2);
        assert !timestamp1.equals(timestamp2);
        assert !caption1.equals(caption2);
    }
    private Instrumentation.ActivityResult createImageCaptureActivityResultStub() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(MediaStore.EXTRA_OUTPUT, BitmapFactory.decodeResource(intentsRule.getActivity().getResources(), R.mipmap.ic_launcher));
        Intent resultData = new Intent();
        resultData.putExtras(bundle);
        return new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);
    }

}
