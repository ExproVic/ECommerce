package com.example.ecommerce.productview;


public class Product {
    private String pname;
    private String description;
    private String image;
    private String price;
    private String productID;
    private String buttonSize;

    public Product() {
        // Конструктор за замовчуванням для Firebase
    }

    public Product(String pname, String description, String image, String price, String productID, String buttonSize) {
        this.pname = pname;
        this.description = description;
        this.image = image;
        this.price = price;
        this.productID = productID;
        this.buttonSize = buttonSize;
    }

    public String getPname() {
        return pname;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public String getPrice() {
        return price;
    }

    public String getProductID() {
        return productID;
    }

    public String getButtonSize() {
        return buttonSize;
    }
}
