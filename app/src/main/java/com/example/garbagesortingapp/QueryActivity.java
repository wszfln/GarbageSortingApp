package com.example.garbagesortingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;


public class QueryActivity extends AppCompatActivity {
    private Toolbar tlb2;
    private TextView txvReturn2;
    private ImageView imvQuery;
    private ImageView imageViewUpload;
    private ImageButton imgBtnAccount2;
    private Button btnSearch;
    private Button btnScan2;
    private Button btnScan3;
    private EditText edtSearch;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.query);
        //Get component
        tlb2 = findViewById(R.id.tlb2);
        txvReturn2 = findViewById(R.id.txvReturn2);
        imvQuery = findViewById(R.id.imvQuery);
        imageViewUpload = findViewById(R.id.imageViewUpload);
        imgBtnAccount2 = findViewById(R.id.imgBtnAccount2);
        btnSearch = findViewById(R.id.btnSearch);
        btnScan2 = findViewById(R.id.btnScan2);
        btnScan3 = findViewById(R.id.btnScan3);
        edtSearch = findViewById(R.id.edtSearch);




    }

}
