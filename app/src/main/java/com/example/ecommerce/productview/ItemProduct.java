package com.example.ecommerce.productview;

import com.google.firebase.database.PropertyName;

public class ItemProduct implements Comparable<ItemProduct> {
    @PropertyName("pname")
    private String pname;

    @PropertyName("descriptionProduct")
    private String descriptionProduct;

    @PropertyName("Price")
    private String Price;
    @PropertyName("count")
    private int count;

    @PropertyName("imageProduct")
    private String imageProduct;

    public ItemProduct() {
    }

    public ItemProduct(String nameProduct, String descriptionProduct, String Price, String imageProduct) {
        this.pname = nameProduct;
        this.descriptionProduct = descriptionProduct;
        this.Price = Price;
        this.imageProduct = imageProduct;
    }
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String Price) {
        this.Price = Price;
    }

    public String getImage() {
        return imageProduct;
    }

    public void setImage(String imageProduct) {
        this.imageProduct = imageProduct;
    }

    public String getDescription() {
        return descriptionProduct;
    }

    public void setDescription(String descriptionProduct) {
        this.descriptionProduct = descriptionProduct;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String nameProduct) {
        this.pname = nameProduct;
    }

    @Override
    public int compareTo(ItemProduct other) {
        return Double.compare(Double.parseDouble(this.getPrice()), Double.parseDouble(other.getPrice()));
    }
}
