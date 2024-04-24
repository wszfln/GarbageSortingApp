package com.example.garbagesortingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Random;

public class QuizActivity extends AppCompatActivity {
    private Toolbar tlb8;
    private ImageButton imgBtnReturn8;
    private ImageButton imgBtnAccount8;
    private FirebaseFirestore db;
    private TextView questionTextView, infoTextView;
    private RadioGroup answersGroup;
    private Button nextButton;
    private DocumentSnapshot lastResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz);
        tlb8 = findViewById(R.id.tlb8);
        // Set the Toolbar as the app bar for the activity
        setSupportActionBar(tlb8);
        imgBtnReturn8 = findViewById(R.id.imgBtnReturn8);
        imgBtnAccount8 = findViewById(R.id.imgBtnAccount8);
        questionTextView = findViewById(R.id.questionText);
        infoTextView = findViewById(R.id.infoText);
        answersGroup = findViewById(R.id.answersGroup);
        nextButton = findViewById(R.id.nextButton);

        boolean isTourist = getIntent().getBooleanExtra("isTourist", false);
        if (isTourist) {
            disableUserSpecificFeatures();
        }

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Load the first question
        loadNewQuestion();

        // Setup listener for next question button
        nextButton.setOnClickListener(view -> loadNewQuestion());

        // Setup listener for answer selection
        answersGroup.setOnCheckedChangeListener((group, checkedId) -> {
            // Before trying to get the button text, check if the ID is not -1, which means an option is selected
            if(checkedId != -1) {
                RadioButton selectedOption = findViewById(checkedId);
                if (selectedOption != null) {
                    checkAnswer(selectedOption.getText().toString());
                }
            }
        });

        imgBtnReturn8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imgBtnAccount8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuizActivity.this, UserProfileActivity.class);
                startActivity(intent);
            }
        });

    }

    // Clears the background color and selected status of all previous options
    private void clearRadioButtonStyles() {
        for (int i = 0; i < answersGroup.getChildCount(); i++) {
            RadioButton button = (RadioButton) answersGroup.getChildAt(i);
            button.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    private void loadNewQuestion() {
        clearRadioButtonStyles();
        // Temporarily remove the listener
        answersGroup.setOnCheckedChangeListener(null);

        // Clear selected status
        answersGroup.clearCheck();
        // Re-add the listener after the new question is loaded and UI is ready
        answersGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != -1) {
                RadioButton selectedOption = findViewById(checkedId);
                if (selectedOption != null) {
                    checkAnswer(selectedOption.getText().toString());
                }
            }
        });
        // Generate random ID
        int randomId = new Random().nextInt(88) + 1; // ID from 1 to 88

        // Query issues using random IDs
        db.collection("waste_classification")
                .whereEqualTo("id", randomId) // Query based on generated ID
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        lastResult = queryDocumentSnapshots.getDocuments().get(0);
                        String name = lastResult.getString("name");
                        String information = lastResult.getString("information");

                        questionTextView.setText("Which of the following garbage categories does " + name + " belong to?");
                        infoTextView.setText(information);
                        infoTextView.setVisibility(View.GONE); // Initially hide the information text
                    } else {
                        // If the document corresponding to the ID is not found, you can try to load it again or display an error message.
                        Toast.makeText(QuizActivity.this, "Problem not found, please try again", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    // Processing query failed
                    Log.e("QuizActivity", "Error loading question with ID: " + randomId, e);
                    Toast.makeText(QuizActivity.this, "Loading problem failed, please check the network connection", Toast.LENGTH_LONG).show();
                    questionTextView.setText("Loading problem failed, please try again.");
                });
    }

    private void checkAnswer(String selectedAnswer) {
        if (lastResult == null) {
            return;
        }

        clearRadioButtonStyles();

        // Remove "A.", "B.", etc. prefixes from the selected answer and the text of each RadioButton before comparing
        String correctCategory = lastResult.getString("category").toLowerCase();

        RadioButton selectedRadioButton = findViewById(answersGroup.getCheckedRadioButtonId());
        String trimmedSelectedAnswer = selectedAnswer.substring(3).toLowerCase(); // the answer starts with "A. ", "B. ", etc.

        if (trimmedSelectedAnswer.equals(correctCategory)) {
            selectedRadioButton.setBackgroundColor(Color.GREEN);
        } else {
            selectedRadioButton.setBackgroundColor(Color.RED);
            // Find the RadioButton with the correct answer and set its background to green
            for (int i = 0; i < answersGroup.getChildCount(); i++) {
                RadioButton button = (RadioButton) answersGroup.getChildAt(i);
                String trimmedButtonAnswer = button.getText().toString().substring(3).toLowerCase();

                if (trimmedButtonAnswer.equals(correctCategory)) {
                    button.setBackgroundColor(Color.GREEN);
                    break;
                }
            }
        }

        // Display information text
        String information = lastResult.getString("information");
        infoTextView.setText(information);
        infoTextView.setVisibility(View.VISIBLE);
    }

    // Hide or disable certain features
    private void disableUserSpecificFeatures() {
        imgBtnAccount8.setVisibility(View.GONE);
    }
}