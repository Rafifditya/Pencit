package com.faza.project.pencit.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.faza.project.pencit.Adapter.ImgAdapter;
import com.faza.project.pencit.ImageProcessing.ImageProcessing;
import com.faza.project.pencit.R;
import com.faza.project.pencit.Session.StaticBitmap;
import com.faza.project.pencit.Thread.PatternMatchingThread;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Dibuat oleh Faza Zulfika Permana Putra
 */

public class ImageActivity extends AppCompatActivity {

    private final int WRITE_REQUEST_CODE = 9165;
    private final int GALLERY_REQUEST_CODE = 9166;

    private FrameLayout frameImg;
    private ImgAdapter adapter;
    private ImageProcessing imageProcessing;
    private ImageView ivImg;
    private String title;
    private TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        title = getString(R.string.app_name);
        String[] menu = getResources().getStringArray(R.array.menu);

        Toolbar tbImg = (Toolbar) findViewById(R.id.tb_img);
        setSupportActionBar(tbImg);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvTitle.setText(title);

        frameImg = (FrameLayout) findViewById(R.id.frame_img);
        ivImg = (ImageView) findViewById(R.id.iv_img);

        if (StaticBitmap.image != null)
            ivImg.setImageBitmap(StaticBitmap.image);
        else
            finish();

        imageProcessing = new ImageProcessing();

        TabLayout tlImgMenu = (TabLayout) findViewById(R.id.tl_img_menu);
        tlImgMenu.setOnTabSelectedListener(new MenuSelectedListener());

        for (String m : menu)
            tlImgMenu.addTab(tlImgMenu.newTab().setText(m));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap image = null;

