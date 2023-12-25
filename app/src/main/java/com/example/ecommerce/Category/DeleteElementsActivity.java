package com.example.ecommerce.Category;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ecommerce.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DeleteElementsActivity extends AppCompatActivity {

    private Spinner spinnerCategory;
    private Spinner spinnerProducts;
    private Button buttonCategory;
    private Button buttonProducts;
    private DatabaseReference selectedCategoryRef;
    private ArrayAdapter<String> categoryAdapter;
    private ArrayAdapter<String> productAdapter;
    private DatabaseReference categoriesDatabaseRef;

    private List<String> categories;
    private List<String> products;

    private DatabaseReference categoriesRef;
    private DatabaseReference productsRef;

    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_elements);

        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerProducts = findViewById(R.id.spinnerProducts);
        buttonCategory = findViewById(R.id.buttonCategory);
        buttonProducts = findViewById(R.id.buttonProducts);
        categoriesRef = FirebaseDatabase.getInstance().getReference().child("categories");

        initCategorySpinner();
        categories = new ArrayList<>();
        products = new ArrayList<>();

        categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        productAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, products);
        productAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProducts.setAdapter(productAdapter);

        buttonCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCategory();
            }
        });

        buttonProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteProduct();
            }
        });
    }

    private void initCategorySpinner() {
        categoriesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                categories.clear();

                for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                    String categoryName = categorySnapshot.getKey();
                    categories.add(categoryName);
                }

                categoryAdapter.notifyDataSetChanged();
                if (categories.size() > 0) {
                    spinnerCategory.setSelection(0);
                }
                spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        String selectedCategory = spinnerCategory.getSelectedItem().toString();
                        updateProductList(selectedCategory);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    private void updateProductList(String selectedCategory) {
        selectedCategoryRef = categoriesRef.child(selectedCategory).child("Products");

        selectedCategoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                products.clear();
                for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                    String productName = productSnapshot.getKey();
                    products.add(productName);
                }
                productAdapter.notifyDataSetChanged();
                if (products.size() > 0) {
                    spinnerProducts.setSelection(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void deleteCategory() {
        Object selectedCategoryObject = spinnerCategory.getSelectedItem();

        if (selectedCategoryObject == null || TextUtils.isEmpty(selectedCategoryObject.toString())) {
            Toast.makeText(DeleteElementsActivity.this, "Select a category to delete", Toast.LENGTH_SHORT).show();
            return;
        }

        final String selectedCategory = selectedCategoryObject.toString();

        // Підтвердження видалення
        new AlertDialog.Builder(DeleteElementsActivity.this)
                .setTitle("Delete a category")
                .setMessage("Are you sure you want to delete the category? \"" + selectedCategory + "\"?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        performCategoryDeletion(selectedCategory);
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
    private void performCategoryDeletion(final String selectedCategory) {
        DatabaseReference categoriesRef = FirebaseDatabase.getInstance().getReference().child("categories");
        categoriesRef.child(selectedCategory).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    categories.remove(selectedCategory);
                    categoryAdapter.notifyDataSetChanged();
                    Toast.makeText(DeleteElementsActivity.this, "Category \"" + selectedCategory + "\" deleted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DeleteElementsActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void deleteProduct() {
        Object selectedProductObject = spinnerProducts.getSelectedItem();

        if (selectedProductObject == null) {
            Toast.makeText(DeleteElementsActivity.this, "Select the product to remove", Toast.LENGTH_SHORT).show();
            return;
        }
        String selectedProduct = selectedProductObject.toString();

        String selectedCategory = spinnerCategory.getSelectedItem().toString();

        if (TextUtils.isEmpty(selectedProduct) || TextUtils.isEmpty(selectedCategory)) {
            Toast.makeText(DeleteElementsActivity.this, "Select the category and product to remove", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference categoriesRef = FirebaseDatabase.getInstance().getReference().child("categories");

        categoriesRef.child(selectedCategory).child("Products").child(selectedProduct).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    products.remove(selectedProduct);
                    productAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(DeleteElementsActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
