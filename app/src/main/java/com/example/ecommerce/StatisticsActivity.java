package com.example.ecommerce;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StatisticsActivity extends AppCompatActivity {

    private TextView statisticCategory;
    private TextView statisticProduct;
    private TextView statisticUsers;

    private DatabaseReference categoriesRef;
    private DatabaseReference productsRef;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        statisticCategory = findViewById(R.id.statisticCategory);
        statisticProduct = findViewById(R.id.statisticProduct);
        statisticUsers = findViewById(R.id.statisticUsers);

        categoriesRef = FirebaseDatabase.getInstance().getReference().child("categories");
        productsRef = FirebaseDatabase.getInstance().getReference().child("products");
        usersRef = FirebaseDatabase.getInstance().getReference().child("users");

        fetchStatistics();
    }

    private void fetchStatistics() {
        categoriesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long categoryCount = dataSnapshot.getChildrenCount();
                statisticCategory.setText("Category: " + categoryCount);

                long totalProductCount = 0;

                for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                    if (categorySnapshot.hasChild("Products")) {
                        long productCount = categorySnapshot.child("Products").getChildrenCount();
                        totalProductCount += productCount;
                    }
                }

                statisticProduct.setText("Products: " + totalProductCount);

                usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        long employeeCount = 0;

                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            String role = userSnapshot.child("role").getValue(String.class);
                            if ("employee".equals(role)) {
                                employeeCount++;
                            }
                        }

                        statisticUsers.setText("Users: " + employeeCount);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }



}


