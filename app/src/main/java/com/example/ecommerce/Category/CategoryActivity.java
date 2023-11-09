package com.example.ecommerce.Category;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ecommerce.AddNewProductsActivity;
import com.example.ecommerce.R;

public class CategoryActivity extends AppCompatActivity {
    private ImageView suit, dress, childrensuit, watch, coat;
    private ImageView smartphone, manshoes, womanshoes, sneakers, childrenshoes;
    private EditText filterEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        init();

        // Додавання фільтрації за допомогою EditText
        filterEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String selectedCategory = filterEditText.getText().toString().trim();
                    Intent intent = new Intent(CategoryActivity.this, AddNewProductsActivity.class);
                    intent.putExtra("category", selectedCategory);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });


        // Додавання функціональності натискання на зображення
        suit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCategory("suit");
            }
        });

        dress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCategory("dress");
            }
        });

        childrensuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCategory("children suit");
            }
        });

        watch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCategory("watch");
            }
        });

        coat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCategory("coat");
            }
        });

        smartphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCategory("smartphone");
            }
        });

        manshoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCategory("man shoes");
            }
        });

        womanshoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCategory("woman shoes");
            }
        });

        sneakers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCategory("sneakers");
            }
        });

        childrenshoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCategory("children shoes");
            }
        });
    }

    private void openCategory(String category) {
        Intent intent = new Intent(CategoryActivity.this, AddNewProductsActivity.class);
        intent.putExtra("category", category);
        startActivity(intent);
    }

    private void init() {
        suit = findViewById(R.id.suit);
        dress = findViewById(R.id.dress);
        childrensuit = findViewById(R.id.childrenSuit);
        watch = findViewById(R.id.watch);
        coat = findViewById(R.id.coat);
        smartphone = findViewById(R.id.smartphone);
        manshoes = findViewById(R.id.manshoes);
        womanshoes = findViewById(R.id.womanshoes);
        sneakers = findViewById(R.id.sneakers);
        childrenshoes = findViewById(R.id.childrenshoes);
        filterEditText = findViewById(R.id.filterEditText);
    }
}
