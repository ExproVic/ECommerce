package com.example.ecommerce.productview;

public class ItemProduct {
    private String pname;
    private String descriptionProduct;
    private String Price; // Змінено назву поля з priceProduct на Price
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
}