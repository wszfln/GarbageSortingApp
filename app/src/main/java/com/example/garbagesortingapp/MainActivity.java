package com.example.garbagesortingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private TextView txvRecycle;
    private TextView txvKitchen;
    private TextView txvGeneral;
    private TextView txvBottle;
    private ImageButton imgBtnAccount;
    private ImageButton imgBtnRecycle;
    private ImageButton imgBtnGeneral;
    private ImageButton imgBtnKitchen;
    private ImageButton imgBtnBottle;
    private Button btnKnowledge;
    private Button btnQuary;
    private Button btnPosition;
    private Toolbar tlb1;
    private ImageView imv1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Get component
        txvRecycle = findViewById(R.id.txvRecycle);
        txvKitchen = findViewById(R.id.txvKitchen);
        txvGeneral = findViewById(R.id.txvGeneral);
        txvBottle = findViewById(R.id.txvBottle);
        imgBtnAccount = findViewById(R.id.imgBtnAccount);
        imgBtnRecycle = findViewById(R.id.imgBtnRecycle);
        imgBtnGeneral = findViewById(R.id.imgBtnGeneral);
        imgBtnKitchen = findViewById(R.id.imgBtnKitchen);
        imgBtnBottle = findViewById(R.id.imgBtnBottle);
        btnKnowledge = findViewById(R.id.btnKnowledge);
        btnQuary = findViewById(R.id.btnQuary);
        btnPosition = findViewById(R.id.btnPosition);
        tlb1 = findViewById(R.id.tlb1);
        // Use current Toolbar as app bar
        setSupportActionBar(tlb1);
        imv1 = findViewById(R.id.imv1);


        // Define a Runnable to execute after the view is rendered
        btnKnowledge.post(() -> {
            // Get the height of all buttons
            int btnKnowledgeHeight = btnKnowledge.getHeight();
            int btnQueryHeight = btnQuary.getHeight();
            int btnPositionHeight = btnPosition.getHeight();

            // Find the tallest button height
            int maxHeight = Math.max(btnKnowledgeHeight, Math.max(btnQueryHeight, btnPositionHeight));

            // Create layout parameters, using maximum height found
            ViewGroup.LayoutParams paramsKnowledge = btnKnowledge.getLayoutParams();
            ViewGroup.LayoutParams paramsQuery = btnQuary.getLayoutParams();
            ViewGroup.LayoutParams paramsPosition = btnPosition.getLayoutParams();

            paramsKnowledge.height = maxHeight;
            paramsQuery.height = maxHeight;
            paramsPosition.height = maxHeight;

            // Apply new layout parameters to all buttons
            btnKnowledge.setLayoutParams(paramsKnowledge);
            btnQuary.setLayoutParams(paramsQuery);
            btnPosition.setLayoutParams(paramsPosition);
        });
        imgBtnRecycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityWithExtra("Recyclable");
            }
        });
        imgBtnGeneral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityWithExtra("General");
            }
        });
        imgBtnKitchen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityWithExtra("Compost");
            }
        });
        imgBtnBottle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityWithExtra("Glass");
            }
        });
        btnKnowledge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, InformationActivity.class);
                startActivity(intent);
            }
        });
        btnQuary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an Intent to start the QueryActivity
                Intent intent = new Intent(MainActivity.this, QueryActivity.class);
                startActivity(intent);
            }
        });
        btnPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);

            }
        });

        imgBtnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UserProfileActivity.class);
                startActivity(intent);
            }
        });


    }
    private void startActivityWithExtra(String trashType) {
        Intent intent = new Intent(MainActivity.this, ExplainActivity.class);
        intent.putExtra("TRASH_TYPE", trashType);
        startActivity(intent);
    }
}