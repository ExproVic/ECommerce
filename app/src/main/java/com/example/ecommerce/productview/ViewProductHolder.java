package com.example.ecommerce.productview;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.R;

import java.util.List;

public class ViewProductHolder extends RecyclerView.ViewHolder {
    ImageView imageProductView;
    TextView nameProductView, descriptionProductView, priceProductView, countProductTextView;
    Button minusButton, plusButton, addToCartButton;

    private OnAddToCartClickListener addToCartClickListener;

    public ViewProductHolder(@NonNull View itemView, OnAddToCartClickListener addToCartClickListener) {
        super(itemView);
        imageProductView = itemView.findViewById(R.id.imgProduct);
        nameProductView = itemView.findViewById(R.id.nameProduct);
        descriptionProductView = itemView.findViewById(R.id.descritionProduct);
        priceProductView = itemView.findViewById(R.id.priceProduct);
        countProductTextView = itemView.findViewById(R.id.countProduct);
        minusButton = itemView.findViewById(R.id.buttonminus);
        plusButton = itemView.findViewById(R.id.buttonplus);
        addToCartButton = itemView.findViewById(R.id.addtoCart);

        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentValue = Integer.parseInt(countProductTextView.getText().toString());
                if (currentValue > 0) {
                    currentValue--;
                    countProductTextView.setText(String.valueOf(currentValue));
                }
            }
        });

        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentValue = Integer.parseInt(countProductTextView.getText().toString());
                currentValue++;
                countProductTextView.setText(String.valueOf(currentValue));
            }
        });

        this.addToCartClickListener = addToCartClickListener;

        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentCount = Integer.parseInt(countProductTextView.getText().toString());
                addToCartClickListener.onAddToCartClick(items.get(getAdapterPosition()), currentCount);
            }
        });
    }
    public void setOnAddToCartClickListener(OnAddToCartClickListener listener) {
        this.addToCartClickListener = listener;
    }
    private List<ItemProduct> items;

    public void setItems(List<ItemProduct> items) {
        this.items = items;
    }

    public interface OnAddToCartClickListener {
        void onAddToCartClick(int count);

        void onAddToCartClick(ItemProduct item, int count);
    }
}
