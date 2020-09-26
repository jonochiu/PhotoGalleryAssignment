package com.example.photogalleryassignment;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.test.InstrumentationRegistry;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.espresso.matcher.HasBackgroundMatcher;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class CameraTest {
    @Rule
    public IntentsTestRule<MainActivity> intentsRule =
            new IntentsTestRule<>(MainActivity.class);

    @Test
    public void validateCameraScenario() {
        Bitmap icon = BitmapFactory.decodeResource(
                InstrumentationRegistry.getTargetContext().getResources(),
                R.mipmap.ic_launcher);

        Intent resultData = new Intent();
        resultData.putExtra("data", icon);
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);

        intending(toPackage("com.android.camera2")).respondWith(result);
        onView(withId(R.id.btn_snap)).perform(click());
        intended(toPackage("com.android.camera2"));
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
