package com.faza.project.pencit.Activity;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.faza.project.pencit.ImageProcessing.ImageProcessing;
import com.faza.project.pencit.R;
import com.faza.project.pencit.Session.StaticBitmap;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class HistogramActivity extends AppCompatActivity {

    private int[] colorL, oneL, twoL, threeL;
    private DataPoint[] colorD, oneD, twoD, threeD;
    private GraphView gvImg, gvOne, gvTwo, gvThree;
    private ImageProcessing imageProcessing;
    private TextView tvLayerOne, tvLayerTwo, tvLayerThree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_histogram);

        String title = getIntent().getExtras().getString("Title");

        Toolbar tbImg = (Toolbar) findViewById(R.id.tb_hist);
        setSupportActionBar(tbImg);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        TextView tvTitle = (TextView) findViewById(R.id.tv_title);
        tvTitle.setText(title);

        imageProcessing = new ImageProcessing();

        gvImg = (GraphView) findViewById(R.id.gv_img);
        gvOne = (GraphView) findViewById(R.id.gv_one);
        gvTwo = (GraphView) findViewById(R.id.gv_two);
        gvThree = (GraphView) findViewById(R.id.gv_three);

        tvLayerOne = (TextView) findViewById(R.id.tv_layer_one);
        tvLayerTwo = (TextView) findViewById(R.id.tv_layer_two);
        tvLayerThree = (TextView) findViewById(R.id.tv_layer_three);

        setRGBHistogram();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.histogram_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_rgb_histogram:
                setRGBHistogram();
                return true;
            case R.id.menu_hsv_histogram:
                setHSVHistogram();
                return true;
            case R.id.menu_rgb_cdf:
                setRGBCDF();
                return true;
            case R.id.menu_hsv_cdf:
                setHSVCDF();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void arrayInitialization(int type) {
        switch (type) {
            case 1: // rgb
                oneL = new int[256];
                twoL = new int[256];
                threeL = new int[256];
                break;
            case 2: // hsv
                oneL = new int[361];
                twoL = new int[101];
                threeL = new int[101];
                break;
        }

        int totalLength = (oneL.length + twoL.length + threeL.length) + 1;
        colorL = new int[totalLength];
    }

    private void setGraphTitle(int type) {
        String layerOne = null;
        String layerTwo = null;
        String layerThree = null;

        switch (type) {
            case 1: // rgb
                layerOne = "Layer " + getString(R.string.red);
                layerTwo = "Layer " + getString(R.string.green);
                layerThree = "Layer " + getString(R.string.blue);
                break;
            case 2: // hsv
                layerOne = "Layer " + getString(R.string.hue);
                layerTwo = "Layer " + getString(R.string.saturation);
                layerThree = "Layer " + getString(R.string.value);
                break;
        }

        tvLayerOne.setText(layerOne);
        tvLayerTwo.setText(layerTwo);
        tvLayerThree.setText(layerThree);
    }

    private void setRGBHistogram() {
        arrayInitialization(1);
        setGraphTitle(1);

        imageProcessing.setRGBHistogram(StaticBitmap.image, oneL, twoL, threeL);
        imageProcessing.setColorHistogram(colorL, oneL, twoL, threeL);

        setDataPoint();
        setGraph();
    }

    private void setHSVHistogram() {
        arrayInitialization(2);
        setGraphTitle(2);

        imageProcessing.setHSVHistogram(StaticBitmap.image, oneL, twoL, threeL);
        imageProcessing.setColorHistogram(colorL, oneL, twoL, threeL);

        setDataPoint();
        setGraph();
    }

    private void setRGBCDF() {
        arrayInitialization(1);
        setGraphTitle(1);

        imageProcessing.setRGBCDF(StaticBitmap.image, oneL, twoL, threeL);
        imageProcessing.setColorCDF(colorL, oneL, twoL, threeL);

        setDataPoint();
        setGraph();
    }

    private void setHSVCDF() {
        arrayInitialization(2);
        setGraphTitle(2);

        imageProcessing.setHSVCDF(StaticBitmap.image, oneL, twoL, threeL);
        imageProcessing.setColorCDF(colorL, oneL, twoL, threeL);

        setDataPoint();
        setGraph();
    }

    private void setDataPoint() {
        colorD = new DataPoint[colorL.length];
        oneD = new DataPoint[oneL.length];
        twoD = new DataPoint[twoL.length];
        threeD = new DataPoint[threeL.length];

        saveDataPoint(colorD, colorL, colorD.length);
        saveDataPoint(oneD, oneL, oneD.length);
        saveDataPoint(twoD, twoL, twoD.length);
        saveDataPoint(threeD, threeL, threeD.length);
    }

    private void saveDataPoint(DataPoint[] dataPoints, int[] points, int max) {
        for (int i = 0; i < max; i++) {
            DataPoint dataPoint = new DataPoint(i, points[i]);
            dataPoints[i] = dataPoint;
        }
    }

    private void setGraph() {
        LineGraphSeries<DataPoint> rgbSeries = new LineGraphSeries<>(colorD);
        LineGraphSeries<DataPoint> redSeries = new LineGraphSeries<>(oneD);
        LineGraphSeries<DataPoint> greenSeries = new LineGraphSeries<>(twoD);
        LineGraphSeries<DataPoint> blueSeries = new LineGraphSeries<>(threeD);

        rgbSeries.setColor(ContextCompat.getColor(HistogramActivity.this, R.color.colorAccent));
        redSeries.setColor(ContextCompat.getColor(HistogramActivity.this, R.color.colorRed));
        greenSeries.setColor(ContextCompat.getColor(HistogramActivity.this, R.color.colorGreen));
        blueSeries.setColor(ContextCompat.getColor(HistogramActivity.this, R.color.colorBlue));

        gvImg.getViewport().setXAxisBoundsManual(true);
        gvImg.getViewport().setMinX(0);
        gvImg.getViewport().setMaxX(colorD.length);

        gvOne.getViewport().setXAxisBoundsManual(true);
        gvOne.getViewport().setMinX(0);
        gvOne.getViewport().setMaxX(oneD.length);

        gvTwo.getViewport().setXAxisBoundsManual(true);
        gvTwo.getViewport().setMinX(0);
        gvTwo.getViewport().setMaxX(twoD.length);

        gvThree.getViewport().setXAxisBoundsManual(true);
        gvThree.getViewport().setMinX(0);
        gvThree.getViewport().setMaxX(threeD.length);

        gvImg.getViewport().setScalable(true);
        gvOne.getViewport().setScalable(true);
        gvTwo.getViewport().setScalable(true);
        gvThree.getViewport().setScalable(true);

        gvImg.removeAllSeries();
        gvOne.removeAllSeries();
        gvTwo.removeAllSeries();
        gvThree.removeAllSeries();

        gvImg.addSeries(rgbSeries);
        gvOne.addSeries(redSeries);
        gvTwo.addSeries(greenSeries);
        gvThree.addSeries(blueSeries);
    }
}