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

    // Порожній конструктор без аргументів (необхідний для Firebase)
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

    // Змінено назву методу з getPriceProduct на getPrice
    public String getPrice() {
        return Price;
    }

    // Змінено назву методу з setPriceProduct на setPrice
    public void setPrice(String Price) {
        this.Price = Price;
    }

    // Змінено назву методу з getImageProduct на getImage
    public String getImage() {
        return imageProduct;
    }

    // Змінено назву методу з setImageProduct на setImage
    public void setImage(String imageProduct) {
        this.imageProduct = imageProduct;
    }

    // Змінено назву методу з getDescriptionProduct на getDescription
    public String getDescription() {
        return descriptionProduct;
    }

    // Змінено назву методу з setDescriptionProduct на setDescription
    public void setDescription(String descriptionProduct) {
        this.descriptionProduct = descriptionProduct;
    }

    // Змінено назву методу з getNameProduct на getName
    public String getPname() {
        return pname;
    }

    // Змінено назву методу з setNameProduct на setName
    public void setPname(String nameProduct) {
        this.pname = nameProduct;
    }

    @Override
    public int compareTo(ItemProduct other) {
        // Реалізуйте логіку порівняння для використання в методі sort
        // Наприклад, для сортування за іменем може бути:
        // return this.pname.compareToIgnoreCase(other.getPname());
        // Для сортування за ціною, як ви робите в ItemProductComparators:
        return Double.compare(Double.parseDouble(this.getPrice()), Double.parseDouble(other.getPrice()));
    }
}
