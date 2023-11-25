package com.example.ecommerce.productview;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.R;

public class ViewProductHolder extends RecyclerView.ViewHolder {
    ImageView imageProductView;
    TextView nameProductView, descriptionProductView, priceProductView, countProductTextView;
    Button minusButton, plusButton;

    public ViewProductHolder(@NonNull View itemView) {
        super(itemView);
        imageProductView = itemView.findViewById(R.id.imgProduct);
        nameProductView = itemView.findViewById(R.id.nameProduct);
        descriptionProductView = itemView.findViewById(R.id.descritionProduct);
        priceProductView = itemView.findViewById(R.id.priceProduct);
        countProductTextView = itemView.findViewById(R.id.countProduct);
        minusButton = itemView.findViewById(R.id.buttonminus);
        plusButton = itemView.findViewById(R.id.buttonplus);

        // Обробник подій для кнопки "Мінус"
        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Отримати поточне значення та конвертувати його в число
                int currentValue = Integer.parseInt(countProductTextView.getText().toString());

                // Перевірити, чи число не менше 1, перед тим як віднімати
                if (currentValue > 0) {
                    currentValue--;

                    // Оновити текстове поле з новим значенням
                    countProductTextView.setText(String.valueOf(currentValue));
                }
            }
        });

        // Обробник подій для кнопки "Плюс"
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Отримати поточне значення та конвертувати його в число
                int currentValue = Integer.parseInt(countProductTextView.getText().toString());

                // Збільшити значення на 1
                currentValue++;

                // Оновити текстове поле з новим значенням
                countProductTextView.setText(String.valueOf(currentValue));
            }
        });
    }
}