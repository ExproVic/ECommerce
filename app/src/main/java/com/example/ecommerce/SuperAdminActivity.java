package com.example.ecommerce;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ecommerce.Category.AddNewCategoryActivity;
import com.example.ecommerce.Category.DeleteElementsActivity;
import com.example.ecommerce.Category.ShowCategoryActivity;

public class SuperAdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_admin);
    }
    public void employeeManagement(View view) {
        startActivity(new Intent(SuperAdminActivity.this, EmployeeManagementActivity.class));
    }
    public void addProducts(View view) {
        startActivity(new Intent(SuperAdminActivity.this, AddNewProductsActivity.class));
    }

    public void showCategory(View view) {
        startActivity(new Intent(SuperAdminActivity.this, ShowCategoryActivity.class));
    }

    public void deleteCategory(View view) {
        startActivity(new Intent(SuperAdminActivity.this, DeleteElementsActivity.class));
    }
    public void addCategory(View view) {
        startActivity(new Intent(SuperAdminActivity.this, AddNewCategoryActivity.class));
    }
}