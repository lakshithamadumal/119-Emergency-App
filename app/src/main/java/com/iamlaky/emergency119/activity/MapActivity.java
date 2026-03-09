package com.iamlaky.emergency119.activity;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;
import com.iamlaky.emergency119.R;
import com.iamlaky.emergency119.databinding.ActivityMapBinding;
import com.mapbox.android.gestures.MoveGestureDetector;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
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

    private ActivityMapBinding binding;
    private Point currentPoint;
    private static final int REQUEST_CHECK_SETTINGS = 1001;

    private final ActivityResultLauncher<String[]> locationPermissionRequest =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                Boolean fineLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                if (fineLocationGranted != null && fineLocationGranted) {
                    enableGPSAndSetupMap();
                } else {
                    Toast.makeText(this, "Location permission required", Toast.LENGTH_SHORT).show();
                    goToHome();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        checkPermissions();

        binding.btnMapBack.setOnClickListener(v -> goToHome());

        binding.btnCurrentLocation.setOnClickListener(v -> {
            if (currentPoint != null) zoomToLocation(currentPoint);
        });

        binding.btnSendReport.setOnClickListener(v -> {
            if (currentPoint != null) {
//                String address = getAddressFromLatLng(currentPoint.latitude(), currentPoint.longitude());
//                Log.d("REPORT_DATA", "Lat: " + currentPoint.latitude() + ", Lng: " + currentPoint.longitude());
//                Log.d("REPORT_DATA", "Address: " + address);
//                Toast.makeText(this, "Emergency Reported at: " + address, Toast.LENGTH_LONG).show();
                binding.mapView.onStop();
                Intent intent = new Intent(MapActivity.this, SendReportActivity.class);
                startActivity(intent);
            }
        });
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationPermissionRequest.launch(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION});
        } else {
            enableGPSAndSetupMap();
        }
    }

    private void enableGPSAndSetupMap() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, locationSettingsResponse -> setupMap());

        task.addOnFailureListener(this, e -> {
            if (e instanceof ResolvableApiException) {
                try {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    resolvable.startResolutionForResult(MapActivity.this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException sendEx) {
                    sendEx.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == RESULT_OK) {
                setupMap();
            } else {
                Toast.makeText(this, "GPS must be on to report", Toast.LENGTH_SHORT).show();
                goToHome();
            }
        }
    }

    private void setupMap() {
        binding.mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS, style -> {
            enableLocationComponent();
            setupMapGestures();
        });
    }

    private void enableLocationComponent() {
        LocationComponentPlugin locationComponentPlugin = LocationComponentUtils.getLocationComponent(binding.mapView);
        locationComponentPlugin.setEnabled(true);
        locationComponentPlugin.setLocationPuck(new LocationPuck2D());
        locationComponentPlugin.addOnIndicatorPositionChangedListener(point -> {
            currentPoint = point;
            zoomToLocation(point);
        });
    }

    private void setupMapGestures() {
        GesturesUtils.getGestures(binding.mapView).addOnMoveListener(new OnMoveListener() {
            @Override
            public void onMoveBegin(@NonNull MoveGestureDetector detector) {}
            @Override
            public boolean onMove(@NonNull MoveGestureDetector detector) { return false; }
            @Override
            public void onMoveEnd(@NonNull MoveGestureDetector detector) {
                currentPoint = binding.mapView.getMapboxMap().getCameraState().getCenter();
            }
        });
    }

    private String getAddressFromLatLng(double lat, double lng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if (addresses != null && !addresses.isEmpty()) return addresses.get(0).getAddressLine(0);
        } catch (IOException e) { e.printStackTrace(); }
        return "Address Not Found";
    }

    private void zoomToLocation(Point point) {
        binding.mapView.getMapboxMap().setCamera(new CameraOptions.Builder().center(point).zoom(16.0).build());
    }

    private void goToHome() {
        finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
}