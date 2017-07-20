package com.faza.project.pencit.ImageProcessing;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;

import com.faza.project.pencit.Models.Point;
import com.faza.project.pencit.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

/**
 * Dibuat oleh Faza Zulfika Permana Putra
 */

public class ImageProcessing {

    public Bitmap decodeImage(Bitmap img) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.reset();

        img.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

        byte[] imgArray = outputStream.toByteArray();

        try {
            outputStream.flush();
            outputStream.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }

        BitmapFactory.Options options = new BitmapFactory.Options();

        options.inMutable = true;
        options.inSampleSize = getSampleSize(img);

        return BitmapFactory.decodeByteArray(imgArray, 0, imgArray.length, options);
    }

    private int getSampleSize(Bitmap img) {
        int byteCount = img.getByteCount();

        if (byteCount < 8294400) // Under full HD image
            byteCount = 1;
        else if (byteCount >= 8294400 && byteCount < 14745600) // Full HD image
            byteCount = 2;
        else if (byteCount >= 14745600 && byteCount < 33177600) // 2K image
            byteCount = 3;
        else if (byteCount >= 33177600 && byteCount < 132710400) // 4K image
            byteCount = 4;
        else // Above 4K image
            byteCount = 6;

        return byteCount;
    }

    public File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageDirectory = "/storage/emulated/0/DCIM/Camera/";
        String imageFileName = "IMG_" + timeStamp;

        return new File(
                imageDirectory,
                imageFileName + ".jpg"
        );
    }

    public void getRedLayer(Bitmap img) {
        int width = img.getWidth();
        int height = img.getHeight();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int color = img.getPixel(i, j);
                int r = Color.red(color);

                color = Color.rgb(r, 0, 0);

                img.setPixel(i, j, color);
            }
        }
    }

    public void getGreenLayer(Bitmap img) {
        int width = img.getWidth();
        int height = img.getHeight();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int color = img.getPixel(i, j);
                int g = Color.green(color);

                color = Color.rgb(0, g, 0);

                img.setPixel(i, j, color);
            }
        }
    }

    public void getBlueLayer(Bitmap img) {
        int width = img.getWidth();
        int height = img.getHeight();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int color = img.getPixel(i, j);
                int b = Color.blue(color);

                color = Color.rgb(0, 0, b);

                img.setPixel(i, j, color);
            }
        }
    }

    public void getInvertImg(Bitmap img) {
        int width = img.getWidth();
        int height = img.getHeight();
        int max = 255;

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int color = img.getPixel(i, j);

                int r = max - Color.red(color);
                int g = max - Color.green(color);
                int b = max - Color.blue(color);

                color = Color.rgb(r, g, b);

                img.setPixel(i, j, color);
            }
        }
    }

    public void getGrayRed(Bitmap img) {
        int width = img.getWidth();
        int height = img.getHeight();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int color = img.getPixel(i, j);
                int r = Color.red(color);

                color = Color.rgb(r, r, r);

                img.setPixel(i, j, color);
            }
        }
    }

    public void getGrayGreen(Bitmap img) {
        int width = img.getWidth();
        int height = img.getHeight();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int color = img.getPixel(i, j);
                int g = Color.green(color);

                color = Color.rgb(g, g, g);

                img.setPixel(i, j, color);
            }
        }
    }

    public void getGrayBlue(Bitmap img) {
        int width = img.getWidth();
        int height = img.getHeight();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int color = img.getPixel(i, j);
                int b = Color.blue(color);

                color = Color.rgb(b, b, b);

                img.setPixel(i, j, color);
            }
        }
    }

    public void getGrayMean(Bitmap img) {
        int width = img.getWidth();
        int height = img.getHeight();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int color = img.getPixel(i, j);

                int r = Color.red(color);
                int g = Color.green(color);
                int b = Color.blue(color);
                int gray = (r + g + b) / 3;

                color = Color.rgb(gray, gray, gray);

                img.setPixel(i, j, color);
            }
        }
    }

    public void getBlackAndWhite(Bitmap img, int threshold) {
        int width = img.getWidth();
        int height = img.getHeight();

        getGrayMean(img);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int color = img.getPixel(i, j);
                int gray = Color.red(color);
                gray = bwPixelNormalization(gray, threshold);

                color = Color.rgb(gray, gray, gray);

                img.setPixel(i, j, color);
            }
        }
    }

    private int changeBit(int color, int bit) {
        return bit * (color / bit);
    }

    public void getBitImage(Bitmap img, int bit) {
        int width = img.getWidth();
        int height = img.getHeight();

        bit = 255 / bit;

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int color = img.getPixel(i, j);

                int r = changeBit(Color.red(color), bit);
                int g = changeBit(Color.green(color), bit);
                int b = changeBit(Color.blue(color), bit);

                color = Color.rgb(r, g, b);

                img.setPixel(i, j, color);
            }
        }
    }

    private void addBitmap(Bitmap one, Bitmap two, Bitmap result) {
        int width = result.getWidth();
        int height = result.getHeight();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int colorOne = one.getPixel(i, j);
                int colorTwo = two.getPixel(i, j);

                int red = Color.red(colorOne) + Color.red(colorTwo);
                int green = Color.green(colorOne) + Color.green(colorTwo);
                int blue = Color.blue(colorOne) + Color.blue(colorTwo);

                red = pixelNormalization(red);
                green = pixelNormalization(green);
                blue = pixelNormalization(blue);

                int color = Color.rgb(red, green, blue);

                result.setPixel(i, j, color);
            }
        }
    }

    private void copyBitmap(Bitmap copy, Bitmap to) {
        int width = copy.getWidth();
        int height = copy.getHeight();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int color = copy.getPixel(i, j);
                to.setPixel(i, j, color);
            }
        }
    }

    public void getHorizontalFlipImage(Bitmap img) {
        int width = img.getWidth();
        int height = img.getHeight();

        Bitmap imgFlip = Bitmap.createBitmap(width, height, img.getConfig());

        for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    int backWidth = (width - 1) - i;
                    int backColor = img.getPixel(backWidth, j);

                    imgFlip.setPixel(i, j, backColor);
                }
        }

        copyBitmap(imgFlip, img);
    }

    public void getVerticalFlipImage(Bitmap img) {
        int width = img.getWidth();
        int height = img.getHeight();

        Bitmap imgFlip = Bitmap.createBitmap(width, height, img.getConfig());

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int backHeight = (height - 1) - j;
                int backColor = img.getPixel(i, backHeight);

                imgFlip.setPixel(i, j, backColor);
            }
        }

        copyBitmap(imgFlip, img);
    }

    public void getTransposeImage(Bitmap img) {
        int width = img.getWidth();
        int height = img.getHeight();

        Bitmap imgFlip = Bitmap.createBitmap(width, height, img.getConfig());

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int backWidth = (width - 1) - i;
                int backHeight = (height - 1) - j;
                int newColor = img.getPixel(backWidth, backHeight);

                imgFlip.setPixel(i, j, newColor);
            }
        }

        copyBitmap(imgFlip, img);
    }

    public void getSepiaImage(Bitmap img) {
        int width = img.getWidth();
        int height = img.getHeight();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int color = img.getPixel(i, j);

                int r = Color.red(color);
                int g = Color.green(color);
                int b = Color.blue(color);

                r = (int) ((r * 0.393) + (g * 0.769) + (b * 0.189));
                g = (int) ((r * 0.349) + (g * 0.686) + (b * 0.168));
                b = (int) ((r * 0.272) + (g * 0.534) + (b * 0.131));

                r = pixelNormalization(r);
                g = pixelNormalization(g);
                b = pixelNormalization(b);

                color = Color.rgb(r, g, b);

                img.setPixel(i, j, color);
            }
        }
    }

    private int getBigger(int max, int color) {
        return color > max ? color : max;
    }

    private int getSmaller(int min, int color) {
        return color < min ? color : min;
    }

    private int getAutoLevelColor(int color, int min, int max) {
        return (color - min) * (255 / (max - min));
    }

    public void getAutoLevelImage(Bitmap img) {
        int width = img.getWidth();
        int height = img.getHeight();

        int color0 = img.getPixel(0, 0);

        int rMin = Color.red(color0);
        int rMax = Color.red(color0);
        int gMin = Color.green(color0);
        int gMax = Color.green(color0);
        int bMin = Color.blue(color0);
        int bMax = Color.blue(color0);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int color = img.getPixel(i, j);

                int r = Color.red(color);
                int g = Color.green(color);
                int b = Color.blue(color);

                rMin = getSmaller(rMin, r);
                gMin = getSmaller(gMin, g);
                bMin = getSmaller(bMin, b);

                rMax = getBigger(rMax, r);
                gMax = getBigger(rMax, g);
                bMax = getBigger(rMax, b);
            }
        }

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int color = img.getPixel(i, j);

                int r = getAutoLevelColor(Color.red(color), rMin, rMax);
                int g = getAutoLevelColor(Color.green(color), gMin, gMax);
                int b = getAutoLevelColor(Color.blue(color), bMin, bMax);

                color = Color.rgb(r, g, b);

                img.setPixel(i, j, color);
            }
        }
    }

    public void getGrayAutoLevelImage(Bitmap img) {
        getGrayMean(img);
        getAutoLevelImage(img);
    }

    private int getHistEqualizationColor(int cumulativeColor, int width, int height) {
        return (255 * cumulativeColor) / (width * height);
    }

    public void getHistogramEqualizationImage(Bitmap img) {
        int width = img.getWidth();
        int height = img.getHeight();

        int[] rL = new int[256];
        int[] gL = new int[256];
        int[] bL = new int[256];

        setRGBCDF(img, rL, gL, bL);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int color = img.getPixel(i, j);

                int r = Color.red(color);
                int g = Color.green(color);
                int b = Color.blue(color);

                r = getHistEqualizationColor(rL[r], width, height);
                g = getHistEqualizationColor(gL[g], width, height);
                b = getHistEqualizationColor(bL[b], width, height);

                color = Color.rgb(r, g, b);

                img.setPixel(i, j, color);
            }
        }
    }

    public void getGrayHistogramEqualizationImage(Bitmap img) {
        getGrayMean(img);
        getHistogramEqualizationImage(img);
    }

    public void getSharpnessImage(Bitmap img) {
        Bitmap imgMeanFilter = img.copy(img.getConfig(), true);
        Bitmap imgSobel = img.copy(img.getConfig(), true);

        getMeanFilterImage(imgMeanFilter);
        getRobertImage(imgSobel);

        int width = img.getWidth();
        int height = img.getHeight();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int colorMean = imgMeanFilter.getPixel(i, j);
                int colorSobel = imgSobel.getPixel(i, j);

                int r = Color.red(colorMean);
                int g = Color.green(colorMean);
                int b = Color.blue(colorMean);

                int sobel = Color.red(colorSobel);

                r += sobel;
                g += sobel;
                b += sobel;

                r = pixelNormalization(r);
                g = pixelNormalization(g);
                b = pixelNormalization(b);

                int color = Color.rgb(r, g, b);

                img.setPixel(i, j, color);
            }
        }
    }

    public void getFilterImage2(Bitmap img, double[][] filter) {
        int width = img.getWidth();
        int height = img.getHeight();
        int filterHeight = filter.length;
        int filterWidth = filter[0].length;

        Bitmap imgFilter = Bitmap.createBitmap(width, height, img.getConfig());

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                double filterValue;
                int sizeHeight, sizeWidth, color;
                int redSum = 0;
                int blueSum = 0;
                int greenSum = 0;
                int centerHeight = (filterHeight / 2);
                int centerWidth = (filterWidth / 2);
                int row = 0;

                sizeHeight = j - centerHeight;

                for (int k = sizeHeight; k < (sizeHeight + filterHeight); k++) {
                    if (k >= 0 && k < height) {
                        int column = 0;

                        sizeWidth = i - centerWidth;

                        for (int l = sizeWidth; l < (sizeWidth + filterWidth); l++) {
                            if (l >= 0 && l < width) {
                                color = img.getPixel(l, k);
                                filterValue = filter[row][column];

                                redSum += Color.red(color) * filterValue;
                                greenSum += Color.green(color) * filterValue;
                                blueSum += Color.blue(color) * filterValue;
                            }

                            column++;
                        }
                    }

                    row++;
                }

                redSum = pixelNormalization(redSum);
                greenSum = pixelNormalization(greenSum);
                blueSum = pixelNormalization(blueSum);

                color = Color.rgb(redSum, greenSum, blueSum);
                imgFilter.setPixel(i, j, color);
            }
        }

        copyBitmap(imgFilter, img);
    }

    public void getFilterImage2(Bitmap img, Bitmap filter) {
        int width = img.getWidth();
        int height = img.getHeight();
        int filterHeight = filter.getHeight();
        int filterWidth = filter.getWidth();

        Bitmap imgFilter = Bitmap.createBitmap(width, height, img.getConfig());

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int sizeHeight, sizeWidth, color, filterColor;
                int redSum = 0;
                int blueSum = 0;
                int greenSum = 0;
                int centerHeight = (filterHeight / 2);
                int centerWidth = (filterWidth / 2);
                int row = 0;

                sizeHeight = j - centerHeight;

                for (int k = sizeHeight; k < (sizeHeight + filterHeight); k++) {
                    if (k >= 0 && k < height) {
                        int column = 0;

                        sizeWidth = i - centerWidth;

                        for (int l = sizeWidth; l < (sizeWidth + filterWidth); l++) {
                            if (l >= 0 && l < width) {
                                color = img.getPixel(l, k);
                                filterColor = filter.getPixel(column, row);

                                redSum += Color.red(color) * Color.red(filterColor);
                                greenSum += Color.green(color) * Color.green(filterColor);
                                blueSum += Color.blue(color) * Color.blue(filterColor);
                            }

                            column++;
                        }
                    }

                    row++;
                }

                redSum = pixelNormalization(redSum);
                greenSum = pixelNormalization(greenSum);
                blueSum = pixelNormalization(blueSum);

                color = Color.rgb(redSum, greenSum, blueSum);
                imgFilter.setPixel(i, j, color);
            }
        }

        copyBitmap(imgFilter, img);
    }

    public void getMeanFilterImage(Bitmap img) {
        double[][] kernel = new double[3][3];

        for (int i = 0; i < kernel.length; i++) {
            for (int j = 0; j < kernel[i].length; j++) {
                kernel[i][j] = 1.0 / 9.0;
            }
        }

        getFilterImage2(img, kernel);
    }

    public void getGaussianFilterImage(Bitmap img) {
        double[][] kernel = new double[3][3];

        for (int i = 0; i < kernel.length; i++) {
            for (int j = 0; j < kernel[i].length; j++) {
                kernel[i][j] = 1.0 / 13.0;
            }
        }

        getFilterImage2(img, kernel);
    }

    private int getMedian(int[] color) {
        int max = color.length - 1;
        int temp;

        for (int i = 1; i < max; i++) {
            for (int j = 1; j < max; j++) {
                if (color[j] > color[j + 1]) {
                    temp = color[j];
                    color[j] = color[j + 1];
                    color[j + 1] = temp;
                }
            }
        }

        return color[5];
    }

    public void getMedianFilterImage(Bitmap img, int kernelHeight, int kernelWidth) {
        int width = img.getWidth();
        int height = img.getHeight();
        int arraySize = kernelHeight * kernelWidth;

        int[] rA = new int[arraySize];
        int[] gA = new int[arraySize];
        int[] bA = new int[arraySize];

        Bitmap imgFilter = Bitmap.createBitmap(width, height, img.getConfig());

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int sizeHeight, sizeWidth, color;
                int centerHeight = (kernelHeight / 2);
                int centerWidth = (kernelWidth / 2);
                int array = 0;

                sizeHeight = j - centerHeight;

                for (int k = sizeHeight; k < (sizeHeight + kernelHeight); k++) {
                    if (k >= 0 && k < height) {
                        sizeWidth = i - centerWidth;

                        for (int l = sizeWidth; l < (sizeWidth + kernelWidth); l++) {
                            if (l >= 0 && l < width) {
                                color = img.getPixel(l, k);

                                rA[array] = Color.red(color);
                                gA[array] = Color.green(color);
                                bA[array] = Color.blue(color);
                            }

                            array++;
                        }
                    }
                }

                int r = getMedian(rA);
                int g = getMedian(gA);
                int b = getMedian(bA);

                color = Color.rgb(r, g, b);

                imgFilter.setPixel(i, j, color);
            }
        }

        copyBitmap(imgFilter, img);
    }

    public void getRobertImage(Bitmap img) {
        double[][] kernel = new double[2][2];

        kernel[0][0] = -1;
        kernel[0][1] = 1;

        kernel[1][0] = -1;
        kernel[1][1] = 1;

        getFilterImage2(img, kernel);
    }

    public void getHorizontalRobertImage(Bitmap img) {
        double[][] kernel = new double[1][1];

        kernel[0][0] = -1;
        kernel[1][0] = 1;

        getFilterImage2(img, kernel);
    }

    public void getVerticalRobertImage(Bitmap img) {
        double[][] kernel = new double[1][1];

        kernel[0][0] = -1;
        kernel[1][0] = 1;

        getFilterImage2(img, kernel);
    }

    public void getPrewitImage(Bitmap img) {
        Bitmap imgHorizontal = img.copy(img.getConfig(), true);
        Bitmap imgVertical = img.copy(img.getConfig(), true);

        double[][] kernelHorizontal = new double[3][3];
        double[][] kernelVertical = new double[3][3];

        kernelHorizontal[0][0] = -1;
        kernelHorizontal[0][1] = 0;
        kernelHorizontal[0][2] = 1;

        kernelHorizontal[1][0] = -1;
        kernelHorizontal[1][1] = 0;
        kernelHorizontal[1][2] = 1;

        kernelHorizontal[2][0] = -1;
        kernelHorizontal[2][1] = 0;
        kernelHorizontal[2][2] = 1;

        kernelVertical[0][0] = -1;
        kernelVertical[0][1] = -1;
        kernelVertical[0][2] = -1;

        kernelVertical[1][0] = 0;
        kernelVertical[1][1] = 0;
        kernelVertical[1][2] = 0;

        kernelVertical[2][0] = 1;
        kernelVertical[2][1] = 1;
        kernelVertical[2][2] = 1;

        getFilterImage2(imgHorizontal, kernelHorizontal);
        getFilterImage2(imgVertical, kernelVertical);

        addBitmap(imgHorizontal, imgVertical, img);
    }

    public void getSobelImage(Bitmap img) {
        Bitmap imgHorizontal = img.copy(img.getConfig(), true);
        Bitmap imgVertical = img.copy(img.getConfig(), true);

        double[][] kernelHorizontal = new double[3][3];
        double[][] kernelVertical = new double[3][3];

        kernelHorizontal[0][0] = -1;
        kernelHorizontal[0][1] = 0;
        kernelHorizontal[0][2] = 1;

        kernelHorizontal[1][0] = -2;
        kernelHorizontal[1][1] = 0;
        kernelHorizontal[1][2] = 2;

        kernelHorizontal[2][0] = -1;
        kernelHorizontal[2][1] = 0;
        kernelHorizontal[2][2] = 1;

        kernelVertical[0][0] = -1;
        kernelVertical[0][1] = -2;
        kernelVertical[0][2] = -1;

        kernelVertical[1][0] = 0;
        kernelVertical[1][1] = 0;
        kernelVertical[1][2] = 0;

        kernelVertical[2][0] = 1;
        kernelVertical[2][1] = 2;
        kernelVertical[2][2] = 1;

        getFilterImage2(imgHorizontal, kernelHorizontal);
        getFilterImage2(imgVertical, kernelVertical);

        addBitmap(imgHorizontal, imgVertical, img);
    }

    public void getLaplacianImage(Bitmap img) {
        double[][] kernel = new double[3][3];

        kernel[0][0] = 1;
        kernel[0][1] = -2;
        kernel[0][2] = 1;

        kernel[1][0] = -2;
        kernel[1][1] = 4;
        kernel[1][2] = -2;

        kernel[2][0] = 1;
        kernel[2][1] = -2;
        kernel[2][2] = 1;

        getFilterImage2(img, kernel);
    }

    private int getA(Bitmap img, int i, int j) {
        int count = 0;

        int p2 = get01Pixel(img.getPixel(i, j - 1));
        int p3 = get01Pixel(img.getPixel(i + 1, j - 1));
        int p4 = get01Pixel(img.getPixel(i + 1, j));
        int p5 = get01Pixel(img.getPixel(i + 1, j + 1));
        int p6 = get01Pixel(img.getPixel(i, j + 1));
        int p7 = get01Pixel(img.getPixel(i - 1, j + 1));
        int p8 = get01Pixel(img.getPixel(i - 1, j));
        int p9 = get01Pixel(img.getPixel(i - 1, j - 1));

        if (p2 == 0 && p3 == 1)
            count++;

        if (p3 == 0 && p4 == 1)
            count++;

        if (p4 == 0 && p5 == 1)
            count++;

        if (p5 == 0 && p6 == 1)
            count++;

        if (p6 == 0 && p7 == 1)
            count++;

        if (p7 == 0 && p8 == 1)
            count++;

        if (p8 == 0 && p9 == 1)
            count++;

        if (p9 == 0 && p2 == 1)
            count++;

        return count;
    }

    private int getB(Bitmap img, int i, int j) {
        int p2 = get01Pixel(img.getPixel(i, j - 1));
        int p3 = get01Pixel(img.getPixel(i + 1, j - 1));
        int p4 = get01Pixel(img.getPixel(i + 1, j));
        int p5 = get01Pixel(img.getPixel(i + 1, j + 1));
        int p6 = get01Pixel(img.getPixel(i, j + 1));
        int p7 = get01Pixel(img.getPixel(i - 1, j + 1));
        int p8 = get01Pixel(img.getPixel(i - 1, j));
        int p9 = get01Pixel(img.getPixel(i - 1, j - 1));

        return p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9;
    }

    private int get01Pixel(int color) {
        int white = Color.rgb(255, 255, 255);
        return color == white ? 0 : 1;
    }

    public void getHilditchThinningImage(Bitmap img) {
        int a, b;
        boolean hasChange;

        int width = img.getWidth();
        int height = img.getHeight();

        getBlackAndWhite(img, 128);

        do {
            hasChange = false;

            for (int i = 1; (i + 2) < width; i++) {
                for (int j = 1; (j + 2) < height; j++) {
                    a = getA(img, i, j);
                    b = getB(img, i, j);

                    int p1 = get01Pixel(img.getPixel(i, j));
                    int p2 = get01Pixel(img.getPixel(i, j - 1));
                    int p4 = get01Pixel(img.getPixel(i + 1, j));
                    int p6 = get01Pixel(img.getPixel(i, j + 1));
                    int p8 = get01Pixel(img.getPixel(i - 1, j));

                    if (p1 == 1 && b >= 2 && b <= 6 && a == 1
                            && ((p2 * p4 * p8) == 0 || getA(img, i, j - 1) != 1)
                            && ((p2 * p4 * p6) == 0 || getA(img, i + 1, j) != 1))
                    {
                        int color = Color.rgb(255, 255, 255);
                        img.setPixel(i, j, color);

                        hasChange = true;
                    }
                }
            }
        } while (hasChange);
    }

    public void getZhangSuenThinningImage(Bitmap img) {
        int a, b;
        ArrayList<Point> pointsToChange = new ArrayList<>();
        boolean hasChange;

        int width = img.getWidth();
        int height = img.getHeight();

        getBlackAndWhite(img, 128);

        do {
            hasChange = false;

            for (int i = 1; (i + 1) < width; i++) {
                for (int j = 1; (j + 1) < height; j++) {
                    a = getA(img, i, j);
                    b = getB(img, i, j);

                    int p1 = get01Pixel(img.getPixel(i, j));
                    int p2 = get01Pixel(img.getPixel(i, j - 1));
                    int p4 = get01Pixel(img.getPixel(i + 1, j));
                    int p6 = get01Pixel(img.getPixel(i, j + 1));
                    int p8 = get01Pixel(img.getPixel(i - 1, j));

                    if (p1 == 1 && b >= 2 && b <= 6 && a == 1
                            && ((p2 * p4 * p6) == 0)
                            && ((p4 * p6 * p8) == 0))
                    {
                        pointsToChange.add(new Point(i, j));
                        hasChange = true;
                    }
                }
            }

            for (Point point : pointsToChange) {
                int color = Color.rgb(255, 255, 255);
                img.setPixel(point.getI(), point.getJ(), color);
            }

            pointsToChange.clear();

            for (int i = 1; (i + 1) < width; i++) {
                for (int j = 1; (j + 1) < height; j++) {
                    a = getA(img, i, j);
                    b = getB(img, i, j);

                    int p1 = get01Pixel(img.getPixel(i, j));
                    int p2 = get01Pixel(img.getPixel(i, j - 1));
                    int p4 = get01Pixel(img.getPixel(i + 1, j));
                    int p6 = get01Pixel(img.getPixel(i, j + 1));
                    int p8 = get01Pixel(img.getPixel(i - 1, j));

                    if (p1 == 1 && b >= 2 && b <= 6 && a == 1
                            && ((p2 * p4 * p8) == 0)
                            && ((p2 * p6 * p8) == 0))
                    {
                        pointsToChange.add(new Point(i, j));
                        hasChange = true;
                    }
                }
            }

            for (Point point : pointsToChange) {
                int color = Color.rgb(255, 255, 255);
                img.setPixel(point.getI(), point.getJ(), color);
            }

            pointsToChange.clear();
        } while (hasChange);
    }

    public void getNormalSkecthImage(Bitmap img) {
        getGrayMean(img);
        getSobelImage(img);
        getInvertImg(img);
    }

    public void getSketchImage(Bitmap img) {
        getGrayMean(img);

        Bitmap imgHorizontal = img.copy(img.getConfig(), true);
        Bitmap imgVertical = img.copy(img.getConfig(), true);

        double[][] kernelHorizontal = new double[9][9];
        double[][] kernelVertical = new double[9][9];

        kernelHorizontal[0][0] = -1;
        kernelHorizontal[0][1] = 0;
        kernelHorizontal[0][2] = 0;
        kernelHorizontal[0][3] = 0;
        kernelHorizontal[0][4] = 0;
        kernelHorizontal[0][5] = 0;
        kernelHorizontal[0][6] = 0;
        kernelHorizontal[0][7] = 0;
        kernelHorizontal[0][8] = 1;

        kernelHorizontal[1][0] = -2;
        kernelHorizontal[1][1] = 0;
        kernelHorizontal[1][2] = 0;
        kernelHorizontal[1][3] = 0;
        kernelHorizontal[1][4] = 0;
        kernelHorizontal[1][5] = 0;
        kernelHorizontal[1][6] = 0;
        kernelHorizontal[1][7] = 0;
        kernelHorizontal[1][8] = 2;

        kernelHorizontal[2][0] = -3;
        kernelHorizontal[2][1] = 0;
        kernelHorizontal[2][2] = 0;
        kernelHorizontal[2][3] = 0;
        kernelHorizontal[2][4] = 0;
        kernelHorizontal[2][5] = 0;
        kernelHorizontal[2][6] = 0;
        kernelHorizontal[2][7] = 0;
        kernelHorizontal[2][8] = 3;

        kernelHorizontal[3][0] = -4;
        kernelHorizontal[3][1] = 0;
        kernelHorizontal[3][2] = 0;
        kernelHorizontal[3][3] = 0;
        kernelHorizontal[3][4] = 0;
        kernelHorizontal[3][5] = 0;
        kernelHorizontal[3][6] = 0;
        kernelHorizontal[3][7] = 0;
        kernelHorizontal[3][8] = 4;

        kernelHorizontal[4][0] = -5;
        kernelHorizontal[4][1] = 0;
        kernelHorizontal[4][2] = 0;
        kernelHorizontal[4][3] = 0;
        kernelHorizontal[4][4] = 0;
        kernelHorizontal[4][5] = 0;
        kernelHorizontal[4][6] = 0;
        kernelHorizontal[4][7] = 0;
        kernelHorizontal[4][8] = 5;

        kernelHorizontal[5][0] = -4;
        kernelHorizontal[5][1] = 0;
        kernelHorizontal[5][2] = 0;
        kernelHorizontal[5][3] = 0;
        kernelHorizontal[5][4] = 0;
        kernelHorizontal[5][5] = 0;
        kernelHorizontal[5][6] = 0;
        kernelHorizontal[5][7] = 0;
        kernelHorizontal[5][8] = 4;

        kernelHorizontal[6][0] = -3;
        kernelHorizontal[6][1] = 0;
        kernelHorizontal[6][2] = 0;
        kernelHorizontal[6][3] = 0;
        kernelHorizontal[6][4] = 0;
        kernelHorizontal[6][5] = 0;
        kernelHorizontal[6][6] = 0;
        kernelHorizontal[6][7] = 0;
        kernelHorizontal[6][8] = 3;

        kernelHorizontal[7][0] = -2;
        kernelHorizontal[7][1] = 0;
        kernelHorizontal[7][2] = 0;
        kernelHorizontal[7][3] = 0;
        kernelHorizontal[7][4] = 0;
        kernelHorizontal[7][5] = 0;
        kernelHorizontal[7][6] = 0;
        kernelHorizontal[7][7] = 0;
        kernelHorizontal[7][8] = 2;

        kernelHorizontal[8][0] = -1;
        kernelHorizontal[8][1] = 0;
        kernelHorizontal[8][2] = 0;
        kernelHorizontal[8][3] = 0;
        kernelHorizontal[8][4] = 0;
        kernelHorizontal[8][5] = 0;
        kernelHorizontal[8][6] = 0;
        kernelHorizontal[8][7] = 0;
        kernelHorizontal[8][8] = 1;

        kernelVertical[0][0] = -1;
        kernelVertical[0][1] = -2;
        kernelVertical[0][2] = -3;
        kernelVertical[0][3] = -4;
        kernelVertical[0][4] = -5;
        kernelVertical[0][5] = -4;
        kernelVertical[0][6] = -3;
        kernelVertical[0][7] = -2;
        kernelVertical[0][8] = -1;

        kernelVertical[1][0] = 0;
        kernelVertical[1][1] = 0;
        kernelVertical[1][2] = 0;
        kernelVertical[1][3] = 0;
        kernelVertical[1][4] = 0;
        kernelVertical[1][5] = 0;
        kernelVertical[1][6] = 0;
        kernelVertical[1][7] = 0;
        kernelVertical[1][8] = 0;

        kernelVertical[2][0] = 0;
        kernelVertical[2][1] = 0;
        kernelVertical[2][2] = 0;
        kernelVertical[2][3] = 0;
        kernelVertical[2][4] = 0;
        kernelVertical[2][5] = 0;
        kernelVertical[2][6] = 0;
        kernelVertical[2][7] = 0;
        kernelVertical[2][8] = 0;

        kernelVertical[3][0] = 0;
        kernelVertical[3][1] = 0;
        kernelVertical[3][2] = 0;
        kernelVertical[3][3] = 0;
        kernelVertical[3][4] = 0;
        kernelVertical[3][5] = 0;
        kernelVertical[3][6] = 0;
        kernelVertical[3][7] = 0;
        kernelVertical[3][8] = 0;

        kernelVertical[4][0] = 0;
        kernelVertical[4][1] = 0;
        kernelVertical[4][2] = 0;
        kernelVertical[4][3] = 0;
        kernelVertical[4][4] = 0;
        kernelVertical[4][5] = 0;
        kernelVertical[4][6] = 0;
        kernelVertical[4][7] = 0;
        kernelVertical[4][8] = 0;

        kernelVertical[5][0] = 0;
        kernelVertical[5][1] = 0;
        kernelVertical[5][2] = 0;
        kernelVertical[5][3] = 0;
        kernelVertical[5][4] = 0;
        kernelVertical[5][5] = 0;
        kernelVertical[5][6] = 0;
        kernelVertical[5][7] = 0;
        kernelVertical[5][8] = 0;

        kernelVertical[6][0] = 0;
        kernelVertical[6][1] = 0;
        kernelVertical[6][2] = 0;
        kernelVertical[6][3] = 0;
        kernelVertical[6][4] = 0;
        kernelVertical[6][5] = 0;
        kernelVertical[6][6] = 0;
        kernelVertical[6][7] = 0;
        kernelVertical[6][8] = 0;

        kernelVertical[7][0] = 0;
        kernelVertical[7][1] = 0;
        kernelVertical[7][2] = 0;
        kernelVertical[7][3] = 0;
        kernelVertical[7][4] = 0;
        kernelVertical[7][5] = 0;
        kernelVertical[7][6] = 0;
        kernelVertical[7][7] = 0;
        kernelVertical[7][8] = 0;

        kernelVertical[8][0] = 1;
        kernelVertical[8][1] = 2;
        kernelVertical[8][2] = 3;
        kernelVertical[8][3] = 4;
        kernelVertical[8][4] = 5;
        kernelVertical[8][5] = 4;
        kernelVertical[8][6] = 3;
        kernelVertical[8][7] = 2;
        kernelVertical[8][8] = 1;

        getFilterImage2(imgHorizontal, kernelHorizontal);
        getFilterImage2(imgVertical, kernelVertical);

        addBitmap(imgHorizontal, imgVertical, img);
        getInvertImg(img);
    }

    public void getNumPatternMatching(Bitmap img, Bitmap imgTemplate) {
        int width = img.getWidth();
        int height = img.getHeight();
        int widthTemplate = imgTemplate.getWidth();
        int heightTemplate = imgTemplate.getHeight();

        getGrayAutoLevelImage(img);
        getGrayAutoLevelImage(imgTemplate);

        Bitmap newImg = Bitmap.createBitmap(width, height, img.getConfig());

        for (int i = 0; i < (width - widthTemplate); i += widthTemplate) {
            for (int j = 0; j < (height - heightTemplate); j += heightTemplate) {
                int x, y;

                for (x = 0; x < widthTemplate; x++) {
                    for (y = 0; y < heightTemplate; y++) {
                        int result;

                        int color = img.getPixel((i + x), (j + y));
                        int colorTemplate = imgTemplate.getPixel(x, y);

                        int r = Color.red(color);
                        int rTemplate = Color.red(colorTemplate);

                        result = rTemplate - r;
//                        result = (int) Math.sqrt(result);
//                        result = pixelNormalization(result);
                        result = bwPixelNormalization(result, 200);
                        result = Color.rgb(result, result, result);

                        newImg.setPixel((i + x), (j + y), result);
                    }
                }

//                int color = img.getPixel(i, j);
//                int colorTemplate = imgTemplate.getPixel(x, y);
//
//                int r = Color.red(color);
//                int rTemplate = Color.red(colorTemplate);
//
//                int result = rTemplate - r;
//                int newColor = Color.rgb(result, result, result);
//
//                y++;
//
//                if (y == heightTemplate)
//                    y = 0;
            }
//
//            x++;
//
//            if (x == widthTemplate)
//                x = 0;
        }

        copyBitmap(newImg, img);

//        int result = 0;
//
//        for (int i = 0; i < width; i++) {
//            for (int j = 0; j < height; j++) {
//                int color = newImg.getPixel(i, j);
//                int r = Color.red(color);
//
//                if (r == 255)
//                    result++;
//            }
//        }

//        return result;
    }

    private void initializeArray0(int[] temp) {
        for (int i = 0; i < temp.length; i++)
            temp[i] = 0;
    }

    private void getHistProjection(Bitmap img, int[] temp) {
        int width = img.getWidth();
        int height = img.getHeight();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int color = img.getPixel(i, j);
                int gray = Color.red(color);
                int biner = gray / 255;

                temp[i] += biner == 0 ? 1 : 0;
            }
        }
    }

    public int getNumMatch(Activity activity, Bitmap img) {
        int width = img.getWidth();

        int[] tempImg = new int[width];
        int[] temp0 = new int[width];
        int[] temp1 = new int[width];
        int[] temp2 = new int[width];
        int[] temp3 = new int[width];
        int[] temp4 = new int[width];
        int[] temp5 = new int[width];
        int[] temp6 = new int[width];
        int[] temp7 = new int[width];
        int[] temp8 = new int[width];
        int[] temp9 = new int[width];

        int[][] temp = {
                temp0,
                temp1,
                temp2,
                temp3,
                temp4,
                temp5,
                temp6,
                temp7,
                temp8,
                temp9,
        };

        int[] tempMin = new int[temp.length];

        initializeArray0(tempMin);
        initializeArray0(tempImg);
        initializeArray0(temp0);
        initializeArray0(temp1);
        initializeArray0(temp2);
        initializeArray0(temp3);
        initializeArray0(temp4);
        initializeArray0(temp5);
        initializeArray0(temp6);
        initializeArray0(temp7);
        initializeArray0(temp8);
        initializeArray0(temp9);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 3;
        options.inMutable = true;

        Bitmap num0 = BitmapFactory.decodeResource(activity.getResources(), R.drawable.num_0, options);
        Bitmap num1 = BitmapFactory.decodeResource(activity.getResources(), R.drawable.num_1, options);
        Bitmap num2 = BitmapFactory.decodeResource(activity.getResources(), R.drawable.num_2, options);
        Bitmap num3 = BitmapFactory.decodeResource(activity.getResources(), R.drawable.num_3, options);
        Bitmap num4 = BitmapFactory.decodeResource(activity.getResources(), R.drawable.num_4, options);
        Bitmap num5 = BitmapFactory.decodeResource(activity.getResources(), R.drawable.num_5, options);
        Bitmap num6 = BitmapFactory.decodeResource(activity.getResources(), R.drawable.num_6, options);
        Bitmap num7 = BitmapFactory.decodeResource(activity.getResources(), R.drawable.num_7, options);
        Bitmap num8 = BitmapFactory.decodeResource(activity.getResources(), R.drawable.num_8, options);
        Bitmap num9 = BitmapFactory.decodeResource(activity.getResources(), R.drawable.num_9, options);

        getBlackAndWhite(img, 128);
        getBlackAndWhite(num0, 128);
        getBlackAndWhite(num1, 128);
        getBlackAndWhite(num2, 128);
        getBlackAndWhite(num3, 128);
        getBlackAndWhite(num4, 128);
        getBlackAndWhite(num5, 128);
        getBlackAndWhite(num6, 128);
        getBlackAndWhite(num7, 128);
        getBlackAndWhite(num8, 128);
        getBlackAndWhite(num9, 128);

        getHistProjection(img, tempImg);
        getHistProjection(num0, temp0);
        getHistProjection(num1, temp1);
        getHistProjection(num2, temp2);
        getHistProjection(num3, temp3);
        getHistProjection(num4, temp4);
        getHistProjection(num5, temp5);
        getHistProjection(num6, temp6);
        getHistProjection(num7, temp7);
        getHistProjection(num8, temp8);
        getHistProjection(num9, temp9);

        for (int i = 0; i < temp.length; i++) {
            for (int j = 0; j < temp[i].length; j++) {
                tempMin[i] += Math.abs(tempImg[j] - temp[i][j]);
            }
        }

        int minIndex = 0;
        int min = tempMin[minIndex];

        for (int i = 1; i < tempMin.length; i++) {
            if (min > tempMin[i]) {
                min = tempMin[i];
                minIndex = i;
            }
        }

        return minIndex;
    }

    public void getLevitationImage(Bitmap imgBackground, Bitmap imgWithoutItem, Bitmap imgWithItem, Bitmap imgLevitation) {
        int width = imgBackground.getWidth();
        int height = imgBackground.getHeight();

        getAutoLevelImage(imgBackground);
        getAutoLevelImage(imgWithoutItem);
        getAutoLevelImage(imgWithItem);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int colorBackground = imgBackground.getPixel(i, j);
                int colorWithItem = imgWithItem.getPixel(i, j);
                int colorWithoutItem = imgWithoutItem.getPixel(i, j);

                int red = (Color.red(colorWithItem) - Color.red(colorWithoutItem));
                int green = (Color.green(colorWithItem) - Color.green(colorWithoutItem));
                int blue = (Color.blue(colorWithItem) - Color.blue(colorWithoutItem));

                red += Color.red(colorBackground);
                green += Color.green(colorBackground);
                blue += Color.blue(colorBackground);

//                red = pixelNormalization(red);
//                green = pixelNormalization(green);
//                blue = pixelNormalization(blue);
//
//                if (red == 0 || green == 0 || blue == 0) {
//                    red = Color.red(colorWithItem);
//                    green = Color.green(colorWithItem);
//                    blue = Color.blue(colorWithItem);
//                } else {
//                    red += Color.red(colorBackground);
//                    green += Color.green(colorBackground);
//                    blue += Color.blue(colorBackground);
//                }

                int color = Color.rgb(red, green, blue);

                imgLevitation.setPixel(i, j, color);
            }
        }
    }

    public int detectionOfTomatoMaturity(Activity activity, Bitmap img) {
        int[] hue = new int[361];
        int[] hue1 = new int[361];
        int[] hue2 = new int[361];
        int[] hue3 = new int[361];

        int[] saturation = new int[101];
        int[] value = new int[101];

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 3;

        Bitmap tomato1 = BitmapFactory.decodeResource(activity.getResources(), R.drawable.tomato_1, options);
        Bitmap tomato2 = BitmapFactory.decodeResource(activity.getResources(), R.drawable.tomato_2, options);
        Bitmap tomato3 = BitmapFactory.decodeResource(activity.getResources(), R.drawable.tomato_3, options);

        setHSVHistogram(tomato1, hue1, saturation, value);
        setHSVHistogram(tomato2, hue2, saturation, value);
        setHSVHistogram(tomato3, hue3, saturation, value);
        setHSVHistogram(img, hue, saturation, value);

        int red = 0;
        int green = 0;
        int orange = 0;

        for (int i = 0; i < hue.length; i++) {
            int[] v = {
                    hue1[i],
                    hue2[i],
                    hue3[i]
            };

            Arrays.sort(v);

            hue[i] -= v[0];

//            Log.d("Hue - " + i, hue[i] + "");
        }

        for (int i = 0; i <= 40; i++)
            red += hue[i];

        for (int i = 70; i <= 110; i++)
            green += hue[i];

        for (int i = 30; i <= 70; i++)
            orange += hue[i];

        int[] result = {
                red,
                green,
                orange
        };

        Arrays.sort(result);

        if (result[2] == red)
            return 1;
        else if (result[2] == green)
            return -1;
        else
            return 0;
    }

    public void getKMeansClusterImage(Bitmap img, int numberOfCluster) {
        int width = img.getWidth();
        int height = img.getHeight();

        int numberOfLayer = 3;
        int numberOfColumn = (width * height) * numberOfLayer;

        int[][] rgbModel = new int[numberOfLayer][numberOfColumn];
        int[][] clusterColor = new int[numberOfCluster][numberOfLayer];
        int[] cluster = new int[numberOfColumn];

        int num = 0;

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int color = img.getPixel(i, j);

                int red = Color.red(color);
                int green = Color.green(color);
                int blue = Color.blue(color);

                rgbModel[0][num] = red;
                rgbModel[1][num] = green;
                rgbModel[2][num] = blue;

                num++;
            }
        }

        for (int i = 0; i < numberOfCluster; i++) {
            for (int j = 0; j < numberOfLayer; j++)
                clusterColor[i][j] = (int) Math.floor(Math.random() * 255);
        }

        for (int i = 0; i < numberOfColumn; i++) {
            int[] diff = new int[numberOfCluster];

            for (int j = 0; j < numberOfCluster; j++) {
                int sum = 0;

                for (int k = 0; k < numberOfLayer; k++)
                    sum += Math.abs(clusterColor[j][k] - rgbModel[k][i]);

                diff[j] = sum;
            }

            int min = diff[0];
            int minIndex = 0;

            for (int j = 0; j < numberOfCluster; j++) {
                if (diff[j] <= min) {
                    min = diff[j];
                    minIndex = j;
                }
            }

            cluster[i] = minIndex;
        }

        int[] sum = new int[numberOfCluster];

        for (int i = 0; i < numberOfCluster; i++) {
            for (int j = 0; j < numberOfLayer; j++)
                clusterColor[i][j] = 0;

            sum[i] = 0;
        }

        for (int i = 0; i < numberOfColumn; i++) {
            int index = cluster[i];

            for (int j = 0; j < numberOfLayer; j++)
                clusterColor[index][j] += rgbModel[j][i];

            sum[index] = sum[index] + 1;
        }

        for (int i = 0; i < numberOfCluster; i++) {
            if (sum[i] != 0) {
                for (int j = 0; j < numberOfLayer; j++)
                    clusterColor[i][j] /= sum[i];
            }
        }

        num = 0;

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int index = cluster[num];

                int red = clusterColor[index][0];
                int green = clusterColor[index][1];
                int blue = clusterColor[index][2];

                int color = Color.rgb(red, green, blue);

                img.setPixel(i, j, color);

                num++;
            }
        }
    }

    private int pixelNormalization(int color) {
        if (color < 0)
            color = 0;
        else if (color > 255)
            color = 255;

        return color;
    }

    private int minusPixelNormalization(int color) {
        if (color < 0)
            color = Math.abs(color);
        else if (color > 255)
            color = 255;

        return color;
    }

    private int bwPixelNormalization(int color, int threshold) {
        if (color >= threshold)
            color = 255;
        else
            color = 0;

        return color;
    }

    public void changeRotation(Bitmap img, int angle) {
        int width = img.getWidth();
        int height = img.getHeight();

        double radiansAngle = Math.toRadians(angle);

        Bitmap newImg = Bitmap.createBitmap(width, height, img.getConfig());

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int color = img.getPixel(i, j);

                int newI = (int) (Math.cos(radiansAngle) * i - Math.sin(radiansAngle) * j);
                int newJ = (int) (Math.sin(radiansAngle) * i + Math.cos(radiansAngle) * j);


            }
        }

        copyBitmap(newImg, img);
    }

    public void changeBrightness(Bitmap img, int changeBrightness) {
        int width = img.getWidth();
        int height = img.getHeight();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int color = img.getPixel(i, j);

                int r = pixelNormalization(Color.red(color) + changeBrightness);
                int g = pixelNormalization(Color.green(color) + changeBrightness);
                int b = pixelNormalization(Color.blue(color) + changeBrightness);

                color = Color.rgb(r, g, b);

                img.setPixel(i, j, color);
            }
        }
    }

    public void changeContrast(Bitmap img, double changeContrast) {
        int width = img.getWidth();
        int height = img.getHeight();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int color = img.getPixel(i, j);

                int r = pixelNormalization((int) (Color.red(color) * changeContrast));
                int g = pixelNormalization((int) (Color.green(color) * changeContrast));
                int b = pixelNormalization((int) (Color.blue(color) * changeContrast));

                color = Color.rgb(r, g, b);

                img.setPixel(i, j, color);
            }
        }
    }

    public void addGaussianNoise(Bitmap img, int percentage) {
        int width = img.getWidth();
        int height = img.getHeight();

        Random random = new Random();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int color = img.getPixel(i, j);
                int randNumber = random.nextInt(100);

                int r = Color.red(color);
                int g = Color.green(color);
                int b = Color.blue(color);

                if (randNumber < percentage) {
                    randNumber = random.nextInt(256) - 128;

                    r = pixelNormalization(r + randNumber);
                    g = pixelNormalization(g + randNumber);
                    b = pixelNormalization(b + randNumber);
                }

                color = Color.rgb(r, g, b);

                img.setPixel(i, j, color);
            }
        }
    }

    public void addSpeckleNoise(Bitmap img, int percentage) {
        int width = img.getWidth();
        int height = img.getHeight();

        Random random = new Random();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int color = img.getPixel(i, j);
                int randNumber = random.nextInt(100);

                int r = Color.red(color);
                int g = Color.green(color);
                int b = Color.blue(color);

                if (randNumber < percentage) {
                    r = 0;
                    g = 0;
                    b = 0;
                }

                color = Color.rgb(r, g, b);

                img.setPixel(i, j, color);
            }
        }
    }

    public void addSaltPepperNoise(Bitmap img, int percentage) {
        int width = img.getWidth();
        int height = img.getHeight();

        Random random = new Random();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int color = img.getPixel(i, j);
                int randNumber = random.nextInt(100);

                int r = Color.red(color);
                int g = Color.green(color);
                int b = Color.blue(color);

                if (randNumber < percentage) {
                    r = 255;
                    g = 255;
                    b = 255;
                }

                color = Color.rgb(r, g, b);

                img.setPixel(i, j, color);
            }
        }
    }

    public void setRGBHistogram(Bitmap img, int[] rL, int[] gL, int[] bL) {
        int width = img.getWidth();
        int height = img.getHeight();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int color = img.getPixel(i, j);

                int r = Color.red(color);
                int g = Color.green(color);
                int b = Color.blue(color);

                rL[r] += 1;
                gL[g] += 1;
                bL[b] += 1;
            }
        }
    }

    public void setColorHistogram(int[] color, int[] oneL, int[] twoL, int[] threeL) {
        int i = 0;

        i = setColorHistogramArray(i, color, oneL);
        i = setColorHistogramArray(i, color, twoL);
        setColorHistogramArray(i, color, threeL);
    }

    private int setColorHistogramArray(int i, int[] colorAll, int[] color) {
        int length = color.length;
        int max = i + length;
        int colorIndex = 0;

        for ( ; i < max; i++) {
            colorAll[i] = color[colorIndex];
            colorIndex++;
        }

        return i;
    }

    public void setHSVHistogram(Bitmap img, int[] hL, int[] sL, int[] vL) {
        int width = img.getWidth();
        int height = img.getHeight();

        float[] hsv = new float[3];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int color = img.getPixel(i, j);

                Color.colorToHSV(color, hsv);

                int h = (int) hsv[0];
                int s = (int) (hsv[1] * 100);
                int v = (int) (hsv[2] * 100);

//                Log.d("Hsv", h + " " + s + " " + v);

                hL[h] += 1;
                sL[s] += 1;
                vL[v] += 1;
            }
        }
    }

    public void setRGBCDF(Bitmap img, int[] rL, int[] gL, int[] bL) {
        setRGBHistogram(img, rL, gL, bL);

        int[] rLTotal = Arrays.copyOf(rL, rL.length);
        int[] gLTotal = Arrays.copyOf(gL, gL.length);
        int[] bLTotal = Arrays.copyOf(bL, bL.length);

        for (int i = 1; i < rLTotal.length; i++) {
            rL[i] = rL[i - 1] + rLTotal[i];
            gL[i] = gL[i - 1] + gLTotal[i];
            bL[i] = bL[i - 1] + bLTotal[i];
        }
    }

    public void setHSVCDF(Bitmap img, int[] hL, int[] sL, int[] vL) {
        setHSVHistogram(img, hL, sL, vL);

        int[] hLTotal = Arrays.copyOf(hL, hL.length);
        int[] sLTotal = Arrays.copyOf(sL, sL.length);
        int[] vLTotal = Arrays.copyOf(vL, vL.length);

        for (int i = 1; i < hLTotal.length; i++)
            hL[i] = hL[i - 1] + hLTotal[i];

        for (int i = 1; i < sLTotal.length; i++) {
            sL[i] = sL[i - 1] + sLTotal[i];
            vL[i] = vL[i - 1] + vLTotal[i];
        }
    }

    public void setColorCDF(int[] rgb, int[] rL, int[] gL, int[] bL) {
        int i = 0;

        i = setColorCDFArray(i, rgb, rL);
        i = setColorCDFArray(i, rgb, gL);
        setColorCDFArray(i, rgb, bL);
    }

    private int setColorCDFArray(int i, int[] rgb, int[] color) {
        int length = color.length;
        int max = i + length;
        int colorIndex = 0;

        if (i == 0)
            rgb[i] = color[i];

        for ( ; i < max; i++) {
            rgb[i + 1] = rgb[i] + color[colorIndex];
            colorIndex++;
        }

        return i;
    }
}