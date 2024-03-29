package com.example.ecommerce.cart;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.R;
import com.example.ecommerce.gopay.GoPayActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
    private String uid;
    private TextView totalItemCountTextView;



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
        updateCartView();
        setupCartAdapter();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            uid = currentUser.getUid();
            cartRef = FirebaseDatabase.getInstance().getReference("cart").child(uid);

            cartRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int countProduct = 0;

                    cartItems.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String itemName = snapshot.child("Pname").getValue(String.class);
                        Integer itemCount = snapshot.child("count").getValue(Integer.class);
                        Double priceDouble = snapshot.child("price").getValue(Double.class);
                        String imageUrl = snapshot.child("image").getValue(String.class);

                        if (itemCount != null && priceDouble != null) {
                            countProduct += itemCount;
                            double itemPrice = priceDouble.doubleValue();

                            ItemCart cartItem = new ItemCart(itemName, itemCount, itemPrice, imageUrl);
                            cartItems.add(cartItem);
                        }
                    }

                    cartAdapter.notifyDataSetChanged();

                    if (cartItems.isEmpty()) {
                        String emptyCartMessage = "Cart is Empty";
                        emptyCartTextView.setText(emptyCartMessage);
                        emptyCartTextView.setVisibility(View.VISIBLE);
                        Log.d("ShowCartActivity", emptyCartMessage);
                    } else {
                        emptyCartTextView.setVisibility(View.GONE);
                        emptyCartTextView.setText("");
                    }

                    Log.d("ShowCartActivity", "Total count of products in cart: " + countProduct);
                    updateTotalItemCount(countProduct);


                    updateTotalAmountInDatabase();


                    updateCartView();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("ShowCartActivity", "Error reading data from Firebase: " + databaseError.getMessage());
                    databaseError.toException().printStackTrace();
                }
            });
        }
    }
    private void updateTotalAmountInDatabase() {
        double totalAmount = 0;

        for (ItemCart cartItem : cartItems) {
            totalAmount += cartItem.getTotalPrice();
        }
        DatabaseReference totalAmountRef = FirebaseDatabase.getInstance().getReference().child("cart").child(uid).child("totalAmount");
        totalAmountRef.setValue(totalAmount);
    }


    private void setupCartAdapter() {
        cartAdapter.setOnRemoveClickListener(new CartAdapter.OnRemoveClickListener() {
            @Override
            public void onRemoveClick(int position) {
                ItemCart removedItem = cartItems.get(position);
                String productName = removedItem.getItemName();
                DatabaseReference productRef = cartRef.child(productName);
                productRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("ShowCartActivity", "Item removed successfully");
                            updateCartView();
                            updateTotalAmount();
                        } else {
                            Log.e("ShowCartActivity", "Error removing item: " + task.getException().getMessage());
                        }
                    }
                });
            }
        });
    }
    private void updateCartView() {
        cartAdapter.notifyDataSetChanged();
        int totalItemCount = calculateTotalItemCount();
        updateTotalItemCount(totalItemCount);
        updateTotalAmount();
    }
    private double calculateTotalAmount() {
        double totalAmount = 0;

        for (ItemCart cartItem : cartItems) {
            totalAmount += cartItem.getTotalPrice();
        }

        return totalAmount;
    }
    public void gotopay(View view) {
        updateTotalItemCount(calculateTotalItemCount());
        updateTotalAmount();
        String amount = String.valueOf(calculateTotalAmount());

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("accept_delivery").child(uid);

        cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userRef.setValue(dataSnapshot.getValue())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    cartRef.removeValue();
                                    Log.d("ShowCartActivity", "Data copied to accept_delivery successfully");
                                    updateCartView();
                                    updateTotalAmount();
                                } else {
                                    Log.e("ShowCartActivity", "Error copying data to accept_delivery: " + task.getException().getMessage());
                                }
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ShowCartActivity", "Error reading data from cart: " + databaseError.getMessage());
            }
        });

        Intent intent = new Intent(this, GoPayActivity.class);
        intent.putExtra("amount", amount);
        startActivity(intent);
    }

    private int calculateTotalItemCount() {
        int totalItemCount = 0;
        for (ItemCart cartItem : cartItems) {
            totalItemCount += cartItem.getItemCount();
        }
        return totalItemCount;
    }

    private void updateTotalItemCount(int totalItemCount) {
        if (totalItemCountTextView != null) {
            totalItemCountTextView.setText("Total items: " + totalItemCount);
        } else {
            Log.e("ShowCartActivity", "totalItemCountTextView is null");
        }
    }
    private void updateTotalAmount() {
        double totalAmount = 0;

        for (ItemCart cartItem : cartItems) {
            totalAmount += cartItem.getTotalPrice();
        }

        TextView totalAmountTextView = findViewById(R.id.topay);

        totalAmountTextView.setText(String.valueOf(totalAmount) + "$");

        updateTotalItemCount(calculateTotalItemCount());
    }
}
