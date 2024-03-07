package com.example.garbagesortingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;


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
    private AutoCompleteTextView edtSearch;

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

        // Provide dropdown list items for AutoCompleteTextView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line);
        edtSearch.setAdapter(adapter);

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // User input is not empty and has changed, perform the partial search
                if (!s.toString().trim().isEmpty()) {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("waste_classification")
                            .orderBy("name")
                            .startAt(s.toString())
                            .endAt(s.toString() + "\uf8ff")
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    List<String> suggestions = new ArrayList<>();
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        suggestions.add(document.getString("name"));
                                    }
                                    // Update the adapter with the new suggestions
                                    adapter.clear();
                                    adapter.addAll(suggestions);
                                    adapter.notifyDataSetChanged();
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Set item click listener for the suggestions
        edtSearch.setOnItemClickListener((parent, view, position, id) -> {
            String selection = (String) parent.getItemAtPosition(position);
            edtSearch.setText(selection);
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the search text from EditText
                String searchText = edtSearch.getText().toString();
                // Check if searchText is empty
                if (searchText.isEmpty()) {
                    Toast.makeText(QueryActivity.this, "Please enter a name to search.", Toast.LENGTH_SHORT).show();
                    return;
                }
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                // Read data
                db.collection("waste_classification")
                        .whereEqualTo("name", searchText)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    boolean isDocumentFound = false;
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        isDocumentFound = true;
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
                                    // If the document is not found, show an error message
                                    if (!isDocumentFound) {
                                        Toast.makeText(QueryActivity.this, "No matching item found.", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                    Toast.makeText(QueryActivity.this, "Error fetching data.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });




    }

}
