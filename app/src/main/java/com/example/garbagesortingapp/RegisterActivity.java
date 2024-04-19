package com.example.garbagesortingapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private TextView txvTitle3;
    private TextView txvTitle4;
    private EditText edtName;
    private EditText edtEmail2;
    private EditText edtPassword2;
    private EditText edtConfirm;
    private Button btnRegister;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        //Get component
        txvTitle3 = findViewById(R.id.txvTitle3);
        txvTitle4 = findViewById(R.id.txvTitle4);
        edtName = findViewById(R.id.edtName);
        edtEmail2 = findViewById(R.id.edtEmail2);
        edtPassword2 = findViewById(R.id.edtPassword2);
        edtConfirm = findViewById(R.id.edtConfirm);
        btnRegister = findViewById(R.id.btnRegister);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        String userName = edtName.getText().toString().trim();
        String email = edtEmail2.getText().toString().trim();
        String password = edtPassword2.getText().toString().trim();
        String confirmPassword = edtConfirm.getText().toString().trim();

        // Check if the password and confirm password fields match
        if (!password.equals(confirmPassword)) {
            Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_LONG).show();
            return;
        }

        if (!email.isEmpty() && !password.isEmpty() && !userName.isEmpty()) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            String userId = mAuth.getCurrentUser().getUid();
                            writeNewUser(userId, userName, email);
                            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                        } else {
                            Toast.makeText(RegisterActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_LONG).show();
        }
    }

    private void writeNewUser(String userId, String name, String email) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> user = new HashMap<>();
        user.put("username", name);
        user.put("email", email);
        // Initialize other fields to default values or leave them blank
        user.put("gender", ""); // Empty string or you can use default value
        user.put("dateOfBirth", "");
        user.put("address", "");

        db.collection("users").document(userId).set(user)
                .addOnSuccessListener(aVoid -> Log.d("Auth", "DocumentSnapshot added with ID: " + userId))
                .addOnFailureListener(e -> Log.w("Auth", "Error adding document", e));
    }

    public static class User {
        public String username;
        public String email;
        public String gender;
        public String dateOfBirth;
        public String address;

        public User(String username, String email, String gender, String dateOfBirth, String address) {
            this.username = username;
            this.email = email;
            this.gender = gender;
            this.dateOfBirth = dateOfBirth;
            this.address = address;
        }
    }
}

