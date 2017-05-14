package com.faza.project.pencit.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.faza.project.pencit.R;
import com.faza.project.pencit.Thread.AdapterThread;
import com.faza.project.pencit.ViewHolder.ImgViewHolder;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Dibuat oleh Faza Zulfika Permana Putra
 */

public class ImgAdapter extends RecyclerView.Adapter<ImgViewHolder> {

    private ArrayList<String> menuList;
    private Context context;
    private ImageView ivImg;
    private LayoutInflater inflater;
    private TextView tvTitle;

    public ImgAdapter(Context context, String[] menuList, ImageView ivImg, TextView tvTitle) {
        this(context, new ArrayList<>(Arrays.asList(menuList)), ivImg, tvTitle);
    }

    private ImgAdapter(Context context, ArrayList<String> menuList, ImageView ivImg, TextView tvTitle) {
        this.menuList = menuList;
        this.context = context;
        this.ivImg = ivImg;
        this.tvTitle = tvTitle;

        inflater = LayoutInflater.from(context);
    }

    @Override
    public ImgViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.rv_img, parent, false);
        return new ImgViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ImgViewHolder holder, int position) {
        String menu = menuList.get(position);

        AdapterThread adapterThread = new AdapterThread(context, menu, ivImg, tvTitle, holder);
        adapterThread.execute(position);
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }
}