package com.example.ecommerce;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EmployeeManagementActivity extends AppCompatActivity {
    EditText name, email, password;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_management);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users");

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
    }

    public void signup(View view) {
        String userName = name.getText().toString();
        String userEmail = email.getText().toString();
        String userPassword = password.getText().toString();

        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(userEmail) || TextUtils.isEmpty(userPassword)) {
            Toast.makeText(this, "Enter all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userPassword.length() < 7) {
            Toast.makeText(this, "Password too short, enter minimum 7 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(EmployeeManagementActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = auth.getCurrentUser();
                    if (user != null) {
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
                        userRef.child("role").setValue("employee"); // Призначте роль "employee" для всіх нових користувачів
                        clearFields();
                    }
                    Toast.makeText(EmployeeManagementActivity.this, "Successfully Registration", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EmployeeManagementActivity.this, "Registration Failed" + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private void clearFields() {
        name.setText("");
        email.setText("");
        password.setText("");
    }
}
