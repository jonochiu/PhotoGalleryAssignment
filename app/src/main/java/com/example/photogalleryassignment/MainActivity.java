package com.example.photogalleryassignment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final String EXTRA_PHOTO_DATA = "EXTRA_PHOTO_DATA";
    private String currentPhotoPath = null;
    private SharedPreferences storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setImageGallery();
        storage = getApplicationContext().getSharedPreferences(EXTRA_PHOTO_DATA, Context.MODE_PRIVATE);
    }

    public void setImageGallery(){
        //Set image into image gallery
        if (currentPhotoPath == null) {
            return;
        }
        ImageView imageView= (ImageView) findViewById(R.id.galleryImage);
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(currentPhotoPath, bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.max(1, Math.min(photoW/targetW, photoH/targetH));

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        imageView.setImageBitmap(bitmap);
    }

    //Handles image after capture from camera intent
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImageView img = (ImageView) findViewById(R.id.galleryImage);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                img.setImageBitmap(imageBitmap);
            } else {
                setImageGallery();
            }

            String newPhotoFilepath = currentPhotoPath;
            PhotoEntry photoEntry = new PhotoEntry("", newPhotoFilepath, new Date());
            ((TextView)findViewById(R.id.imageTimestamp)).setText(photoEntry.getTimestamp().toString());
            ((TextView)findViewById(R.id.editImageCaption)).setText(photoEntry.getCaption());
            savePhotoEntry(photoEntry.getFilepath(), photoEntry.getCaption());
        }
    }

    private void savePhotoEntry(String filepath, String caption) {
        SharedPreferences.Editor editor = storage.edit();
        editor.putString(filepath, caption);
        editor.apply();
    }

    public void saveCaption(View view) {
        String newCaption = (String) ((TextView)findViewById(R.id.editImageCaption)).getText().toString();
        String savedCaption = storage.getString(currentPhotoPath, null);
        if (savedCaption != null && !newCaption.equals(savedCaption)) {
            savePhotoEntry(currentPhotoPath, newCaption);
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        //set image name
        String imageFileName = "JPEG_" + timeStamp + "_";
        //retrieve image from storage
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
    public void onLeftClick(View view){

    }
    public void onRightClick(View view){

    }

    public void onSearchClick(View view){
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }
}