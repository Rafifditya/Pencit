package com.faza.project.pencit.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.faza.project.pencit.R;

/**
 * Dibuat oleh Faza Zulfika Permana Putra
 */

public class ImgViewHolder extends RecyclerView.ViewHolder {

    private ImageView ivImg;
    private ProgressBar pbImg;

    public ImgViewHolder(View itemView) {
        super(itemView);

        ivImg = (ImageView) itemView.findViewById(R.id.iv_img);
        pbImg = (ProgressBar) itemView.findViewById(R.id.pb_img);
    }

    public ImageView getIvImg() {
        return ivImg;
    }

    public ProgressBar getPbImg() {
        return pbImg;
    }
}