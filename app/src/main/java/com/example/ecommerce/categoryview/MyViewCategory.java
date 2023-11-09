package com.example.ecommerce.categoryview;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.R;

public class MyViewCategory extends RecyclerView.ViewHolder {
    ImageView imageView;
    TextView nameView;
    public MyViewCategory(@NonNull View itemView) {
        super(itemView);
        imageView=itemView.findViewById(R.id.imageview);
        nameView=itemView.findViewById(R.id.name_img);
    }
}
