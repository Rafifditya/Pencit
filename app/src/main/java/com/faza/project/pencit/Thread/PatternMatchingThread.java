package com.faza.project.pencit.Thread;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.faza.project.pencit.ImageProcessing.ImageProcessing;

/**
 * Dibuat oleh Faza Zulfika Permana Putra
 */

public class PatternMatchingThread extends AsyncTask<Bitmap, Void, Bitmap> {
    private ImageProcessing imageProcessing;
    private ImageView ivImg;
    private ProgressDialog dialog;

    public PatternMatchingThread(ProgressDialog dialog, ImageView ivImg) {
        imageProcessing = new ImageProcessing();

        this.dialog = dialog;
        this.ivImg = ivImg;
    }

    @Override
    protected Bitmap doInBackground(Bitmap... params) {
        Bitmap img = params[0];
        Bitmap imgTemplate = params[1];

        imageProcessing.getNumPatternMatching(img, imgTemplate);

        return img;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        ivImg.setImageBitmap(bitmap);
        dialog.dismiss();
    }
}