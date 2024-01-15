package com.example.ecommerce.Category;

public class Category {
    private String name;
    private String imageUrl;
    private String buttonSize;

    public Category() {
        // Пустий конструктор для Firebase
    }

    public Category(String name, String imageUrl, String buttonSize) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.buttonSize = buttonSize;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getButtonSize() {
        return buttonSize;
    }
}
