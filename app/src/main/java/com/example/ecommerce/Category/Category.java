package com.example.ecommerce.Category;

public class Category {
    private String name;
    private int imageId;

    public Category(String categoryName, String imageUrl) {
    }

    public Category(String name, int imageId) {
        this.name = name;
        this.imageId = imageId;
    }

    public String getName() {
        return name;
    }

    public int getImageId() {
        return imageId;
    }
}

