package com.example.ecommerce.Category;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.R;
import com.example.ecommerce.cart.ShowCartActivity;
import com.example.ecommerce.categoryview.ItemCategory;
import com.example.ecommerce.categoryview.MyAdapter;
import com.example.ecommerce.productview.ShowProductActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ShowCategoryActivity extends AppCompatActivity implements CategoryClickListener {

    private RecyclerView recyclerView;
    private List<ItemCategory> items;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_category);


        recyclerView = findViewById(R.id.recycleview);
        items = new ArrayList<>();
        MyAdapter myAdapter = new MyAdapter(getApplicationContext(), items, this);
        recyclerView.setAdapter(myAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("categories");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                items.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String categoryName = snapshot.child("name").getValue(String.class);
                    String categoryImageUrl = snapshot.child("imageUrl").getValue(String.class);

                    items.add(new ItemCategory(categoryName, categoryImageUrl));
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
    private void openCategory(ItemCategory itemCategory) {
        Log.d("ShowCategoryActivity", "Opening category: " + itemCategory.getName());
        Toast.makeText(this, "Opening category: " + itemCategory.getName(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(ShowCategoryActivity.this, ShowProductActivity.class);
        intent.putExtra("category", itemCategory.getName());
        startActivity(intent);
    }
}