        if (resultCode == Activity.RESULT_OK && requestCode == GALLERY_REQUEST_CODE) {
            Uri imageUri = data.getData();

            try {
                image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                image = image.copy(image.getConfig(), true);
            } catch (IOException e) {
                Log.e("On Activity Result", e.getMessage());
            }

            if (image != null) {
                String dialogText = getString(R.string.matching_dialog);
                ProgressDialog dialog = new ProgressDialog(this);

                dialog.setMessage(dialogText);
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();

                PatternMatchingThread patternMatchingThread = new PatternMatchingThread(dialog, ivImg);
                patternMatchingThread.execute(StaticBitmap.image, image);
            } else
                Toast.makeText(ImageActivity.this, "Cannot load image", Toast.LENGTH_SHORT).show();
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
        inflater.inflate(R.menu.img_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_histogram:
                gotoHistogram();
                return true;
//            case R.id.menu_matching:
//                getMathingImage();
//                return true;
            case R.id.menu_save:
                saveImage();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setMenuFilter(String[] menu) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View menuView = inflater.inflate(R.layout.frame_filter, frameImg, false);

        adapter = new ImgAdapter(this, menu, ivImg, tvTitle);

        RecyclerView rvOption = (RecyclerView) menuView.findViewById(R.id.rv_img);
        rvOption.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvOption.setAdapter(adapter);

        ImageButton btnCheck = (ImageButton) menuView.findViewById(R.id.btn_check);
        ImageButton btnClose = (ImageButton) menuView.findViewById(R.id.btn_close);

        btnCheck.setOnClickListener(new CheckClickListener());
        btnClose.setOnClickListener(new CloseClickListener());

        frameImg.addView(menuView);
    }

    private void setMenuEnhance(String title, int defaultProgress, int maxProgress) {
        String rotation = getString(R.string.rotate);
        String brightness = getString(R.string.brightness);
        String contrast = getString(R.string.contrast);
        String gaussian = getString(R.string.noise_gaussian);
        String speckle = getString(R.string.noise_speckle);
        String saltPepper = getString(R.string.noise_saltpepper);

        LayoutInflater inflater = LayoutInflater.from(this);
        View menuView = inflater.inflate(R.layout.frame_enhance, frameImg, false);

        TextView tvBrightness = (TextView) menuView.findViewById(R.id.tv_title);
        tvBrightness.setText(title);

        SeekBar sbEnhance = (SeekBar) menuView.findViewById(R.id.sb_enhance);
        sbEnhance.setMax(maxProgress);
        sbEnhance.setProgress(defaultProgress);

        if (title.equals(rotation))
            sbEnhance.setOnSeekBarChangeListener(new RotationChangeListener());
        else if (title.equals(brightness))
            sbEnhance.setOnSeekBarChangeListener(new BrightnessChangeListener());
        else if (title.equals(contrast))
            sbEnhance.setOnSeekBarChangeListener(new ContrastChangeListener());
        else if (title.equals(gaussian))
            sbEnhance.setOnSeekBarChangeListener(new GaussianChangeListener());
        else if (title.equals(speckle))
            sbEnhance.setOnSeekBarChangeListener(new SpeckleChangeListener());
        else if (title.equals(saltPepper))
            sbEnhance.setOnSeekBarChangeListener(new SaltPepperChangeListener());

        ImageButton btnCheck = (ImageButton) menuView.findViewById(R.id.btn_check);
        ImageButton btnClose = (ImageButton) menuView.findViewById(R.id.btn_close);

        btnCheck.setOnClickListener(new CheckClickListener(sbEnhance, defaultProgress));
        btnClose.setOnClickListener(new CloseClickListener(sbEnhance, defaultProgress));

        frameImg.addView(menuView);
    }

    private void setMenu1() { // Flip
        String[] menu = getResources().getStringArray(R.array.menu1);
        setMenuFilter(menu);
    }

    private void setMenu2() {
        String[] menu = getResources().getStringArray(R.array.menu2);
        setMenuFilter(menu);
    }

    private void setMenu3() {
        String[] menu = getResources().getStringArray(R.array.menu3);
        setMenuFilter(menu);
    }

    private void setMenu4() {
        String[] menu = getResources().getStringArray(R.array.menu4);
        setMenuFilter(menu);
    }

    private void setMenu5() {
        String[] menu = getResources().getStringArray(R.array.menu5);
        setMenuFilter(menu);
    }

    private void setMenu6() {
        String[] menu = getResources().getStringArray(R.array.menu6);
        setMenuFilter(menu);
    }

    private void setMenu7() {
        String[] menu = getResources().getStringArray(R.array.menu7);
        setMenuFilter(menu);
    }

    private void setMenu8() {
        String[] menu = getResources().getStringArray(R.array.menu8);
        setMenuFilter(menu);
    }

    private void setMenu9() {
        int defaultProgress = 0;
        int maxProgress = 360;
        String rotate = getString(R.string.rotate);

        setMenuEnhance(rotate, defaultProgress, maxProgress);
    }

    private void setMenu10() {
        int defaultProgress = 128;
        int maxProgress = 255;
        String brightness = getString(R.string.brightness);

        setMenuEnhance(brightness, defaultProgress, maxProgress);
    }

    private void setMenu11() {
        int defaultProgress = 20;
        int maxProgress = 40;
        String contrast = getString(R.string.contrast);

        setMenuEnhance(contrast, defaultProgress, maxProgress);
    }

    private void setMenu12() {
        int defaultProgress = 0;
        int maxProgress = 100;
        String gaussian = getString(R.string.noise_gaussian);

        setMenuEnhance(gaussian, defaultProgress, maxProgress);
    }

    private void setMenu13() {
        int defaultProgress = 0;
        int maxProgress = 100;
        String speckle = getString(R.string.noise_speckle);

        setMenuEnhance(speckle, defaultProgress, maxProgress);
    }

    private void setMenu14() {
        int defaultProgress = 0;
        int maxProgress = 100;
        String saltPepper = getString(R.string.noise_saltpepper);

        setMenuEnhance(saltPepper, defaultProgress, maxProgress);
    }

    private void gotoHistogram() {
        Intent intent = new Intent(this, HistogramActivity.class);
        intent.putExtra("Title", tvTitle.getText().toString());

        startActivity(intent);
    }

    private void getMathingImage() {
        Intent intent = new Intent();

        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent, "Select Image"), GALLERY_REQUEST_CODE);
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
        ImageProcessing imageProcessing = new ImageProcessing();
        String success = getString(R.string.success);
        String failed = getString(R.string.failed);
        String ok = getString(R.string.ok);
        String msg;

