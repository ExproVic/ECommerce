package com.example.ecommerce.emailfeedback;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class emailUserActivity extends AppCompatActivity {

    private List<ReceiptItem> receiptItemList;
    private ReceiptAdapter receiptAdapter;
    int country=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_user);

        // Отримайте посилання на RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recycleviewEmail);

        // Ініціалізуйте список ReceiptItem та адаптер
        receiptItemList = new ArrayList<>();
        receiptAdapter = new ReceiptAdapter(receiptItemList);

        // Встановлюємо адаптер для RecyclerView
        recyclerView.setAdapter(receiptAdapter);

        // Встановлюємо LayoutManager, якщо необхідно (наприклад, LinearLayoutManager)
        recyclerView.setLayoutManager(new LinearLayoutManager(this));




        // Додавання слухача для отримання даних
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        String userId = "YiH8XrfcpdhEU6Q5ji8yQOfwH1o1"; // Замініть це на ваш userId

        databaseReference.child("accept_delivery").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    receiptItemList.clear();
                    double totalAmount = 0.0; // Ініціалізація totalAmount

// Перевірка і додавання інших продуктів в список
                    for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                        // Перевірка, чи існують необхідні підвузли в базі даних
                        if (productSnapshot.child("Pname").exists() && productSnapshot.child("count").exists() &&
                                productSnapshot.child("price").exists() && !productSnapshot.hasChild("decision") &&
                                !productSnapshot.hasChild("totalAmount")) {

                            // Отримання значень полів з бази даних
                            String receiptName = productSnapshot.child("Pname").getValue(String.class);
                            Integer countInteger = productSnapshot.child("count").getValue(Integer.class);
                            country=country+countInteger;
                            Double priceDouble = productSnapshot.child("price").getValue(Double.class);

                            // Перевірка, чи значення не є null, перед використанням
                            int receiptCount = (countInteger != null) ? countInteger.intValue() : 0;
                            double receiptPrice = (priceDouble != null) ? priceDouble.doubleValue() : 0.0;

                            // Додавання кількості товарів до загальної суми
                            totalAmount += receiptCount * receiptPrice;

                            // Створення нового об'єкту ReceiptItem
                            ReceiptItem receiptItem = new ReceiptItem(receiptName, receiptCount, receiptPrice);

                            // Додавання об'єкту ReceiptItem в кінець списку
                            receiptItemList.add(receiptItem);
                        }
                    }
                    ReceiptItem totalAmountItem = new ReceiptItem("Total Amount", country, totalAmount);

                    receiptItemList.add(totalAmountItem);


                    // Сповіщення адаптера про зміни в списку
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
