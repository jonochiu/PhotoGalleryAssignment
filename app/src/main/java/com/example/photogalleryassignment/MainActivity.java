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
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int SEARCH_ACTIVITY_REQUEST_CODE = 2;
    private String currentPhotoPath = null;
    private List<String> photos = null;
    private int index = 0;
    private static final String DELIMITER = "\r";//null char, impossible char to type on keyboard
    private static final int SYS_PATH_INDEX = 0;
    private static final int CAPTION_INDEX = 1;
    private static final int TIMESTAMP_INDEX = 2;
    private static final int SUFFIX_INDEX = 3;
    private static final int DEFAULT_DIMENS = 250;
    public static DateFormat displayFormat;
    private static DateFormat storedFormat;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        displayFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        storedFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());

        photos = findPhotos(new Date(Long.MIN_VALUE), new Date(), "");
        if (photos.size() == 0) {
            displayPhoto(null);
        } else {
            displayPhoto(photos.get(index));
        }
    }

    private List<String> findPhotos(Date startTimestamp, Date endTimestamp, String keywords) {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),
                "/Android/data/" + getApplicationContext().getPackageName() + "/files/Pictures");
        List<String> photos = new ArrayList<>();
        File[] fList = file.listFiles();
        if (fList != null) {
            for (File f : fList) {
                boolean isUndated = startTimestamp == null && endTimestamp == null;
                boolean isWithinDateRange = f.lastModified() >= startTimestamp.getTime() && f.lastModified() <= endTimestamp.getTime();
                boolean isKeywordEmpty = keywords == null;
                boolean isKeywordMatch = f.getPath().contains(keywords);
                if ((isUndated || isWithinDateRange) && (isKeywordEmpty || isKeywordMatch)) {
                    photos.add(f.getPath());
                }
            }
        }
        return photos;
    }

    public void scrollPhotos(View view) {
        if (photos.size() == 0) {
            return;
        }
        updatePhoto(photos.get(index), ((EditText) findViewById(R.id.editImageCaption)).getText().toString());
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
            displayPhoto(photos.get(index));
        }
    }

    private void displayPhoto(String filepath) {
        ImageView image = (ImageView) findViewById(R.id.galleryImage);
        TextView timestamp = (TextView) findViewById(R.id.imageTimestamp);
        EditText caption = (EditText) findViewById(R.id.editImageCaption);

        if (filepath == null || filepath.length() == 0) {
            image.setImageResource(R.mipmap.ic_launcher_round);
            image.setContentDescription("ic_launcher_round");
            caption.setText("");
            timestamp.setText("");
        } else {
            image.setImageBitmap(getOptimizedBitmap(filepath));
            image.setContentDescription(filepath);
            String[] photoData = filepath.split(DELIMITER);
            caption.setText(photoData[CAPTION_INDEX]);
            String timestampTxt = photoData[TIMESTAMP_INDEX];
            try {
                timestampTxt = displayFormat.format(storedFormat.parse(timestampTxt));
            } catch (ParseException e) {
            }
            timestamp.setText(timestampTxt);
        }
    }

    private Bitmap getOptimizedBitmap(String filepath) {
        //Set image into image gallery
        ImageView imageView = (ImageView) findViewById(R.id.galleryImage);
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(filepath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
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

    //Handles image after capture from camera intent
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        displayPhoto(null);
        if (requestCode == SEARCH_ACTIVITY_REQUEST_CODE) {
            DateFormat format = displayFormat;
            Date startTimestamp, endTimestamp;
            try {
                String from = (String) data.getStringExtra("STARTTIMESTAMP");
                String to = (String) data.getStringExtra("ENDTIMESTAMP");
                startTimestamp = format.parse(from);
                endTimestamp = format.parse(to);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            String keywords = (String) data.getStringExtra("KEYWORDS");
            index = 0;
            photos = findPhotos(startTimestamp, endTimestamp, keywords);

            displayPhoto(photos.size() == 0 ? null : photos.get(index));
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            photos = findPhotos(new Date(Long.MIN_VALUE), new Date(), "");
            displayPhoto(currentPhotoPath);
        }
    }

    public void saveCaption(View view) {
        if (photos.size() > 0) {
            updatePhoto(photos.get(index), ((EditText) findViewById(R.id.editImageCaption)).getText().toString());
        }
    }

    private void updatePhoto(String filepath, String caption) {
        String[] data = filepath.split(DELIMITER);
        //only make updates if caption has changed
        if (!data[CAPTION_INDEX].equals(caption)) {
            File from = new File(filepath);
            File to = new File(data[SYS_PATH_INDEX] + DELIMITER + caption + DELIMITER + data[TIMESTAMP_INDEX] + DELIMITER + data[SUFFIX_INDEX]);
            boolean success = from.renameTo(to);
            if (success) {
                photos.set(index, to.getAbsolutePath());
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void getPhotoLocation(){
        final TextView longitudeText = (TextView) findViewById(R.id.longitudeDisplay);
        final TextView latitudeText = (TextView) findViewById(R.id.latitudeDisplay);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            String longitude = Double.toString(location.getLongitude());
                            String latitude = Double.toString(location.getLatitude());
                            longitudeText.setText(longitude);
                            latitudeText.setText(latitude);
                        }
                    }

                });
    }
    private File createImageFile() throws IOException {
        String timeStamp = storedFormat.format(new Date());
        getPhotoLocation();
        EditText longitude = (EditText)findViewById(R.id.longitudeDisplay);
        longitude.getText().toString();
        EditText latitude = (EditText)findViewById(R.id.latitudeDisplay);
        latitude.getText().toString();
        // filepath format: /storage/...DELIMITERcaption>DELIMITERtimeStampDELIMITER.jpg
        final String imageFileName = DELIMITER + "caption" + DELIMITER + timeStamp + DELIMITER + longitude + DELIMITER + latitude + DELIMITER;



        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void onSnapClick(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File, photoFile should still be null
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    public void onSearchClick(View view){
        Intent intent = new Intent(this, SearchActivity.class);
        startActivityForResult(intent, SEARCH_ACTIVITY_REQUEST_CODE);
    }
}