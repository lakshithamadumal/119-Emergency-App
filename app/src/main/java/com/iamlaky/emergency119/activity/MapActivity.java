package com.iamlaky.emergency119.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;
import com.iamlaky.emergency119.R;

public class MapActivity extends AppCompatActivity {

    private MapView mapView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mapView = findViewById(R.id.mapView);

        mapView.getMapboxMap().loadStyleUri(
                Style.MAPBOX_STREETS,
                style -> {
                    CameraOptions cameraOptions = new CameraOptions.Builder()
                            .center(Point.fromLngLat(79.8612, 6.9271))
                            .zoom(15.0)
                            .build();

                    mapView.getMapboxMap().setCamera(cameraOptions);
                }
        );
        ImageButton btnMapBack = findViewById(R.id.btnMapBack);
        btnMapBack.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });
    }
}
