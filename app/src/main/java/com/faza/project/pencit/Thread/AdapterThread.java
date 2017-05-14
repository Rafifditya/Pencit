package com.faza.project.pencit.Thread;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.faza.project.pencit.ImageProcessing.ImageProcessing;
import com.faza.project.pencit.R;
import com.faza.project.pencit.Session.StaticBitmap;
import com.faza.project.pencit.ViewHolder.ImgViewHolder;

/**
 * Dibuat oleh Faza Zulfika Permana Putra
 */

public class AdapterThread extends AsyncTask<Integer, Void, Bitmap> {

    private ImageProcessing imageProcessing;
    private ImageView ivMain, ivImg;
    private ProgressBar pbImg;
    private RelativeLayout.LayoutParams params;
    private String menu;
    private TextView tvTitle;

    private int verticalMargin, horizontalMargin;

    public AdapterThread(Context context, String menu, ImageView ivMain, TextView tvTitle, ImgViewHolder viewHolder) {
        imageProcessing = new ImageProcessing();

        this.menu = menu;
        this.ivMain = ivMain;
        this.tvTitle = tvTitle;

        ivImg = viewHolder.getIvImg();
        pbImg = viewHolder.getPbImg();

        Resources resources = context.getResources();

        verticalMargin = (int) resources.getDimension(R.dimen.activity_half_vertical_margin);
        horizontalMargin = (int) resources.getDimension(R.dimen.activity_half_horizontal_margin);
        params = (RelativeLayout.LayoutParams) ivImg.getLayoutParams();
    }

    @Override
    protected void onPreExecute() {
        ivImg.setVisibility(View.GONE);
        pbImg.setVisibility(View.VISIBLE);
    }

    @Override
    protected Bitmap doInBackground(Integer... position) {
        if (position[0] == 0)
            params.setMargins(horizontalMargin, verticalMargin, horizontalMargin, verticalMargin);
        else
            params.setMargins(0, verticalMargin, horizontalMargin, verticalMargin);

        return setBitmap(menu);
    }

    @Override
    protected void onPostExecute(Bitmap img) {
        ivImg.setVisibility(View.VISIBLE);
        ivImg.setLayoutParams(params);
        ivImg.setImageBitmap(img);
        ivImg.setOnClickListener(new IvImgClickListener(img, menu));

        pbImg.setVisibility(View.GONE);
    }

