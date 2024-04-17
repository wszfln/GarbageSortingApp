package com.example.garbagesortingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class ExplainActivity extends AppCompatActivity {
    private Toolbar tlb7;
    private TextView txvReturn7;
    private ImageButton imgBtnAccount7;
    private ImageView imgTrashType;
    private TextView tvTrashTitle;
    private TextView tvTrashDetail;
    private Button btnStartQuiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.explain);
        tlb7 = findViewById(R.id.tlb7);
        // Set the Toolbar as the app bar for the activity
        setSupportActionBar(tlb7);
        txvReturn7 = findViewById(R.id.txvReturn7);
        imgBtnAccount7 = findViewById(R.id.imgBtnAccount7);
        imgTrashType = findViewById(R.id.imgTrashType);
        tvTrashTitle = findViewById(R.id.tvTrashTitle);
        tvTrashDetail = findViewById(R.id.tvTrashDetail);
        btnStartQuiz = findViewById(R.id.btnStartQuiz);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String trashType = extras.getString("TRASH_TYPE");
            tvTrashTitle.setText(trashType);

            switch (trashType) {
                case "Recyclable":
                    imgTrashType.setImageResource(R.drawable.ic_recyclable);
                    tvTrashDetail.setText(getString(R.string.recyclable_detail));
                    break;
                case "General":
                    imgTrashType.setImageResource(R.drawable.ic_general);
                    tvTrashDetail.setText(getString(R.string.general_detail));
                    break;
                case "Compost":
                    imgTrashType.setImageResource(R.drawable.ic_compost);
                    tvTrashDetail.setText(getString(R.string.compost_detail));
                    break;
                case "Glass":
                    imgTrashType.setImageResource(R.drawable.ic_glass);
                    tvTrashDetail.setText(getString(R.string.glass_detail));
                    break;
            }
        }

        btnStartQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ExplainActivity.this, QuizActivity.class);
                startActivity(intent);
            }
        });
    }
}