package com.example.ecommerce.productview;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.R;
import com.example.ecommerce.cart.ShowCartActivity;
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

    private int changef_s=0;
    private List<ItemProduct> filteredAndSortedProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_product);

        recyclerView = findViewById(R.id.recycleviewProduct);
        items = new ArrayList<>();
        adapter = new ProductAdapter(getApplicationContext(), items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        Button buttonReset = findViewById(R.id.buttonReset);
        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    double fromPrice, toPrice;
                        fromPrice = 0;
                        toPrice = 19999;
                        changef_s=1;
                    displayFilteredProducts(fromPrice, toPrice);
                }
        });


        Button buttonFilter = findViewById(R.id.buttonFilter);
        buttonFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText fieldFrom = findViewById(R.id.fieldfrom);
                EditText fieldTo = findViewById(R.id.fieldto);
                changef_s=1;
                String fromText = fieldFrom.getText().toString();
                String toText = fieldTo.getText().toString();
                if (!TextUtils.isEmpty(fromText) || !TextUtils.isEmpty(toText)) {
                    double fromPrice, toPrice;

                    if (!TextUtils.isEmpty(fromText)) {
                        fromPrice = Double.parseDouble(fromText);
                    } else {
                        fromPrice = 0;
                    }

                    if (!TextUtils.isEmpty(toText)) {
                        toPrice = Double.parseDouble(toText);
                    } else {
                        toPrice = 9999;
                    }

                    displayFilteredProducts(fromPrice, toPrice);
                } else {
                    Toast.makeText(ShowProductActivity.this, "Enter valid price range", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
    private void displayFilteredProducts(double fromPrice, double toPrice) {
        filteredAndSortedProducts = new ArrayList<>();

        for (ItemProduct product : items) {
            double productPrice = Double.parseDouble(product.getPrice());
            if (productPrice >= fromPrice && productPrice <= toPrice) {
                filteredAndSortedProducts.add(product);
            }
        }
        adapter.updateList(filteredAndSortedProducts);
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
        if (changef_s == 0) {
            Collections.sort(items, new ProductComparators.HighPriceComparator());
        } else if (changef_s == 1) {
            Collections.sort(filteredAndSortedProducts, new ProductComparators.HighPriceComparator());
        }
        adapter.notifyDataSetChanged();
    }

    private void sortByLowPrice(String categoryName) {
        if (changef_s == 0) {
            Collections.sort(items, new ProductComparators.LowPriceComparator());
        } else if (changef_s == 1) {
            Collections.sort(filteredAndSortedProducts, new ProductComparators.LowPriceComparator());
        }
        adapter.notifyDataSetChanged();
    }

    private void sortBySignAZ(String categoryName) {
        if (changef_s == 0) {
            Collections.sort(items, new ProductComparators.SignAZComparator());
        } else if (changef_s == 1) {
            Collections.sort(filteredAndSortedProducts, new ProductComparators.SignAZComparator());
        }
        adapter.notifyDataSetChanged();
    }

    private void sortBySignZA(String categoryName) {
        if (changef_s == 0) {
            Collections.sort(items, new ProductComparators.SignZAZComparator());
        } else if (changef_s == 1) {
            Collections.sort(filteredAndSortedProducts, new ProductComparators.SignZAZComparator());
        }
        adapter.notifyDataSetChanged();
    }

}
