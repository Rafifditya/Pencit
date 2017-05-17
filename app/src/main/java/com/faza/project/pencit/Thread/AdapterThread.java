package com.faza.project.pencit.Thread;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.AsyncTask;
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

        double[] kernel;
        switch (menu) {
            case "Flip Horizontal":
                imageProcessing.getHorizontalFlipImage(img);
                break;
            case "Flip Vertical":
                imageProcessing.getVerticalFlipImage(img);
                break;
            case "Flip Horizontal-Vertical":
                imageProcessing.getTransposeImage(img);
                break;
            case "Layer Red":
                imageProcessing.getRedLayer(img);
                break;
            case "Layer Green":
                imageProcessing.getGreenLayer(img);
                break;
            case "Layer Blue":
                imageProcessing.getBlueLayer(img);
                break;
            case "Sepia":
                imageProcessing.getSepiaImage(img);
                break;
            case "Invers":
                imageProcessing.getInvertImg(img);
                break;
            case "Grayscale Red":
                imageProcessing.getGrayRed(img);
                break;
            case "Grayscale Green":
                imageProcessing.getGrayGreen(img);
                break;
            case "Grayscale Blue":
                imageProcessing.getGrayBlue(img);
                break;
            case "Grayscale Mean":
                imageProcessing.getGrayMean(img);
                break;
            case "Black and White":
                imageProcessing.getBlackAndWhite(img, 128);
                break;
            case "Black and White 100":
                imageProcessing.getBlackAndWhite(img, 100);
                break;
            case "Black and White 200":
                imageProcessing.getBlackAndWhite(img, 200);
                break;
            case "Filter Mean":
                imageProcessing.getMeanFilterImage(img);
                break;
            case "Filter Gaussian":
                imageProcessing.getGaussianFilterImage(img);
                break;
            case "Filter Median":
                imageProcessing.getMedianFilterImage(img);
                break;
            case "Auto Level":
                imageProcessing.getAutoLevelImage(img);
                break;
            case "Grayscale Auto Level":
                imageProcessing.getGrayAutoLevelImage(img);
                break;
            case "Histogram Equalization":
                imageProcessing.getHistogramEqualizationImage(img);
                break;
            case "Grayscale Histogram Equalization":
                imageProcessing.getGrayHistogramEqualizationImage(img);
                break;
            case "Sharpness":
                imageProcessing.getSharpnessImage(img);
                break;
            case "Low Pass Filter":
                kernel = new double[10];

                kernel[1] = 0;
                kernel[2] = 0.2;
                kernel[3] = 0;

                kernel[4] = 0.2;
                kernel[5] = 0.2;
                kernel[6] = 0.2;

                kernel[7] = 0;
                kernel[8] = 0.2;
                kernel[9] = 0;

                imageProcessing.getFilterImage3x3(img, kernel);
                break;
            case "High Pass Filter":
                kernel = new double[10];

                kernel[1] = -1;
                kernel[2] = -0.5;
                kernel[3] = 0;

                kernel[4] = -0.5;
                kernel[5] = 0;
                kernel[6] = 0.5;

                kernel[7] = 0;
                kernel[8] = 0.5;
                kernel[9] = 1;

                imageProcessing.getFilterImage3x3(img, kernel);
                break;
            case "Band Stop Filter":
                kernel = new double[10];

                kernel[1] = -1;
                kernel[2] = -0.5;
                kernel[3] = 0;

                kernel[4] = -0.5;
                kernel[5] = 1;
                kernel[6] = 0.5;

                kernel[7] = 0;
                kernel[8] = 0.5;
                kernel[9] = 1;

                imageProcessing.getFilterImage3x3(img, kernel);
                break;
            case "Robert Method":
                imageProcessing.getRobertImage(img);
                break;
            case "Prewitt Method":
                imageProcessing.getPrewitImage(img);
                break;
            case "Sobel Method":
                imageProcessing.getSobelImage(img);
                break;
            case "Laplacian Method":
                imageProcessing.getLaplacianImage(img);
                break;
            case "Sketch":
                imageProcessing.getNormalSkecthImage(img);
                break;
            case "New Sketch":
                imageProcessing.getSketchImage(img);
                break;
            case "Kuantisasi 2 bit":
                imageProcessing.getBitImage(img, 2);
                break;
            case "Kuantisasi 4 bit":
                imageProcessing.getBitImage(img, 4);
                break;
            case "Kuantisasi 8 bit":
                imageProcessing.getBitImage(img, 8);
                break;
            case "Kuantisasi 16 bit":
                imageProcessing.getBitImage(img, 16);
                break;
            case "Kuantisasi 32 bit":
                imageProcessing.getBitImage(img, 32);
                break;
            case "Kuantisasi 64 bit":
                imageProcessing.getBitImage(img, 64);
                break;
            case "Kuantisasi 128 bit":
                imageProcessing.getBitImage(img, 128);
                break;
        }

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