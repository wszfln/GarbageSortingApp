package com.example.garbagesortingapp;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private Toolbar tlb4;
    private ImageButton imgBtnReturn4;
    private ImageButton imgBtnAccount4;
    private GoogleMap mMap;
    private EditText locationSearch;
    private Button searchButton;
    private MapView mapView;
    private FirebaseFirestore firestore;
    private ImageView imgViewFilterGreen;
    private ImageView imgViewFilterPurple;
    private ImageView imgViewFilterBlue;
    private boolean[] categoryFilters;
    private Button btnReset;
    private Marker locationMarker; // Tags for tracking targeting features

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maps);
        // Get component
        tlb4 = findViewById(R.id.tlb4);
        // Set the Toolbar as the app bar for the activity
        setSupportActionBar(tlb4);
        imgBtnReturn4 = findViewById(R.id.imgBtnReturn4);
        imgBtnAccount4 = findViewById(R.id.imgBtnAccount4);
        locationSearch = findViewById(R.id.edtLocation);
        searchButton = findViewById(R.id.btnSearch);
        mapView = findViewById(R.id.mapView);
        imgViewFilterGreen = findViewById(R.id.imgViewFilterGreen);
        imgViewFilterPurple = findViewById(R.id.imgViewFilterPurple);
        imgViewFilterBlue = findViewById(R.id.imgViewFilterBlue);
        categoryFilters = new boolean[]{true, true, true}; //have 3 categories
        btnReset = findViewById(R.id.btnReset);
        // Setup map view
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        firestore = FirebaseFirestore.getInstance();

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String location = locationSearch.getText().toString();
                if (!location.isEmpty()) {
                    searchLocation(location);
                }
            }
        });

        imgViewFilterGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleCategoryFilter(0); // Index for Bring Banks
                updateMapMarkers();
            }
        });
        imgViewFilterPurple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleCategoryFilter(1); // Index for Civic Recycling Centres
                updateMapMarkers();
            }
        });
        imgViewFilterBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleCategoryFilter(2); // Index for Free Electrical Recycling Drop-Off Points
                updateMapMarkers();
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetFiltersAndMap();
            }
        });

        imgBtnReturn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imgBtnAccount4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity.this, UserProfileActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Set up a custom info window adapter
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                // Use custom layout
                View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_window, null);
                renderWindowText(marker, infoWindow);
                return infoWindow;
            }
            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
            private void renderWindowText(Marker marker, View view) {
                String title = marker.getTitle();
                TextView tvTitle = view.findViewById(R.id.tvTitle);
                if (!title.equals("")) {
                    tvTitle.setText(title);
                }
                String snippet = marker.getSnippet();
                TextView tvSnippet = view.findViewById(R.id.tvSnippet);
                if (!snippet.equals("")) {
                    tvSnippet.setText(snippet);
                }
            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                return true;
            }
        });
        loadAllCollectionPoints();  // Load all collection points when map is ready
    }

    private void loadAllCollectionPoints() {
        firestore.collection("collection_points")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot document : queryDocumentSnapshots) {
                                CollectionPoint point = document.toObject(CollectionPoint.class);
                                if (point != null) {
                                    try {
                                        List<Address> addresses = new Geocoder(MapsActivity.this).getFromLocationName(point.getAddress(), 1);
                                        if (!addresses.isEmpty()) {
                                            Address address = addresses.get(0);
                                            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                                            MarkerOptions markerOptions = new MarkerOptions()
                                                    .position(latLng)
                                                    .icon(BitmapDescriptorFactory.defaultMarker(getMarkerColor(point.getCategory())))
                                                    .title(point.getName())
                                                    .snippet(point.getAddress() + "\nEircode: " + point.getEircode());
                                            mMap.addMarker(markerOptions);
                                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                                        }
                                    } catch (IOException e) {
                                        Log.e("MapsActivity", "Geocode failure: ", e);
                                    }
                                }
                            }
                        } else {
                            Log.d("MapsActivity", "No collection points found.");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("MapsActivity", "Error loading collection points: ", e);
                    }
                });
    }

    private void searchLocation(String inputEircode) {
        // Find input eircode using Geocoder
        Geocoder geocoder = new Geocoder(MapsActivity.this);
        try {
            List<Address> addresses = geocoder.getFromLocationName(inputEircode, 1);
            if (addresses.isEmpty()) {
                Toast.makeText(MapsActivity.this, "No location found for the entered Eircode.", Toast.LENGTH_LONG).show();
                return;
            }
            Address address = addresses.get(0);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            runOnUiThread(() -> {
                // Move to a new location
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
                // Check if an existing location marker is there and remove it
                if (locationMarker != null) {
                    locationMarker.remove();
                }
                // Add a new location marker here and save the reference
                locationMarker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("Location for Eircode: " + inputEircode));
            });
        } catch (IOException e) {
            Toast.makeText(MapsActivity.this, "Geocoding failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("MapsActivity", "Geocode failure: ", e);
        }
    }

    private float getMarkerColor(String category) {
        if (category == null) {
            // Log the error or handle it as appropriate
            Log.e("MapsActivity", "Category is null. Defaulting to red marker.");
            return BitmapDescriptorFactory.HUE_RED; // Default color if category is null
        }

        switch (category) {
            case "Bring Banks":
                return BitmapDescriptorFactory.HUE_GREEN;
            case "Civic Recycling Centres":
                return BitmapDescriptorFactory.HUE_VIOLET;
            case "Free Electrical Recycling Drop-Off Points":
                return BitmapDescriptorFactory.HUE_BLUE;
            default:
                return BitmapDescriptorFactory.HUE_RED;
        }
    }

    // Toggle filter status
    private void toggleCategoryFilter(int index) {
        // Set all filters to inactive
        for (int i = 0; i < categoryFilters.length; i++) {
            categoryFilters[i] = false;
        }
        // Activate selected category
        categoryFilters[index] = true;
    }

    private void updateMapMarkers() {
        mMap.clear(); // Clear all marks

        firestore.collection("collection_points")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot document : queryDocumentSnapshots) {
                                CollectionPoint point = document.toObject(CollectionPoint.class);
                                if (point != null) {
                                    // Add tags only if filter allows it
                                    if (categoryFilters[getCategoryIndex(point.getCategory())]) {
                                        addMarkerForPoint(point);
                                    }
                                }
                            }
                        } else {
                            Log.d("MapsActivity", "No collection points found.");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("MapsActivity", "Error loading collection points: ", e);
                    }
                });
    }

    private void addMarkerForPoint(CollectionPoint point) {
        try {
            List<Address> addresses = new Geocoder(MapsActivity.this).getFromLocationName(point.getAddress(), 1);
            if (!addresses.isEmpty()) {
                Address address = addresses.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.defaultMarker(getMarkerColor(point.getCategory())))
                        .title(point.getName())
                        .snippet(point.getAddress() + "\nEircode: " + point.getEircode());
                mMap.addMarker(markerOptions);
            }
        } catch (IOException e) {
            Log.e("MapsActivity", "Geocode failure: ", e);
        }
    }

    private boolean shouldDisplayMarker(String category) {
        int index = getCategoryIndex(category);
        // Ensure the index is within the bounds of the array
        if (index >= 0 && index < categoryFilters.length) {
            return categoryFilters[index];
        }
        return false; // If the category is not recognized, do not display the marker
    }

    private int getCategoryIndex(String category) {
        switch (category) {
            case "Bring Banks":
                return 0;
            case "Civic Recycling Centres":
                return 1;
            case "Free Electrical Recycling Drop-Off Points":
                return 2;
            default:
                return -1; // For unrecognized categories, an invalid index is returned
        }
    }

    // Reset filters and load all locations
    private void resetFiltersAndMap() {
        // If there is an anchor mark, remove it
        if (locationMarker != null) {
            locationMarker.remove();
            locationMarker = null; // Reset anchor tag reference
        }
        // Reset filter to initial state
        for (int i = 0; i < categoryFilters.length; i++) {
            categoryFilters[i] = true;
        }
        // Reload all locations
        loadAllCollectionPoints();
    }

    // Override lifecycle callbacks for MapView
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    public static class CollectionPoint {
        private String name;
        private String category;
        private String address;
        private String eircode;

        public CollectionPoint() {}

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }

        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }

        public String getEircode() { return eircode; }
        public void setEircode(String eircode) { this.eircode = eircode; }
    }
}
