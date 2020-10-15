package com.example.photogalleryassignment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements ViewMain{
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int SEARCH_ACTIVITY_REQUEST_CODE = 2;

    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1252;
    public static boolean locationPermGranted = false;

    private String currentPhotoPath = null;

    public static PresenterMain presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ModelPhoto model = new ModelPhotoImpl();
        presenter = new PresenterMain(model);
        presenter.bind(this);
        presenter.ready();
    }

    //Added presenter
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                presenter.locPermGranted();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == SEARCH_ACTIVITY_REQUEST_CODE) {
            presenter.searchResponse(data);
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            presenter.takePictureResponse();
        }
    }

    @Override
    protected void onDestroy() {
        presenter.unbind();
        super.onDestroy();
    }

    @Override
    public void onDeletePhotoClick(View view) {
        presenter.delete();
    }

    @Override
    public void onScrollPhotosClick(View view) {
        presenter.scroll(view);
    }

    @Override
    public void onSaveCaptionClick(View view) {
        presenter.saveCaption();
    }

    @Override
    public void onSnapClick(View view) {
        presenter.takePicture();
    }

    @Override
    public void onBlogClick(View view){
        presenter.blog();
    }

    @Override
    public void onSearchClick(View view){
        presenter.search();
    }
}