        if (checkPermission()) {
            try {
                File file = imageProcessing.createImageFile();

                OutputStream outStream = new FileOutputStream(file);
                StaticBitmap.image.compress(Bitmap.CompressFormat.PNG, 100, outStream);

                outStream.flush();
                outStream.close();

                msg = getString(R.string.image_saved);
                builder.setTitle(success)
                        .setMessage(msg)
                        .setNegativeButton(ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
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

    private class MenuSelectedListener implements TabLayout.OnTabSelectedListener {

        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            StaticBitmap.image = StaticBitmap.originalImg;
            ivImg.setImageBitmap(StaticBitmap.image);
            tvTitle.setText(title);

            frameImg.removeAllViews();

            int tabPosition = tab.getPosition();

            switch (tabPosition) {
                case 0: // Flip
                    setMenu1();
                    break;
                case 1: // Layer
                    setMenu2();
                    break;
                case 2: // Grayscale
                    setMenu3();
                    break;
                case 3: // Black and White
                    setMenu4();
                    break;
                case 4: // Reduksi Noise
                    setMenu5();
                    break;
                case 5: // Filter
                    setMenu6();
                    break;
                case 6: // Deteksi Tepi
                    setMenu7();
                    break;
                case 7: // Kuantisasi
                    setMenu8();
                    break;
//                case 8: // Rotate
//                    setMenu9();
//                    break;
                case 8: // Brightness
                    setMenu10();
                    break;
                case 9: // Contrast
                    setMenu11();
                    break;
                case 10: // Noise Gaussian
                    setMenu12();
                    break;
                case 11: // Noise Speckle
                    setMenu13();
                    break;
                case 12: // Noise Salt and Pepper
                    setMenu14();
                    break;
            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
            // Do nothing
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {
            // Do nothing
        }
    }

    private class RotationChangeListener implements SeekBar.OnSeekBarChangeListener {

        private Bitmap img;

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            img = StaticBitmap.originalImg.copy(StaticBitmap.originalImg.getConfig(), true);

            imageProcessing.changeRotation(img, progress);
            ivImg.setImageBitmap(img);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // Do nothing
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            StaticBitmap.image = img;
        }
    }

    private class BrightnessChangeListener implements SeekBar.OnSeekBarChangeListener {

        private Bitmap img;

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            img = StaticBitmap.originalImg.copy(StaticBitmap.originalImg.getConfig(), true);
            int changeBrightness = progress - 128;

            imageProcessing.changeBrightness(img, changeBrightness);
            ivImg.setImageBitmap(img);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // Do nothing
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            StaticBitmap.image = img;
        }
    }

    private class ContrastChangeListener implements SeekBar.OnSeekBarChangeListener {

        private Bitmap img;

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            img = StaticBitmap.originalImg.copy(StaticBitmap.originalImg.getConfig(), true);
            double changeContrast = ((double) progress) / 20;

            imageProcessing.changeContrast(img, changeContrast);
            ivImg.setImageBitmap(img);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // Do nothing
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            StaticBitmap.image = img;
        }
    }

    private class GaussianChangeListener implements SeekBar.OnSeekBarChangeListener {

        private Bitmap img;

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            img = StaticBitmap.originalImg.copy(StaticBitmap.originalImg.getConfig(), true);

            imageProcessing.addGaussianNoise(img, progress);
            ivImg.setImageBitmap(img);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // Do nothing
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            StaticBitmap.image = img;
        }
    }

    private class SpeckleChangeListener implements SeekBar.OnSeekBarChangeListener {

        private Bitmap img;

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            img = StaticBitmap.originalImg.copy(StaticBitmap.originalImg.getConfig(), true);

            imageProcessing.addSpeckleNoise(img, progress);
            ivImg.setImageBitmap(img);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // Do nothing
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            StaticBitmap.image = img;
        }
    }

    private class SaltPepperChangeListener implements SeekBar.OnSeekBarChangeListener {

        private Bitmap img;

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            img = StaticBitmap.originalImg.copy(StaticBitmap.originalImg.getConfig(), true);

            imageProcessing.addSaltPepperNoise(img, progress);
            ivImg.setImageBitmap(img);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // Do nothing
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            StaticBitmap.image = img;
        }
    }

    private class CheckClickListener implements View.OnClickListener {

        private int defaultProgress;
        private SeekBar sbEnhance;

        private CheckClickListener() {
            this(null, 0);
        }

        private CheckClickListener(SeekBar sbEnhance, int defaultProgress) {
            this.defaultProgress = defaultProgress;
            this.sbEnhance = sbEnhance;
        }

        @Override
        public void onClick(View v) {
            String ok = getString(R.string.ok);
            StaticBitmap.image = ((BitmapDrawable) ivImg.getDrawable()).getBitmap();
            StaticBitmap.originalImg = ((BitmapDrawable) ivImg.getDrawable()).getBitmap();

            if (sbEnhance != null)
                sbEnhance.setProgress(defaultProgress);
            else
                adapter.notifyDataSetChanged();

            ivImg.setImageBitmap(StaticBitmap.image);
            tvTitle.setText(title);

            Toast.makeText(ImageActivity.this, ok, Toast.LENGTH_LONG).show();
        }
    }

    private class CloseClickListener implements View.OnClickListener {

        private int defaultProgress;
        private SeekBar sbEnhance;

        private CloseClickListener() {
            this(null, 0);
        }

        private CloseClickListener(SeekBar sbEnhance, int defaultProgress) {
            this.defaultProgress = defaultProgress;
            this.sbEnhance = sbEnhance;
        }

        @Override
        public void onClick(View v) {
            Bitmap image = StaticBitmap.originalImg;

            if (sbEnhance != null)
                sbEnhance.setProgress(defaultProgress);

            ivImg.setImageBitmap(image);
            tvTitle.setText(title);
        }
    }
}