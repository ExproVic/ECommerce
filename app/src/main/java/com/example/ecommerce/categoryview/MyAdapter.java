package com.example.ecommerce.categoryview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.Category.CategoryClickListener;
import com.example.ecommerce.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyViewCategory> {
    private Context context;
    private List<ItemCategory> items;
    private CategoryClickListener categoryClickListener;

    public MyAdapter(Context context, List<ItemCategory> items, CategoryClickListener categoryClickListener) {

        this.context = context;
        this.items = items;
        this.categoryClickListener = categoryClickListener;
    }

    @NonNull
    @Override
    public MyViewCategory onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category_view, parent, false);
        return new MyViewCategory(view, categoryClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewCategory holder, int position) {
        holder.nameView.setText(items.get(position).getName());

        String imageUrl = items.get(position).getImageUrl();
        holder.bind(items.get(position));
        Picasso.get()
                .load(imageUrl)
                .into(holder.imageView);
    }
    @Override
    public int getItemCount() {
        return items.size();
    }
}