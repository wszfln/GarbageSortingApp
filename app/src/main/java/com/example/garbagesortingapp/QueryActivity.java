package com.example.garbagesortingapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.label.TensorLabel;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.label.Category;

import android.Manifest;

public class QueryActivity extends AppCompatActivity {
    private static final String TAG = QueryActivity.class.getSimpleName();
    private Toolbar tlb2;
    private ImageButton imgBtnReturn2;
    private ImageView imvQuery;
    private ImageView imageViewUpload;
    private ImageButton imgBtnAccount2;
    private Button btnSearch;
    private Button btnScan2;
    private Button btnScan3;
    private AutoCompleteTextView edtSearch;
    private Interpreter tflite;
    private static final int PERMISSION_REQUEST_CAMERA = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private ActivityResultLauncher<Intent> takePictureLauncher;
    private List<String> labels = new ArrayList<>();
    private ActivityResultLauncher<Intent> selectImageLauncher;
    private Bitmap selectedImageBitmap; // Used to store images selected by the user
    private Button btnReset;

    private void loadLabels() {
        try {
            // Open the labels file from assets
            InputStream is = getAssets().open("labels.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            labels = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                labels.add(line); // Add the read label to the list
            }
            reader.close();
            Log.d(TAG, "Labels loaded successfully.");
        } catch (IOException e) {
            Log.e(TAG, "Error loading labels from file", e);
        }
    }

    // load models
    private void loadModel() {
        try {
            try (AssetFileDescriptor fileDescriptor = this.getAssets().openFd("saved_model.tflite")) {
                try (FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor())) {
                    FileChannel fileChannel = inputStream.getChannel();
                    long startOffset = fileDescriptor.getStartOffset();
                    long declaredLength = fileDescriptor.getDeclaredLength();
                    Interpreter.Options options = new Interpreter.Options();
                    tflite = new Interpreter(fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength), options);
                    Log.d(TAG, "Model loaded successfully.");
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "IOException loading the tflite file", e);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.query);
        //Get component
        tlb2 = findViewById(R.id.tlb2);
        // Use current Toolbar as app bar
        setSupportActionBar(tlb2);
        imgBtnReturn2 = findViewById(R.id.imgBtnReturn2);
        imvQuery = findViewById(R.id.imvQuery);
        imageViewUpload = findViewById(R.id.imageViewUpload);
        imgBtnAccount2 = findViewById(R.id.imgBtnAccount2);
        btnSearch = findViewById(R.id.btnSearch);
        btnScan2 = findViewById(R.id.btnScan2);
        btnScan3 = findViewById(R.id.btnScan3);
        edtSearch = findViewById(R.id.edtSearch);
        btnReset = findViewById(R.id.btnReset);

        boolean isTourist = getIntent().getBooleanExtra("isTourist", false);
        if (isTourist) {
            disableUserSpecificFeatures();
        }

        // 初始化Activity Result Launchers
        initLaunchers();
        loadModel();
        loadLabels();
        initializeCamera();

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

        // Initialize the camera when the scan button is clicked
        btnScan2.setOnClickListener(view -> takePictureLauncher.launch(new Intent(MediaStore.ACTION_IMAGE_CAPTURE)));

