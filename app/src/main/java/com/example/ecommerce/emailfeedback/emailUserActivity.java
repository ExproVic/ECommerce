package com.example.ecommerce.emailfeedback;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class emailUserActivity extends AppCompatActivity {

    private String userId;
    private List<ReceiptItem> receiptItemList;
    private ReceiptAdapter receiptAdapter;
    private int totalcount=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_user);

        RecyclerView recyclerView = findViewById(R.id.recycleviewEmail);

        receiptItemList = new ArrayList<>();
        receiptAdapter = new ReceiptAdapter(receiptItemList);

        recyclerView.setAdapter(receiptAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            userId = currentUser.getUid();
            getReceiptData();
        }
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("accept_delivery").child(userId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    showAlertMessage("No new messages");
                    return;
                }

                if (dataSnapshot.child("decision").exists()) {
                    String decision = dataSnapshot.child("decision").getValue(String.class);
                    if ("Confirm".equals(decision)) {
                        showConfirmationDialog(decision);
                    } else if ("Cancel".equals(decision)) {
                        showAlertMessage("Your purchase request was not confirmed, please try again.");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });




    }

    private void showConfirmationDialog(String decision) {
        String message;

        if ("Confirm".equals(decision)) {
            message = "Congratulations, your purchase has been confirmed and collected for delivery, so expect it soon.";
        } else if ("Cancel".equals(decision)) {
            message = "Your purchase request was not confirmed, please try again.";
            showAlertMessage(message);
            return;
        } else {
            // За замовчуванням
            message = "Your purchase status is unknown.";
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation");
        builder.setMessage(message);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); // Закриваєте діалогове вікно
            }
        });

        builder.show();
    }


    private void showAlertMessage(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }



    private void getReceiptData() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("accept_delivery").child(userId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    receiptItemList.clear();
                    double totalAmount = 0.0;

                    for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                        if (productSnapshot.child("Pname").exists() && productSnapshot.child("count").exists() &&
                                productSnapshot.child("price").exists() && !productSnapshot.hasChild("decision") &&
                                !productSnapshot.hasChild("totalAmount")) {

                            String receiptName = productSnapshot.child("Pname").getValue(String.class);
                            Integer countInteger = productSnapshot.child("count").getValue(Integer.class);
                            totalcount=countInteger+totalcount;
                            int receiptCount = (countInteger != null) ? countInteger : 0;

                            Double priceDouble = productSnapshot.child("price").getValue(Double.class);
                            double receiptPrice = (priceDouble != null) ? priceDouble : 0.0;

                            totalAmount += receiptCount * receiptPrice;

                            ReceiptItem receiptItem = new ReceiptItem(receiptName, receiptCount, receiptPrice);
                            receiptItemList.add(receiptItem);
                        }
                    }

                    ReceiptItem totalAmountItem = new ReceiptItem("Total Amount", totalcount, totalAmount);
                    receiptItemList.add(totalAmountItem);

                    receiptAdapter.notifyDataSetChanged();
                } else {
                    // Обробка випадку, коли немає даних для конкретного користувача
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Обробка помилок при отриманні даних
                // Можете додати код обробки помилок тут, якщо потрібно
            }
        });
    }

}