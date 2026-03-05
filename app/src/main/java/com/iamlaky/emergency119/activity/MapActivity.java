package com.iamlaky.emergency119.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.iamlaky.emergency119.R;
import com.mapbox.android.gestures.MoveGestureDetector;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;
import com.mapbox.maps.plugin.LocationPuck2D;
import com.mapbox.maps.plugin.gestures.GesturesUtils;
import com.mapbox.maps.plugin.gestures.OnMoveListener;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentPlugin;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentUtils;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapActivity extends AppCompatActivity {

    private MapView mapView;
    private Point currentPoint;

    private final ActivityResultLauncher<String[]> locationPermissionRequest =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                Boolean fineLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                if (fineLocationGranted != null && fineLocationGranted) {
                    setupMap();
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    goToHome();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mapView = findViewById(R.id.mapView);
        ImageButton btnMapBack = findViewById(R.id.btnMapBack);
        FloatingActionButton btnCurrentLocation = findViewById(R.id.btnCurrentLocation);
        AppCompatButton btnSendReport = findViewById(R.id.btnSendReport);

        checkPermissions();

        btnMapBack.setOnClickListener(v -> goToHome());

        btnCurrentLocation.setOnClickListener(v -> {
            if (currentPoint != null) zoomToLocation(currentPoint);
        });

        btnSendReport.setOnClickListener(v -> {
            if (currentPoint != null) {
                String address = getAddressFromLatLng(currentPoint.latitude(), currentPoint.longitude());
                Log.d("REPORT_DATA", "Lat: " + currentPoint.latitude() + ", Lng: " + currentPoint.longitude());
                Log.d("REPORT_DATA", "Address: " + address);
                Toast.makeText(this, "Address: " + address, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationPermissionRequest.launch(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION});
        } else {
            setupMap();
        }
    }

    private void setupMap() {
        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS, style -> {
            enableLocationComponent();
            setupMapGestures();
        });
    }

    private void enableLocationComponent() {
        LocationComponentPlugin locationComponentPlugin = LocationComponentUtils.getLocationComponent(mapView);
        locationComponentPlugin.setEnabled(true);
        locationComponentPlugin.setLocationPuck(new LocationPuck2D());
        locationComponentPlugin.addOnIndicatorPositionChangedListener(point -> {
            currentPoint = point;
            zoomToLocation(point);
        });
    }

    private void setupMapGestures() {
        GesturesUtils.getGestures(mapView).addOnMoveListener(new OnMoveListener() {
            @Override
            public void onMoveBegin(@NonNull MoveGestureDetector detector) {}
            @Override
            public boolean onMove(@NonNull MoveGestureDetector detector) { return false; }
            @Override
            public void onMoveEnd(@NonNull MoveGestureDetector detector) {
                currentPoint = mapView.getMapboxMap().getCameraState().getCenter();
            }
        });
    }

    private String getAddressFromLatLng(double lat, double lng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if (addresses != null && !addresses.isEmpty()) return addresses.get(0).getAddressLine(0);
        } catch (IOException e) { e.printStackTrace(); }
        return "No Address Found";
    }

    private void zoomToLocation(Point point) {
        mapView.getMapboxMap().setCamera(new CameraOptions.Builder().center(point).zoom(16.0).build());
    }

    private void goToHome() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
}