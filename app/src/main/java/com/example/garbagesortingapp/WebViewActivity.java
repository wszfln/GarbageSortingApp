package com.example.garbagesortingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.TextView;

public class WebViewActivity extends AppCompatActivity {
    private Toolbar tlb6;
    private ImageButton imgBtnReturn6;
    private ImageButton imgBtnAccount6;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);
        tlb6 = findViewById(R.id.tlb6);
        // Set the Toolbar as the app bar for the activity
        setSupportActionBar(tlb6);
        imgBtnAccount6 = findViewById(R.id.imgBtnAccount6);
        imgBtnAccount6 = findViewById(R.id.imgBtnAccount6);
        webView = findViewById(R.id.webview);

        boolean isTourist = getIntent().getBooleanExtra("isTourist", false);
        if (isTourist) {
            disableUserSpecificFeatures();
        }

        // Enable JavaScript (if the news websites require JavaScript)
        webView.getSettings().setJavaScriptEnabled(true);

        // Get the URL from the intent
        String url = getIntent().getStringExtra("url");
        if (url != null) {
            webView.loadUrl(url);
        }


    }

    // Hide or disable certain features
    private void disableUserSpecificFeatures() {
        imgBtnAccount6.setVisibility(View.GONE);
    }
}