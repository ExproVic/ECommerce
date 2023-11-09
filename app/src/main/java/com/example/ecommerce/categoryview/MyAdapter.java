package com.example.ecommerce.categoryview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.ItemCategory;
import com.example.ecommerce.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyViewCategory> {

    Context context;
    List<ItemCategory> items;

    public MyAdapter(Context context, List<ItemCategory> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public MyViewCategory onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewCategory(LayoutInflater.from(context).inflate(R.layout.item_category_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewCategory holder, int position) {
        holder.nameView.setText(items.get(position).getName());

        // Отримайте URL зображення з об'єкта ItemCategory
        String imageUrl = items.get(position).getImageUrl();

        // Завантаження та відображення зображення з URL за допомогою Picasso
        Picasso.get()
                .load(imageUrl)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
