package com.example.garbagesortingapp;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private Toolbar tlb4;
    private TextView txvReturn4;
    private ImageButton imgBtnAccount4;
    private GoogleMap mMap;
    private EditText locationSearch;
    private Button searchButton;
    private MapView mapView;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maps);
        // Get component
        tlb4 = findViewById(R.id.tlb4);
        // Set the Toolbar as the app bar for the activity
        setSupportActionBar(tlb4);
        txvReturn4 = findViewById(R.id.txvReturn4);
        imgBtnAccount4 = findViewById(R.id.imgBtnAccount4);
        locationSearch = findViewById(R.id.edtLocation);
        searchButton = findViewById(R.id.btnSearch);
        mapView = findViewById(R.id.mapView);
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
                // add a new tag here
                mMap.addMarker(new MarkerOptions()
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
