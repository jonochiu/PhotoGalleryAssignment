package com.example.photogalleryassignment;

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
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;
import android.view.View;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class PresenterMain {
    MainActivity view;
    private ModelPhoto model;
    private List<String> photos;
    private int index = 0;


    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int SEARCH_ACTIVITY_REQUEST_CODE = 2;

    private static final int DEFAULT_DIMENS = 250;
    private static final String DELIMITER = "\r";//null char, impossible char to type on keyboard
    private static final int SYS_PATH_INDEX = 0;
    private static final int CAPTION_INDEX = 1;//
    private static final int TIMESTAMP_INDEX = 2;//
    public static DateFormat displayFormat; //
    private static DateFormat storedFormat; //
    private static final int LON_INDEX = 3;
    private static final int LAT_INDEX = 4;
    //filename.split(delimiter) == 4 -> no lat lon
    private static final int MISSING_LATLON = 4;//

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1252;


    public static boolean locationPermGranted = false;
    private FusedLocationProviderClient fusedLocationClient;



    private String currentPhotoPath = null;

    public PresenterMain(ModelPhoto model) {
        this.model = model;
    }

    public void bind(MainActivity view) {
        this.view = view;
    }

    public void unbind() {
        view = null;
    }

    public void ready() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(view); //need to add this to presentermain
        displayFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        storedFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        photos = model.findPhotos(view, new Date(Long.MIN_VALUE), new Date(), "", 0, 0);

        if (photos.size() == 0) {
            displayPhoto(null);
        } else {
            displayPhoto(photos.get(index));
        }

        if (ActivityCompat.checkSelfPermission(view, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= 23) { // Marshmallow
                ActivityCompat.requestPermissions(view, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            }
            //permission denied && SDK < 23, not sure wat to do in this case
        } else {
            locationPermGranted = true; //need function to set presentermain location to true
            //presenter.setLocationPermGranted();
        }
    }


    public void delete() {
        try {
            //add code to access model to delete
            File imagePath = new File(photos.get(index));
            if(imagePath.delete()) {
                Toast.makeText(view, "Deleted the file: " + imagePath.getName(), Toast.LENGTH_LONG).show();
                photos.remove(index);
                if (photos.size() == 0) {
                    displayPhoto(null);
                } else {
                    index = 0;
                    displayPhoto(photos.get(index));
                }
            }
            //block to put into modelphotoimpl


        } catch (Exception e) {
            Log.e("DeletePhoto","threw:",e);
            Toast.makeText(view, "No photos to delete", Toast.LENGTH_LONG).show();
        }
    }

    public void scroll(View view) {
        if (photos.size() == 0) {
           // return index; //fix here
        }
        int oldIndex = index;
        System.out.println(view.getId());
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
        //return index; //fix here
    }

    public void blog() {
        //twitter
        Uri photo = Uri.parse(photos.get(index));
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, photo);
        shareIntent.setType("image/*");
        view.startActivity(Intent.createChooser(shareIntent, "Share your thoughts"));
    }

    private void displayPhoto(String filepath) {
        ImageView imageview = (ImageView) view.findViewById(R.id.galleryImage);
        TextView timestamp = (TextView) view.findViewById(R.id.imageTimestamp);
        EditText caption = (EditText) view.findViewById(R.id.editImageCaption);
        EditText lat = (EditText) view.findViewById(R.id.latitudeDisplay);
        EditText lon = (EditText) view.findViewById(R.id.longitudeDisplay);

        if (filepath == null || filepath.length() == 0) {
            imageview.setImageResource(R.mipmap.ic_launcher_round);
            imageview.setContentDescription("ic_launcher_round");
            caption.setText("");
            timestamp.setText("");
            lat.setText("");
            lon.setText("");
        } else {
            imageview.setImageBitmap(getOptimizedBitmap(filepath));
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

    public void  saveCaption() {
        if (photos.size() > 0) {
            String captions = ((EditText) view.findViewById(R.id.editImageCaption)).getText().toString();
            String lon = ((EditText) view.findViewById(R.id.longitudeDisplay)).getText().toString();
            String lat = ((EditText) view.findViewById(R.id.latitudeDisplay)).getText().toString();
            updatePhoto(photos.get(index), captions, lon, lat);
            Toast.makeText(view.getApplicationContext(), "File saved", Toast.LENGTH_SHORT).show();
        }
    }

    public void takePicture() {
        Log.d("Photo", "onsnapclick");
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(view.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                Log.d("Photo", "creating image");
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.d("Photo", "error");
                // Error occurred while creating the File, photoFile should still be null
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(view,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                view.startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    public void takePictureResponse() {
        String longitude = ((EditText)view.findViewById(R.id.longitudeDisplay)).getText().toString();
        String latitude = ((EditText)view.findViewById(R.id.latitudeDisplay)).getText().toString();
        //rename the new file to have lat lon value
        currentPhotoPath = updatePhoto(currentPhotoPath, "caption", longitude, latitude);

        //photos = findPhotos(new Date(Long.MIN_VALUE), new Date(), "", 0, 0);
        photos = model.findPhotos(view, new Date(Long.MIN_VALUE), new Date(), "", 0, 0);
        displayPhoto(currentPhotoPath);
    }

    public void search() {
        Intent intent = new Intent(view, SearchActivity.class);
        view.startActivityForResult(intent, SEARCH_ACTIVITY_REQUEST_CODE);
    }

    public void searchResponse(Intent data) {
        DateFormat format = displayFormat;
        Date startTimestamp, endTimestamp;
        int lon = data.getIntExtra("LONGITUDE", 0);
        int lat = data.getIntExtra("LATITUDE", 0);
        String from = (String) data.getStringExtra("STARTTIMESTAMP");
        String to = (String) data.getStringExtra("ENDTIMESTAMP");
        try {
            startTimestamp = format.parse(from);
            endTimestamp = format.parse(to);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String keywords = (String) data.getStringExtra("KEYWORDS");
        index = 0;
        photos = model.findPhotos(view, startTimestamp, endTimestamp, keywords, lon, lat); //refresh with photos array with the searched photos

        displayPhoto(photos.size() == 0 ? null : photos.get(index)); //display the first photo
    }

    public void locPermGranted() {
        locationPermGranted = true;
    }

    @SuppressLint("MissingPermission")
    private void setLocationFieldsAsync() {
        Log.d("Photo", "getting location");
        final TextView longitudeText = (TextView) view.findViewById(R.id.longitudeDisplay);
        final TextView latitudeText = (TextView) view.findViewById(R.id.latitudeDisplay);
        if (locationPermGranted) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(view, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                Log.d("Photo", "location found");
                                String longitude = Double.toString(location.getLongitude());
                                String latitude = Double.toString(location.getLatitude());
                                longitudeText.setText(longitude);
                                latitudeText.setText(latitude);
                            }
                        }
                    });
        }
        Log.d("Photo", "location complete");
    }

    private String updatePhoto(String filepath, String caption, String lon, String lat) {
        //we dont care if the original photo had lat lon, we can add that info now
        lon = lon.trim();
        lat = lat.trim();
        String[] data = filepath.split(DELIMITER);
        File from = new File(filepath);
        File to = new File(data[SYS_PATH_INDEX] + DELIMITER + caption + DELIMITER + data[TIMESTAMP_INDEX] + DELIMITER + lon + DELIMITER + lat+ DELIMITER + data[data.length-1]);
        boolean success = from.renameTo(to);
        if (success && photos.size() > 0) {
            photos.set(index, to.getAbsolutePath());
        }
        return success ? to.getAbsolutePath() : filepath;
    }

    private File createImageFile() throws IOException {
        Log.d("Photo", "oncreating image");
        String timeStamp = storedFormat.format(new Date());
        // location data will not be ready before file is created, will need to rename
        setLocationFieldsAsync();
        // filepath format: /storage/...DELIMITERcaptionDELIMITERtimeStampDELIMITERsomegarbage.jpg
        String imageFileName = DELIMITER + "caption" + DELIMITER + timeStamp + DELIMITER;

        File storageDir = view.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private Bitmap getOptimizedBitmap(String filepath) {
        //Set image into image gallery
        ImageView imageView = (ImageView) view.findViewById(R.id.galleryImage);

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

    public DateFormat getDisplayFormat() {
        return displayFormat;
    }
}
