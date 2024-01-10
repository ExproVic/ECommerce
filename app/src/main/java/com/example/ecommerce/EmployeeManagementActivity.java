package com.example.ecommerce;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import java.util.ArrayList;
import java.util.List;

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

        usersRef = FirebaseDatabase.getInstance().getReference("users");
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        setupEmployeeSpinner();

    }
    public void setupEmployeeSpinner() {
        Spinner employeeSpinner = findViewById(R.id.spinnerDeleteemployee);

        usersRef.orderByChild("role").equalTo("employee").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> employeeEmails = new ArrayList<>();
                List<String> employeeIds = new ArrayList<>();

                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String employeeEmail = userSnapshot.child("email").getValue(String.class);
                    String employeeId = userSnapshot.getKey();

                    if (employeeEmail != null && employeeId != null) {
                        employeeEmails.add(employeeEmail);
                        employeeIds.add(employeeId);
                    }
                }
                if (employeeEmails.isEmpty()) {
                    employeeEmails.add("-");
                    employeeIds.add("-");
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(EmployeeManagementActivity.this, android.R.layout.simple_spinner_item, employeeEmails);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                employeeSpinner.setAdapter(adapter);
                employeeSpinner.setTag(employeeIds);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EmployeeManagementActivity.this, "Error reading employee list", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void deleteEmployee(View view) {
        Spinner employeeSpinner = findViewById(R.id.spinnerDeleteemployee);

        int selectedPosition = employeeSpinner.getSelectedItemPosition();
        if (selectedPosition != AdapterView.INVALID_POSITION) {
            List<String> employeeIds = (List<String>) employeeSpinner.getTag();

            String selectedEmployeeId = employeeIds.get(selectedPosition);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Confirm Deletion");
            builder.setMessage("Are you sure you want to delete this employee?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Видаляємо employee з Firebase Authentication
                    FirebaseAuth.getInstance().signInWithEmailAndPassword("admin@gmail.com", "admin123") // Аутентифікація як адмін (замініть на свої дані)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                        if (firebaseUser != null) {
                                            firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        // Оновлюємо спінер після видалення
                                                        usersRef.child(selectedEmployeeId).removeValue(); // Видалення з бази даних Realtime
                                                        setupEmployeeSpinner();
                                                        Toast.makeText(EmployeeManagementActivity.this, "Employee deleted successfully", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(EmployeeManagementActivity.this, "Failed to delete employee: " + task.getException(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    } else {
                                        Toast.makeText(EmployeeManagementActivity.this, "Authentication failed: " + task.getException(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {}
            });

            builder.show();
        } else {
            Toast.makeText(this, "Please select an employee to delete", Toast.LENGTH_SHORT).show();
        }
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
                        userRef.child("role").setValue("employee");
                        userRef.child("email").setValue(userEmail);
                        userRef.child("name").setValue(userName);
                        clearFields();
                        setupEmployeeSpinner(); // Оновлення спінера після додавання нового працівника
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
