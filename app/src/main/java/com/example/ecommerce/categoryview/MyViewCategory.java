package com.example.ecommerce.categoryview;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.Category.CategoryClickListener;
import com.example.ecommerce.R;

public class MyViewCategory extends RecyclerView.ViewHolder implements View.OnClickListener {
    ImageView imageView;
    TextView nameView;
    private CategoryClickListener categoryClickListener;

    public MyViewCategory(@NonNull View itemView, CategoryClickListener categoryClickListener) {
        super(itemView);
        imageView = itemView.findViewById(R.id.imageview);
        nameView = itemView.findViewById(R.id.name_img);
        this.categoryClickListener = categoryClickListener;

        itemView.setOnClickListener(this);
    }

    public void bind(final ItemCategory itemCategory) {
        // Заповніть дані для елементів UI з об'єкта ItemCategory
        nameView.setText(itemCategory.getName());

        // Змініть обробник подій так, щоб передавався об'єкт itemCategory
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (categoryClickListener != null) {
                    categoryClickListener.onCategoryClick(itemCategory);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        // Можете залишити цей метод порожнім, оскільки обробка кліку вже визначена в bind()
    }
}
