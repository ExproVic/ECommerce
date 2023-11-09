package com.example.ecommerce.Category;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.ItemCategory;
import com.example.ecommerce.R;
import com.example.ecommerce.categoryview.MyAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.lang.String;
import java.util.ArrayList;
import java.util.List;

public class ShowCategoryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<ItemCategory> items;
    private Button confirmChangesButton;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_category);



        recyclerView = findViewById(R.id.recycleview);
        items = new ArrayList<>();

        // Отримайте посилання на базу даних Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference().child("categories");

        // Додайте слухача для отримання даних з Firebase
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                // Проходьтеся по даним із Firebase та додавайте категорії до списку
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String categoryName = snapshot.child("name").getValue(String.class);
                    String categoryImageUrl = snapshot.child("imageUrl").getValue(String.class);

                    items.add(new ItemCategory(categoryName, categoryImageUrl));
                }

                recyclerView.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Обробка помилок, якщо потрібно
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new MyAdapter(getApplicationContext(), items));
    }
}
