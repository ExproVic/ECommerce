package com.example.ecommerce;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddNewProductsActivity extends AppCompatActivity {

    private Spinner categorySpinner;
    private EditText productName, productDescription, productPrice;
    private ImageView productImage;
    private Button addNewProductButton;
    private static final int GALLERYPICK = 1;
    private Uri imageUri;
    private StorageReference productImageRef;
    private DatabaseReference categoriesRef;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_products);

        init();

        productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        addNewProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateProductData();
            }
        });
    }

    private void validateProductData() {
        String description = productDescription.getText().toString();
        String price = productPrice.getText().toString();
        String productNameText = productName.getText().toString();
        String selectedCategory = categorySpinner.getSelectedItem().toString();

        if (imageUri == null) {
            Toast.makeText(this, "Add a product image.", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(description)) {
            Toast.makeText(this, "Add a product description.", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(price)) {
            Toast.makeText(this, "Add the cost of the item.", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(productNameText)) {
            Toast.makeText(this, "Add the product name.", Toast.LENGTH_SHORT).show();
        } else {
            storeProductInformation(description, price, productNameText, selectedCategory);
        }
    }

    private void storeProductInformation(String description, String price, String productName, String category) {
        loadingBar.setTitle("Loading Data");
        loadingBar.setMessage("Please wait...");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("ddMMyyyy");
        String saveCurrentDate = currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HHmmss");
        String saveCurrentTime = currentTime.format(calendar.getTime());

        final String productRandomKey = saveCurrentDate + saveCurrentTime;

        final StorageReference filePath = productImageRef.child(category).child(productRandomKey + ".jpg");
        final UploadTask uploadTask = filePath.putFile(imageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.toString();
                Toast.makeText(AddNewProductsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AddNewProductsActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();

                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            String downloadImageUrl = task.getResult().toString();
                            Toast.makeText(AddNewProductsActivity.this, "Photo saved", Toast.LENGTH_SHORT).show();
                            saveProductInfoToDatabase(category, productName, description, price, downloadImageUrl, productRandomKey);
                        }
                    }
                });
            }
        });
    }

    private void saveProductInfoToDatabase(String category, String productName, String description, String price, String imageUrl, String productRandomKey) {
        DatabaseReference categoriesRef = FirebaseDatabase.getInstance().getReference().child("categories");

        DatabaseReference categoryRef = categoriesRef.child(category);

        DatabaseReference productRef = categoryRef.child("Products").child(productName);

        Map<String, Object> productData = new HashMap<>();
        productData.put("productID",productRandomKey );
        productData.put("description", description);
        productData.put("image", imageUrl);
        productData.put("price", price);
        productData.put("pname", productName);

        productRef.setValue(productData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            loadingBar.dismiss();
                            Toast.makeText(AddNewProductsActivity.this, "Product added", Toast.LENGTH_SHORT).show();
                            Intent loginIntent = new Intent(AddNewProductsActivity.this, AdminPanelActivity.class);
                            startActivity(loginIntent);
                        } else {
                            String message = task.getException().toString();
                            Toast.makeText(AddNewProductsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }
                });
    }

    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERYPICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERYPICK && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            productImage.setImageURI(imageUri);
        }
    }

    private void init() {
        productImageRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        loadingBar = new ProgressDialog(this);

        categorySpinner = findViewById(R.id.spinner);
        productName = findViewById(R.id.product_name);
        productDescription = findViewById(R.id.description);
        productPrice = findViewById(R.id.product_price);
        productImage = findViewById(R.id.select_product_image);
        addNewProductButton = findViewById(R.id.addNewproduct);

        categoriesRef = FirebaseDatabase.getInstance().getReference().child("categories");

        categoriesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> categoriesList = new ArrayList<>();
                for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                    String categoryName = categorySnapshot.getKey();
                    categoriesList.add(categoryName);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(AddNewProductsActivity.this, android.R.layout.simple_spinner_item, categoriesList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                categorySpinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
