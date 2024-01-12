package com.example.ecommerce.Category;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.R;
import com.example.ecommerce.cart.ShowCartActivity;
import com.example.ecommerce.categoryview.ItemCategory;
import com.example.ecommerce.categoryview.MyAdapter;
import com.example.ecommerce.emailfeedback.emailUserActivity;
import com.example.ecommerce.productview.ShowProductActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
//Designed by Viktor Parakhonych all icons were taken from Flaticon

public class ShowCategoryActivity extends AppCompatActivity implements CategoryClickListener {

    private RecyclerView recyclerView;
    private List<ItemCategory> allItems;
    private List<ItemCategory> items;
    private DatabaseReference databaseReference;
    private EditText areaSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_category);

        recyclerView = findViewById(R.id.recycleview);
        areaSearch = findViewById(R.id.areaSearch);

        items = new ArrayList<>();
        allItems = new ArrayList<>();

        MyAdapter myAdapter = new MyAdapter(getApplicationContext(), items, this);
        recyclerView.setAdapter(myAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("categories");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                items.clear();
                allItems.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String categoryName = snapshot.child("name").getValue(String.class);
                    String categoryImageUrl = snapshot.child("imageUrl").getValue(String.class);

                    ItemCategory item = new ItemCategory(categoryName, categoryImageUrl);
                    items.add(item);
                    allItems.add(item);
                }

                recyclerView.getAdapter().notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("ShowCategoryActivity", "Database Error: " + databaseError.getMessage());
                Toast.makeText(ShowCategoryActivity.this, "Error fetching data from Firebase", Toast.LENGTH_SHORT).show();
            }
        });

        MyAdapter adapter = new MyAdapter(getApplicationContext(), items, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        setupSearchButton();
        setupResetButton();
    }
    private void setupSearchButton() {
        Button searchButton = findViewById(R.id.buttonSearch);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performSearch();
            }
        });
    }

    private void setupResetButton() {
        Button resetButton = findViewById(R.id.buttonSearchReset);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetSearch();
            }
        });
    }

    private void performSearch() {
        String searchTerm = areaSearch.getText().toString().toLowerCase();

        if (searchTerm.isEmpty()) {
            Toast.makeText(ShowCategoryActivity.this, "Please enter a search term", Toast.LENGTH_SHORT).show();
            return;
        }

        items.clear();

        for (ItemCategory item : allItems) {
            if (item.getName().toLowerCase().contains(searchTerm)) {
                items.add(item);
            }
        }

        recyclerView.getAdapter().notifyDataSetChanged();
    }

    private void resetSearch() {
        areaSearch.getText().clear();
        items.clear();
        items.addAll(allItems);
        recyclerView.getAdapter().notifyDataSetChanged();
    }
    @Override
    public void onCategoryClick(ItemCategory itemCategory) {
        if (itemCategory != null) {
            openCategory(itemCategory);
        }
    }
    public void onCartButtonClick(View view) {
        Intent intent = new Intent(this, ShowCartActivity.class);
        startActivity(intent);
    }
    public void onEmailButtonClick(View view) {
        Intent intent = new Intent(this, emailUserActivity.class);
        startActivity(intent);
    }
    private void openCategory(ItemCategory itemCategory) {
        Log.d("ShowCategoryActivity", "Opening category: " + itemCategory.getName());
        Toast.makeText(this, "Opening category: " + itemCategory.getName(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(ShowCategoryActivity.this, ShowProductActivity.class);
        intent.putExtra("category", itemCategory.getName());
        startActivity(intent);
    }
}