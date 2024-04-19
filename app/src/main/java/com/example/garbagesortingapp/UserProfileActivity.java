package com.example.garbagesortingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UserProfileActivity extends AppCompatActivity {
    private Toolbar tlb9;
    private ImageButton imgBtnReturn9;
    private ImageButton imgBtnAccount9;
    private TextView tvUsername, tvEmail;
    private EditText etGender, etDateOfBirth, etAddress;
    private Button btnUpdateInfo, btnLogout;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user);
        tlb9 = findViewById(R.id.tlb9);
        // Set the Toolbar as the app bar for the activity
        setSupportActionBar(tlb9);
        imgBtnReturn9 = findViewById(R.id.imgBtnReturn9);
        imgBtnAccount9 = findViewById(R.id.imgBtnAccount9);
        tvUsername = findViewById(R.id.tvUsername);
        tvEmail = findViewById(R.id.tvEmail);
        etGender = findViewById(R.id.etGender);
        etDateOfBirth = findViewById(R.id.etDateOfBirth);
        etAddress = findViewById(R.id.etAddress);
        btnUpdateInfo = findViewById(R.id.btnUpdateInfo);
        btnLogout = findViewById(R.id.btnLogout);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        loadUserData();

        btnUpdateInfo.setOnClickListener(v -> {
            // Get text from EditText and remove leading and trailing spaces
            String gender = etGender.getText().toString().trim();
            String dateOfBirth = etDateOfBirth.getText().toString().trim();
            String address = etAddress.getText().toString().trim();

            // Get the UID of the current user
            String userId = mAuth.getCurrentUser().getUid();

            // Update user information
            updateUserData(userId, gender, dateOfBirth, address);
        });
        btnLogout.setOnClickListener(v -> logoutUser());

        imgBtnReturn9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    private void loadUserData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            db.collection("users").document(userId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        tvUsername.setText(document.getString("username"));
                        tvEmail.setText(document.getString("email"));
                        etGender.setText(document.getString("gender"));
                        etDateOfBirth.setText(document.getString("dateOfBirth"));
                        etAddress.setText(document.getString("address"));
                    } else {
                        Log.d("UserProfile", "No such document");
                    }
                } else {
                    Log.w("UserProfile", "Failed to load user data", task.getException());
                }
            });
        }
    }

    private void updateUserData(String userId, String gender, String dateOfBirth, String address) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Only update user-provided information fields
        Map<String, Object> updates = new HashMap<>();
        updates.put("gender", gender);
        updates.put("dateOfBirth", dateOfBirth);
        updates.put("address", address);

        db.collection("users").document(userId).update(updates)
                .addOnSuccessListener(aVoid -> {
                    // Display a Toast message indicating successful operation
                    Toast.makeText(UserProfileActivity.this, "User info updated successfully.", Toast.LENGTH_SHORT).show();
                    Log.d("UserProfile", "User profile updated.");
                })
                .addOnFailureListener(e -> {
                    // Display Toast message for failed operation
                    Toast.makeText(UserProfileActivity.this, "Failed to update user info.", Toast.LENGTH_SHORT).show();
                    Log.w("UserProfile", "Error updating profile.", e);
                });
    }

    private void logoutUser() {
        mAuth.signOut();
        startActivity(new Intent(UserProfileActivity.this, LoginActivity.class));
        finish();
    }
}