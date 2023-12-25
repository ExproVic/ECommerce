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


        String categoryName = getIntent().getStringExtra("category");

        if (categoryName != null) {
            loadAndDisplayProducts(categoryName);

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
                    items.clear();

                    for (DataSnapshot productSnapshot : productsSnapshot.getChildren()) {
                        ItemProduct itemProduct = productSnapshot.getValue(ItemProduct.class);
                        items.add(itemProduct);
                    }

                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "Error reading data from Firebase: " + databaseError.getMessage());
                    databaseError.toException().printStackTrace();
                }
            });
        } else {
            Log.e(TAG, "Category name not provided in the intent");
        }
    }
    private void onAddToCartButtonClick() {
        if (!items.isEmpty()) {
            ItemProduct selectedProduct = items.get(0);
            addToCart(selectedProduct);
        }
    }
    private void addToCart(ItemProduct product) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("cart").child(uid);
            cartRef.child(product.getPname()).setValue(product);
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
                items.clear();

                for (DataSnapshot productSnapshot : productsSnapshot.getChildren()) {
                    ItemProduct itemProduct = productSnapshot.getValue(ItemProduct.class);
                    items.add(itemProduct);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void onCartButtonClick(View view) {
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
