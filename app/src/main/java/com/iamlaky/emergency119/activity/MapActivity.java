package com.iamlaky.emergency119.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;
import com.iamlaky.emergency119.R;

public class MapActivity extends AppCompatActivity {

    private MapView mapView;

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
    }
}