        // Select part of photo query from album
        // Set a click listener for imageViewUpload to launch the image picker
        imageViewUpload.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            selectImageLauncher.launch(intent);
        });

        btnScan3.setOnClickListener(v -> {
            if (selectedImageBitmap != null) {
                // Use the model to analyze the picture to obtain the itemName
                String itemName = classifyImage(selectedImageBitmap);
                // Call Firebase query and navigate
                queryFirebaseAndNavigate(itemName);
            } else {
                Toast.makeText(QueryActivity.this, "Please select an image first.", Toast.LENGTH_SHORT).show();
            }
        });

        imgBtnReturn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetActivity();
                finish();
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetActivity();
            }
        });

        imgBtnAccount2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QueryActivity.this, UserProfileActivity.class);
                startActivity(intent);
            }
        });

    }

    private void initLaunchers() {
        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bundle extras = result.getData().getExtras();
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        String itemName = classifyImage(imageBitmap);
                        queryFirebaseAndNavigate(itemName);
                    }
                }
        );

        selectImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                            imageViewUpload.setImageBitmap(selectedImageBitmap);
                        } catch (IOException e) {
                            Log.e(TAG, "IOException when converting Uri to Bitmap", e);
                        }
                    }
                }
        );
    }

    private void initializeCamera() {
        // Check if camera permission already exists
        Log.d(TAG, "Initializing camera");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // No permission, need to request
            Log.d(TAG, "Camera permission not granted. Requesting permission.");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    PERMISSION_REQUEST_CAMERA);
        } else {
            // Already have permission to start the camera
            Log.d(TAG, "Camera permission granted. Opening camera.");
            dispatchTakePictureIntent();
        }
    }


    private ByteBuffer convertBitmapToByteBuffer(Bitmap bitmap) {
        // The expected input size of the model
        int DIM_IMG_SIZE_X = 224;
        int DIM_IMG_SIZE_Y = 224;
        int DIM_PIXEL_SIZE = 3; // For RGB images

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, DIM_IMG_SIZE_X, DIM_IMG_SIZE_Y, true);

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y * DIM_PIXEL_SIZE);
        byteBuffer.order(ByteOrder.nativeOrder());
        int[] intValues = new int[DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y];

        resizedBitmap.getPixels(intValues, 0, resizedBitmap.getWidth(), 0, 0, resizedBitmap.getWidth(), resizedBitmap.getHeight());
        int pixel = 0;
        for (int i = 0; i < DIM_IMG_SIZE_X; ++i) {
            for (int j = 0; j < DIM_IMG_SIZE_Y; ++j) {
                final int val = intValues[pixel++];
                byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 255.f));
                byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 255.f));
                byteBuffer.putFloat((val & 0xFF) * (1.f / 255.f));
            }
        }
        return byteBuffer;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            // Check request code and results
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, launch camera
                dispatchTakePictureIntent();
            } else {
                // Permission denied, explain to user why this permission is required
                Toast.makeText(this, "Camera permission is needed to scan items", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            takePictureLauncher.launch(takePictureIntent);
        }
    }

    private String classifyImage(Bitmap image) {
        if (labels == null || labels.isEmpty()) {
            Log.e(TAG, "Labels are not loaded");
            return "Labels not loaded";
        }
        ByteBuffer inputBuffer = convertBitmapToByteBuffer(image);
        // Run inference
        TensorBuffer outputBuffer = TensorBuffer.createFixedSize(new int[]{1, labels.size()}, DataType.FLOAT32);
        tflite.run(inputBuffer, outputBuffer.getBuffer());
        // Obtain the output probabilities from the output buffer
        float[] probabilities = outputBuffer.getFloatArray();
        // Find the index of the label with the highest probability
        int maxIndex = -1;
        float maxProb = 0.0f;
        for (int i = 0; i < probabilities.length; i++) {
            if (probabilities[i] > maxProb) {
                maxProb = probabilities[i];
                maxIndex = i;
            }
        }
        // Handle the case where no max index is found
        if (maxIndex == -1) {
            return "Unknown";
        }
        // Return the label corresponding to the highest probability
        return labels.get(maxIndex);
    }

    private void queryFirebaseAndNavigate(String itemName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("waste_classification")
                .whereEqualTo("name", itemName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            navigateToAnswerActivity(document);
                            break; // Only take the first match
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                        Toast.makeText(QueryActivity.this, "Error fetching data.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void navigateToAnswerActivity(QueryDocumentSnapshot document) {
        Intent intent = new Intent(QueryActivity.this, AnswerActivity.class);
        intent.putExtra("name", document.getString("name"));
        intent.putExtra("binName", document.getString("binName"));
        intent.putExtra("category", document.getString("category"));
        intent.putExtra("information", document.getString("information"));
        startActivity(intent);
    }

    private void resetActivity() {
        edtSearch.setText(""); // Clear search box contents
        imageViewUpload.setImageDrawable(null); // Clear image preview
        selectedImageBitmap = null; // Clear selected images
    }

    // Hide or disable certain features
    private void disableUserSpecificFeatures() {
        imgBtnAccount2.setVisibility(View.GONE);
    }

}
