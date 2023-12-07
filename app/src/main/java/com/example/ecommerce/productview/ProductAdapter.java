package com.example.ecommerce.productview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ecommerce.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ViewProductHolder> {
    private Context context;
    private List<ItemProduct> items;

    public ProductAdapter(Context context, List<ItemProduct> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public ViewProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewProductHolder holder = new ViewProductHolder(LayoutInflater.from(context).inflate(R.layout.item_view_product, parent, false), new ViewProductHolder.OnAddToCartClickListener() {
            @Override
            public void onAddToCartClick(int count) {
                // Опрацювання події додавання товару в кошик
                Toast.makeText(context, "Товар додано в кошик. Кількість: " + count, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAddToCartClick(ItemProduct item, int count) {
                // Отримайте UID користувача
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    String uid = currentUser.getUid();

                    // Отримайте ідентифікатор продукту з об'єкта ItemProduct
                    String productID = item.getPname();

                    DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("cart").child(uid);

                    // Перевірка чи існує вже такий ключ
                    cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String productID = null;

                            // Вибір або створення ідентифікатора продукту
                            for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                                String productName = productSnapshot.child("Pname").getValue(String.class);
                                if (productName != null && productName.equals(item.getPname())) {
                                    productID = productName; // Використовуємо ім'я продукту як ідентифікатор
                                    break;
                                }
                            }

                            // Запис даних в базу даних
                            if (productID == null) {
                                // Якщо продукт не знайдено, використовуємо ім'я продукту як ключ
                                productID = item.getPname();
                            }

                            if (count > 0) {
                                DatabaseReference productRef = cartRef.child(productID);
                                productRef.child("image").setValue(item.getImage());
                                productRef.child("Pname").setValue(item.getPname());
                                productRef.child("price").setValue(Double.parseDouble(item.getPrice()));
                                productRef.child("count").setValue(count);
                                productRef.child("totalAmount").setValue(count * Double.parseDouble(item.getPrice()));

                                Toast.makeText(context, "Товар додано в кошик", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Кількість товару повинна бути більше 0", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Обробка помилок бази даних, якщо потрібно
                        }
                    });

                }
            }
        });

        holder.setItems(items);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewProductHolder holder, int position) {
        ItemProduct item = items.get(position);
        holder.nameProductView.setText(item.getPname());
        holder.descriptionProductView.setText(item.getDescription());
        holder.priceProductView.setText(item.getPrice());

        Glide.with(context).load(item.getImage()).into(holder.imageProductView);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
