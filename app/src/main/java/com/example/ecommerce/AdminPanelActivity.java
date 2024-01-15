package com.example.ecommerce;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ecommerce.Category.AddNewCategoryActivity;
import com.example.ecommerce.Category.DeleteElementsActivity;
import com.example.ecommerce.Category.ShowCategoryActivity;

public class AdminPanelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);
    }
    public void acceptDelivery(View view) {
        startActivity(new Intent(AdminPanelActivity.this, AcceptDeliveryActivity.class));
    }

    public void addProducts(View view) {
        startActivity(new Intent(AdminPanelActivity.this, AddNewProductsActivity.class));
    }
    public void editElements(View view){
        startActivity(new Intent(AdminPanelActivity.this, EditElemetsActivity.class));
    }
    public void Statistics(View view) {
        startActivity(new Intent(AdminPanelActivity.this, StatisticsActivity.class));
    }

    public void showCategory(View view) {
        startActivity(new Intent(AdminPanelActivity.this, ShowCategoryActivity.class));
    }

    public void deleteCategory(View view) {
        startActivity(new Intent(AdminPanelActivity.this, DeleteElementsActivity.class));
    }
    public void addCategory(View view) {
        startActivity(new Intent(AdminPanelActivity.this,AddNewCategoryActivity.class));
    }
}