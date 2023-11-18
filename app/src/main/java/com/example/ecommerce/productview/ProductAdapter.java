package com.example.ecommerce.productview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.R;
import com.bumptech.glide.Glide;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ViewProductHolder> {
    private Context context;
    private List<ItemProduct> items;

    public ProductAdapter(Context context, List<ItemProduct> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public ViewProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewProductHolder(LayoutInflater.from(context).inflate(R.layout.item_view_product, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewProductHolder holder, int position) {
        ItemProduct item = items.get(position);
        holder.nameProductView.setText(item.getPname());
        holder.descriptionProductView.setText(item.getDescription());
        holder.priceProductView.setText(item.getPrice());

        // Використовуйте Glide для завантаження зображення з URL
        Glide.with(context).load(item.getImage()).into(holder.imageProductView);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}