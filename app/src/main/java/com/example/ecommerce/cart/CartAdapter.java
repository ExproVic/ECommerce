package com.example.ecommerce.cart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ecommerce.R;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private List<ItemCart> cartItems;
    private OnRemoveClickListener onRemoveClickListener;

    public CartAdapter(Context context, List<ItemCart> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
    }

    public interface OnRemoveClickListener {
        void onRemoveClick(int position);
    }

    public void setOnRemoveClickListener(OnRemoveClickListener listener) {
        this.onRemoveClickListener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart_view, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        ItemCart currentItem = cartItems.get(position);
        ItemCart cartItem = cartItems.get(position);

        holder.nameProduct.setText(cartItem.getItemName());
        holder.countProduct.setText(String.valueOf(cartItem.getItemCount()));
        holder.totalPrice.setText(String.valueOf(cartItem.getTotalPrice()));

        Glide.with(context).load(cartItem.getImageUrl()).into(holder.imgProduct);

        holder.deleteProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();

                if (onRemoveClickListener != null) {
                    onRemoveClickListener.onRemoveClick(adapterPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {

        TextView nameProduct, countProduct, totalPrice;
        ImageView imgProduct, deleteProductButton;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            nameProduct = itemView.findViewById(R.id.nameProduct);
            countProduct = itemView.findViewById(R.id.countProduct);
            totalPrice = itemView.findViewById(R.id.totalprice);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            deleteProductButton = itemView.findViewById(R.id.deleteproductfromcart);
        }
    }
}
