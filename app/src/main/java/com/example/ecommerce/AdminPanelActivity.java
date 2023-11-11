package com.example.ecommerce;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ecommerce.Category.AddNewCategoryActivity;
import com.example.ecommerce.Category.DeleteCategoryActivity;
import com.example.ecommerce.Category.ShowCategoryActivity;

public class AdminPanelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);
    }

    public void addProducts(View view) {
        startActivity(new Intent(AdminPanelActivity.this, AddNewProductsActivity.class));
    }

    public void showCategory(View view) {
        startActivity(new Intent(AdminPanelActivity.this, ShowCategoryActivity.class));
    }

    public void deleteCategory(View view) {
        startActivity(new Intent(AdminPanelActivity.this, DeleteCategoryActivity.class));
    }
    public void addCategory(View view) {
        startActivity(new Intent(AdminPanelActivity.this,AddNewCategoryActivity.class));
    }
}