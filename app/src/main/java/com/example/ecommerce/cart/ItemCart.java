package com.example.ecommerce.cart;

import android.view.View;

public class ItemCart {
    private String itemName;
    private int itemCount;
    private double itemPrice;
    private View.OnClickListener onRemoveClickListener;
    private String imageUrl;

    // Конструктор
    public ItemCart(String itemName, int itemCount, double itemPrice, String imageUrl) {
        this.itemName = itemName;
        this.itemCount = itemCount;
        this.itemPrice = itemPrice;
        this.imageUrl = imageUrl;
        this.onRemoveClickListener = null;
    }

    // Геттери та сеттери
    public String getItemName() {
        return itemName;
    }

    public View.OnClickListener getOnRemoveClickListener() {
        return onRemoveClickListener;
    }

    public void setOnRemoveClickListener(View.OnClickListener onRemoveClickListener) {
        this.onRemoveClickListener = onRemoveClickListener;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public double getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(double itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // Геттер для обчислення загальної суми за товар
    public double getTotalPrice() {
        return itemCount * itemPrice;
    }
}
