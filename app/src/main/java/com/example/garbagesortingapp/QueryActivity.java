package com.example.garbagesortingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import androidx.annotation.NonNull;


public class QueryActivity extends AppCompatActivity {
    private static final String TAG = QueryActivity.class.getSimpleName();
    private Toolbar tlb2;
    private TextView txvReturn2;
    private ImageView imvQuery;
    private ImageView imageViewUpload;
    private ImageButton imgBtnAccount2;
    private Button btnSearch;
    private Button btnScan2;
    private Button btnScan3;
    private EditText edtSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.query);
        //Get component
        tlb2 = findViewById(R.id.tlb2);
        // Use current Toolbar as app bar
        setSupportActionBar(tlb2);
        txvReturn2 = findViewById(R.id.txvReturn2);
        imvQuery = findViewById(R.id.imvQuery);
        imageViewUpload = findViewById(R.id.imageViewUpload);
        imgBtnAccount2 = findViewById(R.id.imgBtnAccount2);
        btnSearch = findViewById(R.id.btnSearch);
        btnScan2 = findViewById(R.id.btnScan2);
        btnScan3 = findViewById(R.id.btnScan3);
        edtSearch = findViewById(R.id.edtSearch);


        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchText = edtSearch.getText().toString();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                // Read data
                db.collection("waste_classification")
                        .whereEqualTo("name", searchText)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        // Each query result has only one matching document
                                        String name = document.getString("name");
                                        String category = document.getString("category");
                                        String binName = document.getString("binName");
                                        String information = document.getString("information");

                                        // Use Intent to pass these values to the Answer page
                                        Intent intent = new Intent(QueryActivity.this, AnswerActivity.class);
                                        intent.putExtra("name", name);
                                        intent.putExtra("binName", binName);
                                        intent.putExtra("category", category);
                                        intent.putExtra("information", information);
                                        startActivity(intent);
                                        break; // Because only the first matching result is processed
                                    }
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });
            }
        });




    }

}
