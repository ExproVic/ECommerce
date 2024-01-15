package com.example.ecommerce;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ecommerce.Category.Category;
import com.example.ecommerce.productview.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EditElemetsActivity extends AppCompatActivity {

    private Spinner spinnerCategory;
    private Spinner spinnerProducts;
    private EditText changeCategoryEditText;
    private EditText changeProductNameEditText;
    private EditText changeProductDescriptionEditText;
    private EditText changeProductPriceEditText;
    private Button editCategoryButton;
    private Button editProductButton;
    private DatabaseReference selectedCategoryRef;
    private ArrayAdapter<String> categoryAdapter;
    private ArrayAdapter<String> productAdapter;
    private DatabaseReference categoriesDatabaseRef;

    private List<String> categories;
    private List<String> products;


    private DatabaseReference categoriesRef;
    private DatabaseReference productsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_elemets);

        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerProducts = findViewById(R.id.spinnerProducts);
        changeCategoryEditText = findViewById(R.id.changeCategory);
        changeProductNameEditText = findViewById(R.id.changeProductName);
        changeProductDescriptionEditText = findViewById(R.id.changeProductDescription);
        changeProductPriceEditText = findViewById(R.id.changeProductPrice);
        editCategoryButton = findViewById(R.id.editbuttonCategory);
        editProductButton = findViewById(R.id.buttonEditProducts);
        categoriesRef = FirebaseDatabase.getInstance().getReference().child("categories");
        editCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editCategory();
            }
        });
        initCategorySpinner();
        categories = new ArrayList<>();
        products = new ArrayList<>();

        categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        productAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, products);
        productAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProducts.setAdapter(productAdapter);

        editCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editCategory();
            }
        });

        editProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditProduct(view);
            }
        });
    }
    private void editCategory() {
        String selectedCategory = spinnerCategory.getSelectedItem().toString();
        String newCategoryName = changeCategoryEditText.getText().toString().trim();

        if (!TextUtils.isEmpty(selectedCategory) && !TextUtils.isEmpty(newCategoryName)) {
            DatabaseReference categoriesRef = FirebaseDatabase.getInstance().getReference().child("categories");

            if (categories.contains(selectedCategory)) {
                categoriesRef.child(selectedCategory).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Category oldCategory = dataSnapshot.getValue(Category.class);

                            if (oldCategory != null) {
                                categoriesRef.child(selectedCategory).removeValue();
                                Category updatedCategory = new Category(newCategoryName, oldCategory.getImageUrl(), oldCategory.getButtonSize());

                                categoriesRef.child(newCategoryName).setValue(updatedCategory);

                                initCategorySpinner();

                                changeCategoryEditText.setText("");

                                Toast.makeText(EditElemetsActivity.this, "Category updated successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(EditElemetsActivity.this, "Failed to update category", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(EditElemetsActivity.this, "Failed to update category", Toast.LENGTH_SHORT).show();
                        }
                    }



                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(EditElemetsActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(EditElemetsActivity.this, "Selected category does not exist", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(EditElemetsActivity.this, "Please select a category and enter a new name", Toast.LENGTH_SHORT).show();
        }
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
    public void EditProduct(View view) {
        String selectedProduct = spinnerProducts.getSelectedItem().toString();
        String selectedCategory = spinnerCategory.getSelectedItem().toString();

        if (TextUtils.isEmpty(selectedProduct) || TextUtils.isEmpty(selectedCategory)) {
            Toast.makeText(EditElemetsActivity.this, "Select the category and product to edit", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference selectedProductRef = categoriesRef.child(selectedCategory).child("Products").child(selectedProduct);

        String newProductName = changeProductNameEditText.getText().toString().trim();
        String newProductDescription = changeProductDescriptionEditText.getText().toString().trim();
        String newProductPrice = changeProductPriceEditText.getText().toString().trim();

        if (TextUtils.isEmpty(newProductName) && TextUtils.isEmpty(newProductDescription) && TextUtils.isEmpty(newProductPrice)) {
            Toast.makeText(EditElemetsActivity.this, "Enter at least one field to update", Toast.LENGTH_SHORT).show();
            return;
        }

        selectedProductRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Product oldProduct = dataSnapshot.getValue(Product.class);

                    if (oldProduct != null) {
                        Product updatedProduct = new Product(
                                (TextUtils.isEmpty(newProductName)) ? oldProduct.getPname() : newProductName,
                                (TextUtils.isEmpty(newProductDescription)) ? oldProduct.getDescription() : newProductDescription,
                                oldProduct.getImage(),
                                (TextUtils.isEmpty(newProductPrice)) ? oldProduct.getPrice() : newProductPrice,
                                oldProduct.getProductID(),
                                oldProduct.getButtonSize()
                        );

                        selectedProductRef.removeValue();

                        categoriesRef.child(selectedCategory).child("Products").child(updatedProduct.getPname()).setValue(updatedProduct);

                        updateProductList(selectedCategory);

                        changeProductNameEditText.setText("");
                        changeProductDescriptionEditText.setText("");
                        changeProductPriceEditText.setText("");

                        Toast.makeText(EditElemetsActivity.this, "Product updated successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(EditElemetsActivity.this, "Failed to update product", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(EditElemetsActivity.this, "Failed to update product", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(EditElemetsActivity.this, "Database error", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
