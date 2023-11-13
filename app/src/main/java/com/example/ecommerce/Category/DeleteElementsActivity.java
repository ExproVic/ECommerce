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
    private DatabaseReference productsRef;  // Вибачте за пропуск

    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_elements);

        // Ініціалізація всіх змінних

        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerProducts = findViewById(R.id.spinnerProducts);
        buttonCategory = findViewById(R.id.buttonCategory);
        buttonProducts = findViewById(R.id.buttonProducts);

        // Отримайте посилання на "categories" в базі даних
        categoriesRef = FirebaseDatabase.getInstance().getReference().child("categories");

        // Ініціалізуйте Spinner для категорій
        initCategorySpinner();

        // Ініціалізація списку категорій та продуктів
        categories = new ArrayList<>();
        products = new ArrayList<>();

        // Ініціалізація адаптерів
        categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        productAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, products);
        productAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProducts.setAdapter(productAdapter);

        // Додавання слухача подій для кнопок
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
        // Додаємо слухача подій для отримання списку категорій
        categoriesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Очистіть попередні дані перед оновленням
                categories.clear();

                // Ітеруємося по всіх дочірніх вузлах (категоріях)
                for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                    String categoryName = categorySnapshot.getKey();
                    categories.add(categoryName);
                }

                // Оновіть адаптер та покажіть обрану категорію
                categoryAdapter.notifyDataSetChanged();
                if (categories.size() > 0) {
                    spinnerCategory.setSelection(0);
                }
                spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        // При кожній зміні вибору категорії оновлюйте список продуктів
                        String selectedCategory = spinnerCategory.getSelectedItem().toString();
                        updateProductList(selectedCategory);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                        // Нічого не робимо, якщо нічого не вибрано
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Обробляйте помилки, якщо потрібно
            }
        });
    }
    private void updateProductList(String selectedCategory) {
        selectedCategoryRef = categoriesRef.child(selectedCategory).child("Products");

        selectedCategoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Очистіть попередні дані перед оновленням
                products.clear();

                // Ітеруємося по всіх дочірніх вузлах (продуктах)
                for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                    String productName = productSnapshot.getKey();
                    products.add(productName);
                }

                // Оновіть адаптер та покажіть обраний продукт
                productAdapter.notifyDataSetChanged();
                if (products.size() > 0) {
                    spinnerProducts.setSelection(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Обробляйте помилки, якщо потрібно
            }
        });
    }

    public void deleteCategory() {
        // Отримайте обрану категорію
        Object selectedCategoryObject = spinnerCategory.getSelectedItem();

        // Перевірка, чи вибрана категорія
        if (selectedCategoryObject == null || TextUtils.isEmpty(selectedCategoryObject.toString())) {
            Toast.makeText(DeleteElementsActivity.this, "Виберіть категорію для видалення", Toast.LENGTH_SHORT).show();
            return;
        }

        final String selectedCategory = selectedCategoryObject.toString();

        // Підтвердження видалення
        new AlertDialog.Builder(DeleteElementsActivity.this)
                .setTitle("Видалення категорії")
                .setMessage("Ви впевнені, що хочете видалити категорію \"" + selectedCategory + "\"?")
                .setPositiveButton("Так", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Викликається при позитивному відповіді
                        performCategoryDeletion(selectedCategory);
                    }
                })
                .setNegativeButton("Ні", null)
                .show();
    }
    private void performCategoryDeletion(final String selectedCategory) {
        // Отримайте посилання на "categories" в базі даних
        DatabaseReference categoriesRef = FirebaseDatabase.getInstance().getReference().child("categories");

        // Видаліть категорію з бази даних
        categoriesRef.child(selectedCategory).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // Видаліть категорію зі списку
                    categories.remove(selectedCategory);
                    // Оновіть адаптер
                    categoryAdapter.notifyDataSetChanged();
                    Toast.makeText(DeleteElementsActivity.this, "Категорію \"" + selectedCategory + "\" видалено", Toast.LENGTH_SHORT).show();
                } else {
                    // Обробіть помилку видалення з бази даних
                    Toast.makeText(DeleteElementsActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void deleteProduct() {
        // Отримайте обраний продукт
        Object selectedProductObject = spinnerProducts.getSelectedItem();

        // Перевірка, чи вибрано продукт
        if (selectedProductObject == null) {
            Toast.makeText(DeleteElementsActivity.this, "Виберіть продукт для видалення", Toast.LENGTH_SHORT).show();
            return;
        }

        String selectedProduct = selectedProductObject.toString();

        // Отримайте обрану категорію
        String selectedCategory = spinnerCategory.getSelectedItem().toString();

        // Перевірка, чи вибрані продукт та категорія
        if (TextUtils.isEmpty(selectedProduct) || TextUtils.isEmpty(selectedCategory)) {
            Toast.makeText(DeleteElementsActivity.this, "Виберіть категорію та продукт для видалення", Toast.LENGTH_SHORT).show();
            return;
        }

        // Отримайте посилання на "categories" в базі даних
        DatabaseReference categoriesRef = FirebaseDatabase.getInstance().getReference().child("categories");

        // Видаліть продукт з бази даних
        categoriesRef.child(selectedCategory).child("Products").child(selectedProduct).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // Видаліть продукт зі списку
                    products.remove(selectedProduct);
                    // Оновіть адаптер
                    productAdapter.notifyDataSetChanged();
                } else {
                    // Обробіть помилку видалення з бази даних
                    Toast.makeText(DeleteElementsActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
