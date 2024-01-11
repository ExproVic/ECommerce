package com.example.ecommerce;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AcceptDeliveryActivity extends AppCompatActivity {

    private Spinner spinnerCartConfirm;
    private Button confirmButton;
    private Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_delivery);

        spinnerCartConfirm = findViewById(R.id.spinnerCartCofirm);
        confirmButton = findViewById(R.id.Confirmcart);
        cancelButton = findViewById(R.id.Cancelcart);
        confirmButton.setOnClickListener(v -> onConfirmButtonClick());
        cancelButton.setOnClickListener(v -> onCancelButtonClick());

        setupSpinnerAcceptDelivery();
    }
    private void onCancelButtonClick() {
        String selectedUserId = spinnerCartConfirm.getSelectedItem().toString();

        String decision = "Cancel";

        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("Orders").child(selectedUserId);

        Map<String, Object> orderData = new HashMap<>();
        orderData.put("decision", decision);

        ordersRef.setValue(orderData);

        DatabaseReference acceptDeliveryRef = FirebaseDatabase.getInstance().getReference("accept_delivery");
        acceptDeliveryRef.child(selectedUserId).removeValue();
        setupSpinnerAcceptDelivery();

    }
    private void onConfirmButtonClick() {
        String selectedUserId = spinnerCartConfirm.getSelectedItem().toString();

        String decision = "Confirm";

        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("Orders").child(selectedUserId);

        Map<String, Object> orderData = new HashMap<>();
        orderData.put("decision", decision);

        ordersRef.setValue(orderData);

        DatabaseReference acceptDeliveryRef = FirebaseDatabase.getInstance().getReference("accept_delivery");
        acceptDeliveryRef.child(selectedUserId).removeValue();
        setupSpinnerAcceptDelivery();

    }
    private void setupSpinnerAcceptDelivery() {
        Query query = FirebaseDatabase.getInstance().getReference("accept_delivery");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> deliveryIds = new ArrayList<>();

                for (DataSnapshot deliverySnapshot : snapshot.getChildren()) {
                    String deliveryId = deliverySnapshot.getKey();
                    deliveryIds.add(deliveryId);
                }

                if (deliveryIds.isEmpty()) {
                    deliveryIds.add("-");
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(AcceptDeliveryActivity.this, android.R.layout.simple_spinner_dropdown_item, deliveryIds);
                spinnerCartConfirm.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

}