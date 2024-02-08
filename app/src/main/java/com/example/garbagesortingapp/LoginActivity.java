package com.example.garbagesortingapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

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

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
}

