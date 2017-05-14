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

    private int[] rgbL, rL, gL, bL;
    private DataPoint[] rgbD, rD, gD, bD;
    private GraphView gvImg, gvRed, gvGreen, gvBlue;
    private ImageProcessing imageProcessing;

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
        gvRed = (GraphView) findViewById(R.id.gv_red);
        gvGreen = (GraphView) findViewById(R.id.gv_green);
        gvBlue = (GraphView) findViewById(R.id.gv_blue);

        setHistogram();
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
            case R.id.menu_histogram:
                setHistogram();
                return true;
            case R.id.menu_cdf:
                setCDF();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void arrayInitialization() {
        rL = new int[256];
        gL = new int[256];
        bL = new int[256];

        int rgbLength = (rL.length + gL.length + bL.length) + 1;
        rgbL = new int[rgbLength];
    }

    private void setHistogram() {
        arrayInitialization();

        imageProcessing.setHistogram(StaticBitmap.image, rL, gL, bL);
        imageProcessing.setColorHistogram(rgbL, rL, gL, bL);

        setDataPoint();
        setGraph();
    }

    private void setCDF() {
        arrayInitialization();

        imageProcessing.setCDF(StaticBitmap.image, rL, gL, bL);
        imageProcessing.setColorCDF(rgbL, rL, gL, bL);

        setDataPoint();
        setGraph();
    }

    private void setDataPoint() {
        rgbD = new DataPoint[rgbL.length];
        rD = new DataPoint[rL.length];
        gD = new DataPoint[gL.length];
        bD = new DataPoint[bL.length];

        saveDataPoint(rgbD, rgbL, rgbD.length);
        saveDataPoint(rD, rL, rD.length);
        saveDataPoint(gD, gL, gD.length);
        saveDataPoint(bD, bL, bD.length);
    }

    private void saveDataPoint(DataPoint[] dataPoints, int[] points, int max) {
        for (int i = 0; i < max; i++) {
            DataPoint dataPoint = new DataPoint(i, points[i]);
            dataPoints[i] = dataPoint;
        }
    }

    private void setGraph() {
        LineGraphSeries<DataPoint> rgbSeries = new LineGraphSeries<>(rgbD);
        LineGraphSeries<DataPoint> redSeries = new LineGraphSeries<>(rD);
        LineGraphSeries<DataPoint> greenSeries = new LineGraphSeries<>(gD);
        LineGraphSeries<DataPoint> blueSeries = new LineGraphSeries<>(bD);

        rgbSeries.setColor(ContextCompat.getColor(HistogramActivity.this, R.color.colorAccent));
        redSeries.setColor(ContextCompat.getColor(HistogramActivity.this, R.color.colorRed));
        greenSeries.setColor(ContextCompat.getColor(HistogramActivity.this, R.color.colorGreen));
        blueSeries.setColor(ContextCompat.getColor(HistogramActivity.this, R.color.colorBlue));

        gvImg.getViewport().setXAxisBoundsManual(true);
        gvImg.getViewport().setMinX(0);
        gvImg.getViewport().setMaxX(rgbD.length);

        gvRed.getViewport().setXAxisBoundsManual(true);
        gvRed.getViewport().setMinX(0);
        gvRed.getViewport().setMaxX(rD.length);

        gvGreen.getViewport().setXAxisBoundsManual(true);
        gvGreen.getViewport().setMinX(0);
        gvGreen.getViewport().setMaxX(gD.length);

        gvBlue.getViewport().setXAxisBoundsManual(true);
        gvBlue.getViewport().setMinX(0);
        gvBlue.getViewport().setMaxX(bD.length);

        gvImg.getViewport().setScalable(true);
        gvRed.getViewport().setScalable(true);
        gvGreen.getViewport().setScalable(true);
        gvBlue.getViewport().setScalable(true);

        gvImg.removeAllSeries();
        gvRed.removeAllSeries();
        gvGreen.removeAllSeries();
        gvBlue.removeAllSeries();

        gvImg.addSeries(rgbSeries);
        gvRed.addSeries(redSeries);
        gvGreen.addSeries(greenSeries);
        gvBlue.addSeries(blueSeries);
    }
}