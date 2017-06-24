package com.faza.project.pencit.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.faza.project.pencit.ImageProcessing.ImageProcessing;
import com.faza.project.pencit.R;
import com.faza.project.pencit.Session.StaticBitmap;

import java.io.File;
import java.io.IOException;

/**
 * Dibuat oleh Faza Zulfika Permana Putra
 */

public class MainActivity extends AppCompatActivity {

    private final int GALLERY_REQUEST_CODE = 9165;
    private final int CAMERA_REQUEST_CODE = 9166;

    private RecyclerView rvHistory;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton btnGallery = (ImageButton) findViewById(R.id.btn_gallery);
        ImageButton btnCamera = (ImageButton) findViewById(R.id.btn_camera);
        ImageButton btnLevitation = (ImageButton) findViewById(R.id.btn_levitation);

        btnGallery.setOnClickListener(new GalleryClickListener());
        btnCamera.setOnClickListener(new CameraClickListener());
        btnLevitation.setOnClickListener(new LevitationClickListener());

        rvHistory = (RecyclerView) findViewById(R.id.rv_history);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap image = null;

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY_REQUEST_CODE || requestCode == CAMERA_REQUEST_CODE) {
                if (requestCode == GALLERY_REQUEST_CODE)
                    imageUri = data.getData();

                try {
                    image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                } catch (IOException e) {
                    Log.e("On Activity Result", e.getMessage());
                }

                moveIntent(image);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            openCamera();
    }

    private void moveIntent(Bitmap image) {
        ImageProcessing imageProcessing = new ImageProcessing();
        image = imageProcessing.decodeImage(image);

        try {
            StaticBitmap.image = image.copy(image.getConfig(), true);
            StaticBitmap.originalImg = image.copy(image.getConfig(), true);

            Intent intent = new Intent(this, ImageActivity.class);
            startActivity(intent);
        } catch (NullPointerException e) {
            String msg = getString(R.string.memory_lack_error);

            Log.e("NullPointerException", e.getMessage());
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();

            StaticBitmap.image = null;
            StaticBitmap.originalImg = null;
        }
    }

    private class GalleryClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent();

            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);

            startActivityForResult(Intent.createChooser(intent, "Select Image"), GALLERY_REQUEST_CODE);
        }
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
                return true;
            else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);

                return false;
            }
        } else
            return true;
    }

    private void openCamera() {
        ImageProcessing imageProcessing = new ImageProcessing();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (intent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;

            try {
                photoFile = imageProcessing.createImageFile();
            } catch (IOException e) {
                Log.e("CreateImageFile", e.getMessage());
            }

            if (photoFile != null) {
                imageUri = Uri.fromFile(photoFile);

                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            }
        }
    }

    private class CameraClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (checkPermission())
                openCamera();
        }
    }

    private class LevitationClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, LevitationActivity.class);
            startActivity(intent);
        }
    }
}