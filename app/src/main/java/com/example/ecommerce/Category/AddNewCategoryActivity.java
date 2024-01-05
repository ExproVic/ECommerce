package com.example.ecommerce.Category;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ecommerce.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class AddNewCategoryActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private StorageReference mStorageRef;
    private EditText categoryEditText;
    private Uri imageUri;
    private Button addCategoryButton;
    private ImageView categoryImage;
    private static final int GALLERY_PICK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_categoty);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference("category_images");
        categoryEditText = findViewById(R.id.Category_name);
        addCategoryButton = findViewById(R.id.addNewCategory);
        categoryImage = findViewById(R.id.select_product_image);

        categoryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent, "Choose the image for category"), GALLERY_PICK);
            }
        });
        DatabaseReference categoriesRef = mDatabase.child("categories");
        addCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String categoryName = categoryEditText.getText().toString();

                if (!categoryName.isEmpty()) {
                    if (imageUri != null) {
                        StorageReference imageRef = mStorageRef.child(categoryName).child(imageUri.getLastPathSegment());

                        UploadTask uploadTask = imageRef.putFile(imageUri);

                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                while (!urlTask.isSuccessful()) ;
                                Uri downloadUrl = urlTask.getResult();

                                Map<String, Object> categoryData = new HashMap<>();
                                categoryData.put("name", categoryName);
                                categoryData.put("imageUrl", downloadUrl.toString());

                                // Додаємо інформацію про обрану радіо-кнопку
                                RadioGroup radioGroup = findViewById(R.id.radiogrourpSize);
                                int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();
                                RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
                                String buttonSize = selectedRadioButton.getText().toString();
                                categoryData.put("buttonSize", buttonSize);

                                categoriesRef.child(categoryName).setValue(categoryData);

                                categoryEditText.setText("");
                                Toast.makeText(AddNewCategoryActivity.this, "Category added successfully", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AddNewCategoryActivity.this, "Error loading image", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(AddNewCategoryActivity.this, "Select a category image", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddNewCategoryActivity.this, "The category name field is empty", Toast.LENGTH_SHORT).show();
                }
            }
        });




    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
            // Отримайте URI вибраного зображення з галереї
            imageUri = data.getData();

            // Встановіть обране зображення для ImageView
            categoryImage.setImageURI(imageUri);
        }
    }

}