    private Bitmap setBitmap(String menu) {
        Bitmap img = StaticBitmap.originalImg.copy(StaticBitmap.originalImg.getConfig(), true);

        if (menu.equals("Flip Horizontal"))
            imageProcessing.getHorizontalFlipImage(img);
        else if (menu.equals("Flip Vertical"))
            imageProcessing.getVerticalFlipImage(img);
        else if (menu.equals("Transpose Image"))
            imageProcessing.getTransposeImage(img);
        else if (menu.equals("Layer Red"))
            imageProcessing.getRedLayer(img);
        else if (menu.equals("Layer Green"))
            imageProcessing.getGreenLayer(img);
        else if (menu.equals("Layer Blue"))
            imageProcessing.getBlueLayer(img);
        else if (menu.equals("Invert"))
            imageProcessing.getInvertImg(img);
        else if (menu.equals("Gray Red"))
            imageProcessing.getGrayRed(img);
        else if (menu.equals("Gray Green"))
            imageProcessing.getGrayGreen(img);
        else if (menu.equals("Gray Blue"))
            imageProcessing.getGrayBlue(img);
        else if (menu.equals("Gray Mean"))
            imageProcessing.getGrayMean(img);
        else if (menu.equals("Black and White"))
            imageProcessing.getBlackAndWhite(img);
        else if (menu.equals("128 Bit"))
            imageProcessing.get128Bit(img);
        else if (menu.equals("64 Bit"))
            imageProcessing.get64Bit(img);
        else if (menu.equals("32 Bit"))
            imageProcessing.get32Bit(img);
        else if (menu.equals("16 Bit"))
            imageProcessing.get16Bit(img);
        else if (menu.equals("8 Bit"))
            imageProcessing.get8Bit(img);
        else if (menu.equals("4 Bit"))
            imageProcessing.get4Bit(img);
        else if (menu.equals("2 Bit"))
            imageProcessing.get2Bit(img);
        else if (menu.equals("Auto Level"))
            imageProcessing.getAutoLevelImage(img);
        else if (menu.equals("Gray Auto Level"))
            imageProcessing.getGrayAutoLevelImage(img);
        else if (menu.equals("Histogram Equalization"))
            imageProcessing.getHistogramEqualizationImage(img);
        else if (menu.equals("Sharpness Image"))
            imageProcessing.getSharpnessImage(img);
        else if (menu.equals("Filter 4 Node")) {
            double[] filter = new double[10];

            filter[1] = 0;
            filter[2] = 0.2;
            filter[3] = 0;

            filter[4] = 0.2;
            filter[5] = 0.2;
            filter[6] = 0.2;

            filter[7] = 0;
            filter[8] = 0.2;
            filter[9] = 0;

            imageProcessing.getFilterImage3x3(img, filter);
        } else if (menu.equals("Filter 4 Node 2")) {
            double[] filter = new double[10];

            filter[1] = 0;
            filter[2] = -0.5;
            filter[3] = 0;

            filter[4] = -0.5;
            filter[5] = 0;
            filter[6] = 0.5;

            filter[7] = 0;
            filter[8] = 0.5;
            filter[9] = 0;

            imageProcessing.getFilterImage3x3(img, filter);
        } else if (menu.equals("Filter 4 Node 3")) {
            double[] filter = new double[10];

            filter[1] = 0;
            filter[2] = -0.5;
            filter[3] = 0;

            filter[4] = -0.5;
            filter[5] = 1;
            filter[6] = 0.5;

            filter[7] = 0;
            filter[8] = 0.5;
            filter[9] = 0;

            imageProcessing.getFilterImage3x3(img, filter);
        } else if (menu.equals("Filter 8 Node")) {
            double[] filter = new double[10];

            filter[1] = 0.1;
            filter[2] = 0.1;
            filter[3] = 0.1;

            filter[4] = 0.1;
            filter[5] = 0.2;
            filter[6] = 0.1;

            filter[7] = 0.1;
            filter[8] = 0.1;
            filter[9] = 0.1;

            imageProcessing.getFilterImage3x3(img, filter);
        } else if (menu.equals("Filter 8 Node 2")) {
            double[] filter = new double[10];

            filter[1] = -1;
            filter[2] = -0.5;
            filter[3] = 0;

            filter[4] = -0.5;
            filter[5] = 0;
            filter[6] = 0.5;

            filter[7] = 0;
            filter[8] = 0.5;
            filter[9] = 1;

            imageProcessing.getFilterImage3x3(img, filter);
        } else if (menu.equals("Filter 8 Node 3")) {
            double[] filter = new double[10];

            filter[1] = -1;
            filter[2] = -0.5;
            filter[3] = 0;

            filter[4] = -0.5;
            filter[5] = 1;
            filter[6] = 0.5;

            filter[7] = 0;
            filter[8] = 0.5;
            filter[9] = 1;

            imageProcessing.getFilterImage3x3(img, filter);
        } else if (menu.equals("Filter Rata-Rata"))
            imageProcessing.getMeanFilterImage(img);
        else if (menu.equals("Filter Gaussian"))
            imageProcessing.getGaussianFilterImage(img);
        else if (menu.equals("Filter Median"))
            imageProcessing.getMedianFilterImage(img);
        else if (menu.equals("Robert Method"))
            imageProcessing.getRobertImage(img);
        else if (menu.equals("Horizontal Robert Method"))
            imageProcessing.getHorizontalRobertImage(img);
        else if (menu.equals("Vertical Robert Method"))
            imageProcessing.getVerticalRobertImage(img);
        else if (menu.equals("Prewit Method"))
            imageProcessing.getPrewitImage(img);
        else if (menu.equals("Sobel Method"))
            imageProcessing.getSobelImage(img);
        else if (menu.equals("Laplacian Method"))
            imageProcessing.getLaplacianImage(img);
        else if (menu.equals("Normal Sketch"))
            imageProcessing.getNormalSkecthImage(img);
        else if (menu.equals("Sketch"))
            imageProcessing.getSketchImage(img);

        return img;
    }

    private class IvImgClickListener implements View.OnClickListener {

        private Bitmap img;
        private String title;

        private IvImgClickListener(Bitmap img, String title) {
            this.img = img;
            this.title = title;
        }

        @Override
        public void onClick(View v) {
            ivMain.setImageBitmap(img);
            tvTitle.setText(title);

            StaticBitmap.image = img;
        }
    }
}