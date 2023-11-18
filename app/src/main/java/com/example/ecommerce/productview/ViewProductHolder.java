package com.example.ecommerce.productview;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.R;

public class ViewProductHolder extends RecyclerView.ViewHolder {
    ImageView imageProductView;
    TextView nameProductView, descriptionProductView, priceProductView;

    public ViewProductHolder(@NonNull View itemView) {
        super(itemView);
        imageProductView = itemView.findViewById(R.id.imgProduct);
        nameProductView = itemView.findViewById(R.id.nameProduct);
        descriptionProductView = itemView.findViewById(R.id.descritionProduct);
        priceProductView = itemView.findViewById(R.id.priceProduct);
    }
}
