package com.example.ecommerce.cart;

import com.google.firebase.database.PropertyName;

public class ItemCart {
    @PropertyName("pname")
    private String itemName;

    @PropertyName("count")
    private int itemCount;

    @PropertyName("price")
    private double itemPrice;

    @PropertyName("image")
    private String imageUrl;

    @PropertyName("totalAmount")
    private double itemTotalAmount;

    // Конструктор
    public ItemCart(String itemName, int itemCount, double itemPrice, String imageUrl) {
        this.itemName = itemName;
        this.itemCount = itemCount;
        this.itemPrice = itemPrice;
        this.imageUrl = imageUrl;
    }


    // Геттери та сеттери
    @PropertyName("pname")
    public String getItemName() {
        return itemName;
    }

    @PropertyName("pname")
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    @PropertyName("count")
    public int getItemCount() {
        return itemCount;
    }

    @PropertyName("count")
    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    @PropertyName("price")
    public double getItemPrice() {
        return itemPrice;
    }

    @PropertyName("price")
    public void setItemPrice(double itemPrice) {
        this.itemPrice = itemPrice;
    }

    @PropertyName("image")
    public String getImageUrl() {
        return imageUrl;
    }

    @PropertyName("image")
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @PropertyName("totalAmount")
    public double getItemTotalAmount() {
        return itemTotalAmount;
    }

    @PropertyName("totalAmount")
    public void setItemTotalAmount(double itemTotalAmount) {
        this.itemTotalAmount = itemTotalAmount;
    }

    // Геттер для обчислення загальної суми за товар
    public double getTotalPrice() {
        return itemCount * itemPrice;
    }
}
