package com.iamlaky.emergency119.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.iamlaky.emergency119.R;
import com.iamlaky.emergency119.adapter.CategoryAdapter;
import com.iamlaky.emergency119.adapter.SelectedImageAdapter;
import com.iamlaky.emergency119.databinding.ActivitySendReportBinding;
import com.iamlaky.emergency119.model.Category;
import com.iamlaky.emergency119.model.Report;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SendReportActivity extends BaseActivity {

    private ActivitySendReportBinding binding;
    private CategoryViewModel categoryViewModel;
    private CategoryAdapter categoryAdapter;
    private List<Category> categoryList = new ArrayList<>();

    private List<Uri> selectedImageUris = new ArrayList<>();
    private List<String> uploadedImageUrls = new ArrayList<>();
    private SelectedImageAdapter imageAdapter;

    private double reportLat, reportLng;
    private String reportAddress;
    private static final int PICK_IMAGES_REQUEST = 101;

    @Override
    protected boolean shouldCheckInternet() {
        return true;
    }

    @Override
    protected boolean shouldCheckBattery() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivitySendReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);

        // Map Intents
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
        binding.btnUploadGallery.setOnClickListener(v -> pickImages());
        binding.btnSubmit.setOnClickListener(v -> validateAndStartUpload());
    }

    private void validateAndStartUpload() {
        Category selectedCat = categoryAdapter.getSelectedCategory();
        int selectedId = binding.rgSeverity.getCheckedRadioButtonId();
        RadioButton rb = findViewById(selectedId);
        String severity = (rb != null) ? rb.getText().toString() : "";
        String description = binding.etDescription.getText().toString().trim();

        if (selectedCat == null || severity.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.btnSubmit.setEnabled(false);
        uploadedImageUrls.clear();

        if (!selectedImageUris.isEmpty()) {
            uploadImagesToImgBB(0);
        } else {
            saveReportToFirestore();
        }
    }

    private void uploadImagesToImgBB(int index) {
        if (index >= selectedImageUris.size()) {
            saveReportToFirestore();
            return;
        }

        runOnUiThread(() -> binding.btnSubmit.setText("Uploading Image " + (index + 1) + "/" + selectedImageUris.size()));

        try {
            InputStream inputStream = getContentResolver().openInputStream(selectedImageUris.get(index));
            byte[] bytes = getBytes(inputStream);

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("key", "a3c8d35257779a06bdc4b306d992a25a")
                    .addFormDataPart("image", "emergency_img_" + index + ".jpg",
                            RequestBody.create(bytes, MediaType.parse("image/*")))
                    .build();

            Request request = new Request.Builder().url("https://api.imgbb.com/1/upload").post(requestBody).build();

            new OkHttpClient().newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    handleFailure(e.getMessage());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            uploadedImageUrls.add(jsonObject.getJSONObject("data").getString("url"));
                            uploadImagesToImgBB(index + 1);
                        } catch (Exception e) { handleFailure(e.getMessage()); }
                    } else { handleFailure("Upload failed at server"); }
                }
            });
        } catch (Exception e) { handleFailure(e.getMessage()); }
    }

    private void saveReportToFirestore() {
        runOnUiThread(() -> binding.btnSubmit.setText("Finalizing Report..."));

        String customId = "REP_" + new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date())
                + "_" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();

        Category cat = categoryAdapter.getSelectedCategory();
        int selectedId = binding.rgSeverity.getCheckedRadioButtonId();
        String severity = ((RadioButton) findViewById(selectedId)).getText().toString();

        Report report = new Report(
                customId, FirebaseAuth.getInstance().getUid(),
                cat.getCategoryId(), cat.getName(),
                reportLat, reportLng, reportAddress,
                severity, binding.etDescription.getText().toString().trim(),
                "Received", new Date(),null,null,null, uploadedImageUrls
        );

        FirebaseFirestore.getInstance().collection("reports").document(customId)
                .set(report)
                .addOnSuccessListener(aVoid -> {
                    incrementUserReportCount();

                    String uid = FirebaseAuth.getInstance().getUid();
                    String msg = "Your emergency report was sent successfully.\n"
                            + "Report ID: " + customId;

                    addNotificationToHistory(uid, "Report Received", msg);
                    showLocalNotification("Report Received", msg, customId);

                    Intent intent = new Intent(this, ReportSuccessActivity.class);
                    intent.putExtra("REPORT_ID", customId);

                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> handleFailure(e.getMessage()));
    }

    private void incrementUserReportCount() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        java.util.Map<String, Object> updateData = new java.util.HashMap<>();
        updateData.put("totalReports", com.google.firebase.firestore.FieldValue.increment(1));

        FirebaseFirestore.getInstance().collection("users").document(uid)
                .set(updateData, com.google.firebase.firestore.SetOptions.merge())
                .addOnSuccessListener(unused -> {
                    Log.d("DB_SUCCESS", "User report count incremented");
                })
                .addOnFailureListener(e -> {
                    Log.e("DB_ERROR", "Failed to increment count: " + e.getMessage());
                });
    }

    private void handleFailure(String error) {
        runOnUiThread(() -> {
            binding.btnSubmit.setEnabled(true);
            binding.btnSubmit.setText("SUBMIT REPORT");
            Toast.makeText(this, "Error: " + error, Toast.LENGTH_SHORT).show();
        });
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inputStream.read(buffer)) != -1) byteBuffer.write(buffer, 0, len);
        return byteBuffer.toByteArray();
    }

    private void setupImageRecyclerView() {
        imageAdapter = new SelectedImageAdapter(selectedImageUris);
        binding.rvImagePreview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.rvImagePreview.setAdapter(imageAdapter);
    }

    private void pickImages() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Images"), PICK_IMAGES_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGES_REQUEST && resultCode == RESULT_OK && data != null) {
            if (data.getClipData() != null) {
                for (int i = 0; i < data.getClipData().getItemCount(); i++)
                    selectedImageUris.add(data.getClipData().getItemAt(i).getUri());
            } else if (data.getData() != null) selectedImageUris.add(data.getData());
            imageAdapter.notifyDataSetChanged();
        }
    }

    private void setupCategoryRecyclerView() {
        binding.rvCategories.setLayoutManager(new GridLayoutManager(this, 3));
        categoryAdapter = new CategoryAdapter(categoryList);
        binding.rvCategories.setAdapter(categoryAdapter);
    }

    private void observeViewModel() {
        categoryViewModel.categories.observe(this, categories -> {
            categoryList.clear();
            categoryList.addAll(categories);
            categoryAdapter.notifyDataSetChanged();
        });
        categoryViewModel.fetchCategories();
    }

    private void setupPreviewMap() {
        binding.mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS, style -> {
            Point point = Point.fromLngLat(reportLng, reportLat);
            binding.mapView.getMapboxMap().setCamera(new CameraOptions.Builder().center(point).zoom(15.0).build());
            addMarkerToMap(point);
        });
    }

    private void addMarkerToMap(Point point) {
        AnnotationPlugin api = AnnotationPluginImplKt.getAnnotations(binding.mapView);
        PointAnnotationManager manager = PointAnnotationManagerKt.createPointAnnotationManager(api, new AnnotationConfig());
        PointAnnotationOptions opts = new PointAnnotationOptions().withPoint(point)
                .withIconImage(android.graphics.BitmapFactory.decodeResource(getResources(), R.drawable.ic_location_preview_pin));
        manager.create(opts);
    }

    private void addNotificationToHistory(String userId, String title, String description) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String notificationId = db.collection("notifications").document().getId();

        com.iamlaky.emergency119.model.Notification historyNotif = new com.iamlaky.emergency119.model.Notification(
                notificationId,
                userId,
                title,
                description,
                "REPORT",
                System.currentTimeMillis()
        );

        db.collection("notifications").document(notificationId).set(historyNotif);
    }

    private void showLocalNotification(String title, String message, String reportId) {
        String channelId = "emergency_alerts";
        android.app.NotificationManager notificationManager = (android.app.NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            android.app.NotificationChannel channel = new android.app.NotificationChannel(
                    channelId,
                    "Emergency Alerts",
                    android.app.NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Emergency Notifications");
            notificationManager.createNotificationChannel(channel);
        }

        android.content.Intent intent = new android.content.Intent(this, ViewReportActivity.class);
        intent.putExtra("REPORT_ID", reportId);
        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP | android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP);

        android.app.PendingIntent pendingIntent = android.app.PendingIntent.getActivity(
                this,
                (int) System.currentTimeMillis(),
                intent,
                android.app.PendingIntent.FLAG_ONE_SHOT | android.app.PendingIntent.FLAG_IMMUTABLE
        );
        androidx.core.app.NotificationCompat.Builder builder = new androidx.core.app.NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setColor(androidx.core.content.ContextCompat.getColor(this, R.color.mainRed))
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
}