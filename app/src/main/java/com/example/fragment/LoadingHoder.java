package com.example.fragment;

import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class LoadingHoder extends RecyclerView.ViewHolder{

    public ProgressBar progressBar;

    public LoadingHoder(@NonNull View itemView) {
        super(itemView);
        progressBar=(ProgressBar)itemView.findViewById(R.id.progressBar);
    }
}

