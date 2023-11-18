package com.example.ecommerce.productview;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ShowProductActivity extends AppCompatActivity {

    private static final String TAG = "ShowProductActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_product);

        RecyclerView recyclerView = findViewById(R.id.recycleviewProduct);
        List<ItemProduct> items = new ArrayList<>();

        // Отримайте дані з інтенту (від категорії, з якої ви прийшли)
        String categoryName = getIntent().getStringExtra("category");

        if (categoryName != null) {
            DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference("categories")
                    .child(categoryName)
                    .child("Products");

            productsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot productsSnapshot) {
                    for (DataSnapshot productSnapshot : productsSnapshot.getChildren()) {
                        ItemProduct itemProduct = productSnapshot.getValue(ItemProduct.class);
                        items.add(itemProduct);
                    }

                    // Встановіть адаптер після отримання даних для конкретної категорії
                    recyclerView.setLayoutManager(new LinearLayoutManager(ShowProductActivity.this));
                    recyclerView.setAdapter(new ProductAdapter(getApplicationContext(), items));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Обробте помилку читання продуктів з бази даних
                }
            });
        } else {
            // Обробте випадок, коли дані про категорію не були передані
            Log.e(TAG, "Category name not provided in the intent");
            // Тут ви можете відобразити повідомлення користувачеві або взяти інші дії за замовчуванням
        }
    }
}
