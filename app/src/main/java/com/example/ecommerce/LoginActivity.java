package com.example.ecommerce;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ecommerce.Category.ShowCategoryActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    EditText email,password;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth=FirebaseAuth.getInstance();
        email =findViewById(R.id.email);
        password =findViewById(R.id.password);

    }
    public void signUp(View view){
        startActivity(new Intent(LoginActivity.this,RegistrationActivity.class));
    }
    public void signIn(View view){

        String userEmail=email.getText().toString();
        String userPassword=password.getText().toString();

        if(TextUtils.isEmpty(userEmail)){
            Toast.makeText(this,"Enter Email",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(userPassword)){
            Toast.makeText(this,"Enter Password",Toast.LENGTH_SHORT).show();
            return;
        }

        if (userPassword.length()<7){
            Toast.makeText(this,"Password too short, enter minimum 7 characters",Toast.LENGTH_SHORT).show();
            return;

        }
        auth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = auth.getCurrentUser();
                    if (user != null) {
                        if ("admin@gmail.com".equals(user.getEmail()) && "admin123".equals(userPassword)) {
                            // login - admin@gmail.com і password - admin123
                            Intent intent = new Intent(LoginActivity.this, SuperAdminActivity.class);
                            startActivity(intent);
                        } else {
                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
                            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String userRole = snapshot.child("role").getValue(String.class);
                                        if ("employee".equals(userRole)) {
                                            Intent intent = new Intent(LoginActivity.this, AdminPanelActivity.class);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(LoginActivity.this, ShowCategoryActivity.class);
                                            startActivity(intent);
                                        }
                                    }


                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(LoginActivity.this, "Error reading user role", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Error" + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}