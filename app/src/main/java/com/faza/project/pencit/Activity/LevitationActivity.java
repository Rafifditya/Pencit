package com.faza.project.pencit.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.faza.project.pencit.ImageProcessing.ImageProcessing;
import com.faza.project.pencit.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class LevitationActivity extends AppCompatActivity {

    private final int WRITE_REQUEST_CODE = 9164;
    private final int IMG_BACKGROUND_CODE = 9165;
    private final int IMG_WITHOUT_ITEM_CODE = 9166;
    private final int IMG_WITH_ITEM_CODE = 9167;

    private Bitmap imgDefault, imgBackground, imgWithoutItem, imgWithItem, imgLevitation;
    private ImageProcessing imageProcessing;
    private ImageView ivImgBackground, ivImgWithoutItem, ivImgWithItem, ivImgLevitation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levitation);

        Toolbar tbLevitation = (Toolbar) findViewById(R.id.tb_levitation);
        setSupportActionBar(tbLevitation);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        imageProcessing = new ImageProcessing();
        imgDefault = BitmapFactory.decodeResource(getResources(), R.drawable.image_not_found);
        imgLevitation = null;

        ivImgBackground = (ImageView) findViewById(R.id.iv_img_background);
        ivImgWithoutItem = (ImageView) findViewById(R.id.iv_img_without_item);
        ivImgWithItem = (ImageView) findViewById(R.id.iv_img_with_item);
        ivImgLevitation = (ImageView) findViewById(R.id.iv_img_levitation);

        ImageButton btnImgBackground = (ImageButton) findViewById(R.id.btn_img_background);
        ImageButton btnImgWithoutItem = (ImageButton) findViewById(R.id.btn_img_without_item);
        ImageButton btnImgWithItem = (ImageButton) findViewById(R.id.btn_img_with_item);
        ImageButton btnImgLevitation = (ImageButton) findViewById(R.id.btn_img_levitation);
        ImageButton btnImgSave = (ImageButton) findViewById(R.id.btn_img_save);

        btnImgBackground.setOnClickListener(new AddImageClickListener(IMG_BACKGROUND_CODE));
        btnImgWithoutItem.setOnClickListener(new AddImageClickListener(IMG_WITHOUT_ITEM_CODE));
        btnImgWithItem.setOnClickListener(new AddImageClickListener(IMG_WITH_ITEM_CODE));
        btnImgLevitation.setOnClickListener(new LevitationClickListener());
        btnImgSave.setOnClickListener(new SaveClickListener());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap image;

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == IMG_BACKGROUND_CODE || requestCode == IMG_WITHOUT_ITEM_CODE || requestCode == IMG_WITH_ITEM_CODE) {
                Uri imageUri = data.getData();

                try {
                    image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    image = imageProcessing.decodeImage(image);
                    image = image.copy(image.getConfig(), true);

                    switch (requestCode) {
                        case IMG_BACKGROUND_CODE:
                            imgBackground = image;
                            ivImgBackground.setImageBitmap(imgBackground);
                            break;
                        case IMG_WITHOUT_ITEM_CODE:
                            imgWithoutItem = image;
                            ivImgWithoutItem.setImageBitmap(imgWithoutItem);
                            break;
                        case IMG_WITH_ITEM_CODE:
                            imgWithItem = image;
                            ivImgWithItem.setImageBitmap(imgWithItem);
                            break;
                    }
                } catch (IOException e) {
                    image = imgDefault;

                    Log.e("On Activity Result", e.getMessage());
                    Toast.makeText(this, "Cannot load image...", Toast.LENGTH_LONG).show();

                    switch (requestCode) {
                        case IMG_BACKGROUND_CODE:
                            imgBackground = null;
                            ivImgBackground.setImageBitmap(image);
                            break;
                        case IMG_WITHOUT_ITEM_CODE:
                            imgWithoutItem = null;
                            ivImgWithoutItem.setImageBitmap(image);
                            break;
                        case IMG_WITH_ITEM_CODE:
                            imgWithItem = null;
                            ivImgWithItem.setImageBitmap(image);
                            break;
                    }
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == WRITE_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            saveImage();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.levitation_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_levitation_info:
                createInfoDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void createInfoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        String title = getString(R.string.levitation_info);
        String msg = getString(R.string.levitation_info_msg);

        builder.setTitle(title)
                .setMessage(msg);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                return true;
            else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_REQUEST_CODE);

                return false;
            }
        } else
            return true;
    }

    private void saveImage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String success = getString(R.string.success);
        String failed = getString(R.string.failed);
        String ok = getString(R.string.ok);
        String msg;

        if (checkPermission()) {
            try {
                File file = imageProcessing.createImageFile();

                OutputStream outStream = new FileOutputStream(file);
                imgLevitation.compress(Bitmap.CompressFormat.PNG, 100, outStream);

                outStream.flush();
                outStream.close();

                msg = getString(R.string.image_saved);
                builder.setTitle(success)
                        .setMessage(msg)
                        .setNegativeButton(ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
            } catch (IOException e) {
                Log.e("Save Image", e.getMessage());

                msg = getString(R.string.failed_saved_image);
                builder.setTitle(failed)
                        .setMessage(msg)
                        .setNegativeButton(ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
            }

            AlertDialog dialog = builder.create();

            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
    }

    private class AddImageClickListener implements View.OnClickListener {

        private int requestCode;

        private AddImageClickListener(int requestCode) {
            this.requestCode = requestCode;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent();

            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);

            startActivityForResult(Intent.createChooser(intent, "Select Image"), requestCode);
        }
    }

    private class LevitationClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (imgBackground == null || imgWithoutItem == null || imgWithItem == null) {
                imgLevitation = null;
                ivImgLevitation.setImageBitmap(imgDefault);

                Toast.makeText(LevitationActivity.this, "Cannot proceed image...", Toast.LENGTH_LONG).show();
            } else {
                imgLevitation = Bitmap.createBitmap(imgBackground.getWidth(), imgBackground.getHeight(), imgBackground.getConfig());

                imageProcessing.getLevitationImage(imgBackground, imgWithoutItem, imgWithItem, imgLevitation);
                ivImgLevitation.setImageBitmap(imgLevitation);
            }
        }
    }

    private class SaveClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (imgLevitation == null)
                Toast.makeText(LevitationActivity.this, "Cannot save image...", Toast.LENGTH_LONG).show();
            else
                saveImage();
        }
    }
}
