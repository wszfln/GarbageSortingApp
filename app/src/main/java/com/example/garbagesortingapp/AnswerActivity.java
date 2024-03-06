package com.example.garbagesortingapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

public class AnswerActivity extends AppCompatActivity {

    private Toolbar tlb3;
    private TextView txvReturn3;
    private ImageButton imgBtnAccount3;
    private TextView txvName;
    private TextView txvResult;
    private ImageView imvResult;
    private TextView txvInformation;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.answer);
        //Get component
        tlb3 = findViewById(R.id.tlb3);
        txvReturn3 = findViewById(R.id.txvReturn3);
        imgBtnAccount3 = findViewById(R.id.imgBtnAccount3);
        txvName = findViewById(R.id.txvName);
        //The txvName component gets the Intent and reads the data in it
        Intent intent = getIntent();
        if (intent != null) {
            String receivedText = intent.getStringExtra("searchQuery"); // Use the same key as when sending to get the data
            txvName.setText(receivedText);
        }
        txvResult = findViewById(R.id.txvResult);
        imvResult = findViewById(R.id.imvResult);
        txvInformation = findViewById(R.id.txvInformation);



    }
}
