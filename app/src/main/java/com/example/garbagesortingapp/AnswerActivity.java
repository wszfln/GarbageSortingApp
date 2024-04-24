package com.example.garbagesortingapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

public class AnswerActivity extends AppCompatActivity {

    private Toolbar tlb3;
    private ImageButton imgBtnReturn3;
    private ImageButton imgBtnAccount3;
    private TextView txvName;
    private TextView txvResult;
    private ImageView imvResult;
    private TextView txvInformation;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.answer);
        //Get component
        tlb3 = findViewById(R.id.tlb3);
        // Use current Toolbar as app bar
        setSupportActionBar(tlb3);
        imgBtnReturn3 = findViewById(R.id.imgBtnReturn3);
        imgBtnAccount3 = findViewById(R.id.imgBtnAccount3);
        txvName = findViewById(R.id.txvName);
        txvResult = findViewById(R.id.txvResult);
        imvResult = findViewById(R.id.imvResult);
        txvInformation = findViewById(R.id.txvInformation);

        boolean isTourist = getIntent().getBooleanExtra("isTourist", false);
        if (isTourist) {
            disableUserSpecificFeatures();
        }

        // Get the category from the Intent
        Intent intent = getIntent();
        if (intent != null) {
            String name = intent.getStringExtra("name");
            String binName = intent.getStringExtra("binName");
            String information = intent.getStringExtra("information");
            String category = intent.getStringExtra("category");

            txvName.setText(name);
            txvResult.setText("Put it in the" + " " + binName);
            txvInformation.setText("More information:" + information);

            // Determine the image resource based on the category
            int imageResourceId = R.drawable.recycle2; // Default image if no matching category
            switch (category) {
                case "recycle":
                    imageResourceId = R.drawable.recycle2;
                    break;
                case "general":
                    imageResourceId = R.drawable.general2;
                    break;
                case "compost":
                    imageResourceId = R.drawable.kitchen2;
                    break;
                case "glass":
                    imageResourceId = R.drawable.bottle2;
                    break;
            }
            imvResult.setImageResource(imageResourceId);
        }

        imgBtnReturn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();  // End current activity
            }
        });

        imgBtnAccount3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AnswerActivity.this, UserProfileActivity.class);
                startActivity(intent);
            }
        });

    }

    // save state
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the information passed by the Intent
        outState.putString("name", txvName.getText().toString());
        outState.putString("binName", txvResult.getText().toString());
        outState.putString("information", txvInformation.getText().toString());
        outState.putInt("imageResourceId", (Integer) imvResult.getTag());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Recover text and image resources
        txvName.setText(savedInstanceState.getString("name"));
        txvResult.setText(savedInstanceState.getString("binName"));
        txvInformation.setText(savedInstanceState.getString("information"));
        imvResult.setImageResource(savedInstanceState.getInt("imageResourceId"));
    }

    // Hide or disable certain features
    private void disableUserSpecificFeatures() {
        imgBtnAccount3.setVisibility(View.GONE);
    }

}
