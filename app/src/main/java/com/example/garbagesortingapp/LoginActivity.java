package com.example.garbagesortingapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoginActivity extends AppCompatActivity {

    private TextView txvTitle1;
    private TextView txvTitle2;
    private TextView txvTitle5;
    private EditText edtEmail;
    private EditText edtPassword;
    private Button btnLogin;
    private TextView txvTitle6;
    private Button btnTourist;
    private TextView txvTitle7;
    private TextView txvTitle8;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        //Get component
        txvTitle1 = findViewById(R.id.txvTitle1);
        txvTitle2 = findViewById(R.id.txvTitle2);
        txvTitle5 = findViewById(R.id.txvTitle5);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txvTitle6 = findViewById(R.id.txvTitle6);
        btnTourist = findViewById(R.id.btnTourist);
        txvTitle7 = findViewById(R.id.txvTitle7);
        txvTitle8 = findViewById(R.id.txvTitle8);

        mAuth = FirebaseAuth.getInstance();


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });
        btnTourist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        txvTitle8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an Intent object and jump from LoginActivity to RegisterActivity
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                // Start the Intent using the startActivity method
                startActivity(intent);
            }
        });

    }

    private void loginUser() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (!email.isEmpty() && !password.isEmpty()) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        } else {
                            // Check the error type and make corresponding prompts
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidUserException e) {
                                // User does not exist
                                Toast.makeText(LoginActivity.this, "Please register first", Toast.LENGTH_LONG).show();
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                // Incorrect password or incorrect account format
                                Toast.makeText(LoginActivity.this, "Wrong login information", Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                // Other errors
                                Toast.makeText(LoginActivity.this, "Login failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_LONG).show();
        }
    }
}

