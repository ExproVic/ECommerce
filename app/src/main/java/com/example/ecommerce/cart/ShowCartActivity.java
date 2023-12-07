package com.example.ecommerce.cart;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ShowCartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private List<ItemCart> cartItems;
    private DatabaseReference cartRef;
    private TextView emptyCartTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_cart);

        recyclerView = findViewById(R.id.recycleviewCart);
        emptyCartTextView = findViewById(R.id.emptyCartMessage);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartItems = new ArrayList<>();
        cartAdapter = new CartAdapter(this, cartItems);
        recyclerView.setAdapter(cartAdapter);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();

            cartRef = FirebaseDatabase.getInstance().getReference("cart").child(uid);

            cartRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int countProduct = 0;

                    cartItems.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String itemName = snapshot.child("Pname").getValue(String.class);
                        Integer itemCount = snapshot.child("count").getValue(Integer.class); // Змінено на Integer
                        double itemPrice = snapshot.child("price").getValue(Double.class);
                        String imageUrl = snapshot.child("image").getValue(String.class);

                        // Перевірка на null перед використанням
                        if (itemCount != null) {
                            // Використання значення itemCount
                            countProduct += itemCount.intValue();
                        }

                        ItemCart cartItem = new ItemCart(itemName, itemCount, itemPrice, imageUrl);
                        cartItems.add(cartItem);
                    }

                    cartAdapter.notifyDataSetChanged();

                    if (cartItems.isEmpty()) {
                        String emptyCartMessage = "Cart is Empty";
                        emptyCartTextView.setText(emptyCartMessage);
                        emptyCartTextView.setVisibility(View.VISIBLE); // Зробіть видимим, якщо порожній
                        Log.d("ShowCartActivity", emptyCartMessage);
                    } else {
                        emptyCartTextView.setVisibility(View.GONE); // Зробіть невидимим, якщо не порожній
                        emptyCartTextView.setText("");
                    }

                    Log.d("ShowCartActivity", "Total count of products in cart: " + countProduct);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Обробте помилку читання з бази даних
                    Log.e("ShowCartActivity", "Error reading data from Firebase: " + databaseError.getMessage());
                    databaseError.toException().printStackTrace();
                }
            });
        }
    }
}
