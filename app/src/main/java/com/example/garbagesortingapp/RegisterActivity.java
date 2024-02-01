package com.example.garbagesortingapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private TextView txvTitle3;
    private TextView txvTitle4;
    private EditText edtName;
    private EditText edtEmail2;
    private EditText edtPassword2;
    private EditText edtConfirm;
    private Button btnRegister;

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

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}
