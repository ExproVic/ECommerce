package com.example.ecommerce.productview;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.R;
import com.example.ecommerce.cart.ShowCartActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShowProductActivity extends AppCompatActivity {

    private static final String TAG = "ShowProductActivity";
    private RecyclerView recyclerView;
    private List<ItemProduct> items;
    private ProductAdapter adapter;
    private int countProduct = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_product);

        recyclerView = findViewById(R.id.recycleviewProduct);
        items = new ArrayList<>();
        adapter = new ProductAdapter(getApplicationContext(), items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


        // Отримайте дані з інтенту (від категорії, з якої ви прийшли)
        String categoryName = getIntent().getStringExtra("category");

        if (categoryName != null) {
            loadAndDisplayProducts(categoryName); // Відображення продуктів за замовчуванням

            // Додайте кнопки або інші елементи управління для сортування
            Button sortByHighPriceButton = findViewById(R.id.radioHighprice);
            sortByHighPriceButton.setOnClickListener(v -> sortByHighPrice(categoryName));

            Button sortByLowPriceButton = findViewById(R.id.radioLowprice);
            sortByLowPriceButton.setOnClickListener(v -> sortByLowPrice(categoryName));

            Button sortBySignAZButton = findViewById(R.id.radioSignAZ);
            sortBySignAZButton.setOnClickListener(v -> sortBySignAZ(categoryName));

            Button sortBySignZAButton = findViewById(R.id.radioSignZA);
            sortBySignZAButton.setOnClickListener(v -> sortBySignZA(categoryName));

            DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference("categories")
                    .child(categoryName)
                    .child("Products");

            productsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot productsSnapshot) {
                    items.clear(); // Очистіть поточний список перед завантаженням нових даних

                    for (DataSnapshot productSnapshot : productsSnapshot.getChildren()) {
                        ItemProduct itemProduct = productSnapshot.getValue(ItemProduct.class);
                        items.add(itemProduct);
                    }

                    // Оновіть адаптер після отримання нових даних
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Обробте помилку читання продуктів з бази даних
                    Log.e(TAG, "Error reading data from Firebase: " + databaseError.getMessage());
                    databaseError.toException().printStackTrace();
                }
            });
        } else {
            // Обробте випадок, коли дані про категорію не були передані
            Log.e(TAG, "Category name not provided in the intent");
            // Тут ви можете відобразити повідомлення користувачеві або взяти інші дії за замовчуванням
        }
    }
    private void onAddToCartButtonClick() {
        // Отримайте вибраний продукт (наприклад, перший продукт у списку)
        if (!items.isEmpty()) {
            ItemProduct selectedProduct = items.get(0); // Отримайте перший продукт (можливо, вам слід реалізувати вибір продукта за допомогою діалогового вікна або іншого методу)

            // Код для додавання вибраного продукту до корзини
            addToCart(selectedProduct);
        }
    }
    private void addToCart(ItemProduct product) {
        // Отримайте UID поточного користувача
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();

            // Отримайте посилання на "cart" в базі даних та додайте вибраний продукт
            DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("cart").child(uid);
            cartRef.child(product.getPname()).setValue(product);

            // Після успішного додавання продукту обнуліть лічильник
            countProduct = 0;
            Log.d(TAG, "Product added to cart successfully.");
        }
    }


    private void loadAndDisplayProducts(String categoryName) {
        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference("categories")
                .child(categoryName)
                .child("Products");

        productsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot productsSnapshot) {
                items.clear(); // Очистіть поточний список перед завантаженням нових даних

                for (DataSnapshot productSnapshot : productsSnapshot.getChildren()) {
                    ItemProduct itemProduct = productSnapshot.getValue(ItemProduct.class);
                    items.add(itemProduct);
                }

                // Оновіть адаптер після отримання нових даних
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Обробте помилку читання продуктів з бази даних
            }
        });
    }

    public void onCartButtonClick(View view) {
        // Код для переходу до ShowCartActivity
        Intent intent = new Intent(this, ShowCartActivity.class);
        startActivity(intent);
    }

    private void sortByHighPrice(String categoryName) {
        Collections.sort(items, new ProductComparators.HighPriceComparator());
        adapter.notifyDataSetChanged();
    }

    private void sortByLowPrice(String categoryName) {
        Collections.sort(items, new ProductComparators.LowPriceComparator());
        adapter.notifyDataSetChanged();
    }

    private void sortBySignAZ(String categoryName) {
        Collections.sort(items, new ProductComparators.SignAZComparator());
        adapter.notifyDataSetChanged();
    }

    private void sortBySignZA(String categoryName) {
        Collections.sort(items, new ProductComparators.SignZAZComparator());
        adapter.notifyDataSetChanged();
    }
}
