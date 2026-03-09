package com.iamlaky.emergency119.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.iamlaky.emergency119.R;
import com.iamlaky.emergency119.adapter.CategoryAdapter;
import com.iamlaky.emergency119.adapter.SelectedImageAdapter; // මම කලින් දුන්න Adapter එක
import com.iamlaky.emergency119.databinding.ActivitySendReportBinding;
import com.iamlaky.emergency119.model.Category;
import com.iamlaky.emergency119.viewmodel.CategoryViewModel;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.Style;
import com.mapbox.maps.plugin.annotation.AnnotationConfig;
import com.mapbox.maps.plugin.annotation.AnnotationPlugin;
import com.mapbox.maps.plugin.annotation.AnnotationPluginImplKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManagerKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions;

import java.util.ArrayList;
import java.util.List;

public class SendReportActivity extends AppCompatActivity {

    private ActivitySendReportBinding binding;
    private CategoryViewModel categoryViewModel;
    private CategoryAdapter categoryAdapter;
    private List<Category> categoryList = new ArrayList<>();

    private List<Uri> selectedImageUris = new ArrayList<>();
    private SelectedImageAdapter imageAdapter;
    private static final int PICK_IMAGES_REQUEST = 101;

    private double reportLat;
    private double reportLng;
    private String reportAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivitySendReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);

        reportLat = getIntent().getDoubleExtra("LATITUDE", 0.0);
        reportLng = getIntent().getDoubleExtra("LONGITUDE", 0.0);
        reportAddress = getIntent().getStringExtra("ADDRESS");

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupPreviewMap();
        setupCategoryRecyclerView();
        setupImageRecyclerView();

        observeViewModel();

        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnUploadGallery.setOnClickListener(v -> pickImagesFromGallery());

        binding.btnSubmit.setOnClickListener(v -> validateAndSubmit());
    }

    private void setupImageRecyclerView() {
        imageAdapter = new SelectedImageAdapter(selectedImageUris);
        binding.rvImagePreview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.rvImagePreview.setAdapter(imageAdapter);
    }

    private void pickImagesFromGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Emergency Images"), PICK_IMAGES_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGES_REQUEST && resultCode == RESULT_OK && data != null) {
            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    selectedImageUris.add(data.getClipData().getItemAt(i).getUri());
                }
            } else if (data.getData() != null) {
                selectedImageUris.add(data.getData());
            }
            imageAdapter.notifyDataSetChanged();
        }
    }

    private void validateAndSubmit() {
        Category selectedCat = categoryAdapter.getSelectedCategory();

        int selectedId = binding.rgSeverity.getCheckedRadioButtonId();
        RadioButton rb = findViewById(selectedId);
        String severity = (rb != null) ? rb.getText().toString() : "";

        String description = binding.etDescription.getText().toString().trim();

        if (selectedCat == null) {
            Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show();
        } else if (severity.isEmpty()) {
            Toast.makeText(this, "Please select severity level", Toast.LENGTH_SHORT).show();
        } else if (description.isEmpty()) {
            binding.etDescription.setError("Please describe the situation");
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("🚨 REPORT READY\n");
            sb.append("Category: ").append(selectedCat.getName()).append("\n");
            sb.append("Severity: ").append(severity).append("\n");
            sb.append("Location: ").append(reportLat).append(", ").append(reportLng).append("\n");
            if (reportAddress != null) sb.append("Address: ").append(reportAddress).append("\n");
            sb.append("Images: ").append(selectedImageUris.size()).append(" selected");

            Toast.makeText(this, sb.toString(), Toast.LENGTH_LONG).show();

        }
    }

    private void observeViewModel() {
        categoryViewModel.categories.observe(this, categories -> {
            if (categories != null) {
                categoryList.clear();
                categoryList.addAll(categories);
                categoryAdapter.notifyDataSetChanged();
            }
        });
        categoryViewModel.fetchCategories();
    }

    private void setupCategoryRecyclerView() {
        binding.rvCategories.setLayoutManager(new GridLayoutManager(this, 3));
        categoryAdapter = new CategoryAdapter(categoryList);
        binding.rvCategories.setAdapter(categoryAdapter);
    }

    private void setupPreviewMap() {
        binding.mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS, style -> {
            Point point = Point.fromLngLat(reportLng, reportLat);
            binding.mapView.getMapboxMap().setCamera(new CameraOptions.Builder().center(point).zoom(15.0).build());
            addMarkerToMap(point);
        });
    }

    private void addMarkerToMap(Point point) {
        AnnotationPlugin annotationApi = AnnotationPluginImplKt.getAnnotations(binding.mapView);
        PointAnnotationManager pointAnnotationManager = PointAnnotationManagerKt.createPointAnnotationManager(annotationApi, new AnnotationConfig());
        PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions()
                .withPoint(point)
                .withIconImage(android.graphics.BitmapFactory.decodeResource(getResources(), R.drawable.ic_location_preview_pin));
        pointAnnotationManager.create(pointAnnotationOptions);
    }
}