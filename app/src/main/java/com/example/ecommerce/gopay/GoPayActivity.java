package com.example.ecommerce.gopay;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.ecommerce.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.view.CardInputWidget;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GoPayActivity extends AppCompatActivity {
    //    private static final String BACKEND_URL = "http://10.0.2.2:5001/";
    private static final String BACKEND_URL = " https://stripe-payment-7961cbdd7755.herokuapp.com/";
    String amountString = "";
    private static final int MY_PERMISSIONS_REQUEST_INTERNET = 123;
    private OkHttpClient httpClient = new OkHttpClient();
    private String paymentIntentClientSecret;
    private Stripe stripe;
    private EditText deliveryAddressEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_go_pay);
        amountString = getIntent().getStringExtra("amount");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "channel_id",
                    "Channel Name",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        deliveryAddressEditText = findViewById(R.id.deliveryAddress);
        Button saveAddressButton = findViewById(R.id.saveAddressButton);
        saveAddressButton.setOnClickListener(view -> saveDeliveryAddress());

        startCheckout();
    }
    private void saveDeliveryAddress() {
        String deliveryAddress = deliveryAddressEditText.getText().toString().trim();
        if (!deliveryAddress.isEmpty()) {
             saveAddressToFirebase(deliveryAddress);
            Toast.makeText(this, "Address saved: " + deliveryAddress, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Please enter a delivery address", Toast.LENGTH_SHORT).show();
        }
    }
    private void saveAddressToFirebase(String address) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference addressReference = FirebaseDatabase.getInstance().getReference("addresses").child(userId);
            addressReference.setValue(address)
                    .addOnSuccessListener(aVoid ->
                            Toast.makeText(GoPayActivity.this, "Address saved successfully", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(GoPayActivity.this, "Failed to save address", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(GoPayActivity.this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }


    private void displayNotification(@NonNull String title, @Nullable String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel_id")
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, MY_PERMISSIONS_REQUEST_INTERNET);
        } else {
            notificationManager.notify(/* notification_id */ 1, builder.build());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_INTERNET) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                displayNotification("Title", "Message");
            } else {

            }
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    private void startCheckout() {
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");
        double amount = Double.parseDouble(amountString)*100;
        Map<String,Object> payMap=new HashMap<>();
        Map<String,Object> itemMap=new HashMap<>();
        List<Map<String,Object>> itemList =new ArrayList<>();
        payMap.put("currency","usd");
        payMap.put("deliveryAddress", deliveryAddressEditText.getText().toString().trim());
        itemMap.put("id","photo_subscription");
        itemMap.put("amount",amount);
        itemList.add(itemMap);
        payMap.put("items",itemList);
        String json = new Gson().toJson(payMap);
        RequestBody body = RequestBody.create(json, mediaType);
        Request request = new Request.Builder()
                .url(BACKEND_URL + "create-payment-intent")
                .post(body)
                .build();
        httpClient.newCall(request)
                .enqueue(new PayCallback(this));

        Button payButton = findViewById(R.id.payButton);
        payButton.setOnClickListener((View view) -> {
            CardInputWidget cardInputWidget = findViewById(R.id.cardInputWidget);
            PaymentMethodCreateParams params = cardInputWidget.getPaymentMethodCreateParams();
            if (params != null) {
                ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams
                        .createWithPaymentMethodCreateParams(params, paymentIntentClientSecret);
                stripe.confirmPayment(this, confirmParams);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        stripe.onPaymentResult(requestCode, data, new PaymentResultCallback(this));
    }

    private void onPaymentSuccess(@NonNull final Response response) throws IOException {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String> responseMap = gson.fromJson(
                Objects.requireNonNull(response.body()).string(),
                type
        );
        String stripePublishableKey = responseMap.get("publishableKey");
        paymentIntentClientSecret = responseMap.get("clientSecret");

        stripe = new Stripe(
                getApplicationContext(),
                Objects.requireNonNull(stripePublishableKey)
        );
    }

    private static final class PayCallback implements Callback {
        @NonNull private final WeakReference<GoPayActivity> activityRef;

        PayCallback(@NonNull GoPayActivity activity) {
            activityRef = new WeakReference<>(activity);
        }

        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException e) {
            final GoPayActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }

            activity.runOnUiThread(() ->
                    Toast.makeText(
                            activity, "Error: " + e.toString(), Toast.LENGTH_LONG
                    ).show()
            );
        }

        @Override
        public void onResponse(@NonNull Call call, @NonNull final Response response)
                throws IOException {
            final GoPayActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }

            if (!response.isSuccessful()) {
                activity.runOnUiThread(() ->
                        Toast.makeText(
                                activity, "Error: " + response.toString(), Toast.LENGTH_LONG
                        ).show()
                );
            } else {
                activity.onPaymentSuccess(response);
            }
        }
    }

    private static final class PaymentResultCallback implements ApiResultCallback<PaymentIntentResult> {
        @NonNull
        private final WeakReference<GoPayActivity> activityRef;

        PaymentResultCallback(@NonNull GoPayActivity activity) {
            activityRef = new WeakReference<>(activity);
        }

        @Override
        public void onSuccess(@NonNull PaymentIntentResult result) {
            final GoPayActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }

            PaymentIntent paymentIntent = result.getIntent();
            PaymentIntent.Status status = paymentIntent.getStatus();
            if (status == PaymentIntent.Status.Succeeded) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String paymentIntentJson = gson.toJson(paymentIntent);
                activity.runOnUiThread(() ->
                        Toast.makeText(activity, "Payment completed. Your payment was successful!", Toast.LENGTH_LONG).show()
                );
                activity.deleteCartFromFirebase();
                Log.d("PaymentResultCallback", "Payment completed: " + paymentIntentJson);
            } else if (status == PaymentIntent.Status.RequiresPaymentMethod) {
                String errorMessage = Objects.requireNonNull(paymentIntent.getLastPaymentError()).getMessage();
                activity.runOnUiThread(() ->
                        Toast.makeText(activity, "Payment failed: " + errorMessage, Toast.LENGTH_LONG).show()
                );
                Log.d("PaymentResultCallback", "Payment failed: " + errorMessage);
            }
        }

        @Override
        public void onError(@NonNull Exception e) {
            final GoPayActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }
            activity.displayNotification("Error", e.toString());
            Log.e("PaymentResultCallback", "Payment error: " + e.toString());
        }
    }
    private void deleteCartFromFirebase() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference cartReference = FirebaseDatabase.getInstance().getReference("cart").child(userId);
            cartReference.removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(GoPayActivity.this, "Cart cleared successfully", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(GoPayActivity.this, "Failed to clear cart", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(GoPayActivity.this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }
}