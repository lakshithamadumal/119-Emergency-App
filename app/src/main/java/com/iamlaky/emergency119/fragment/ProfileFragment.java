package com.iamlaky.emergency119.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.iamlaky.emergency119.R;
import com.iamlaky.emergency119.activity.MedicalInfoActivity;
import com.iamlaky.emergency119.databinding.FragmentProfileBinding;
import com.iamlaky.emergency119.model.User;
import com.iamlaky.emergency119.viewmodel.UserViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private UserViewModel userViewModel;
    private FirebaseAuth firebaseAuth;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        String uid = firebaseAuth.getUid();
        if (uid != null) {
            userViewModel.getUser(uid).observe(getViewLifecycleOwner(), user -> {
                if (user != null) {
                    updateUI(user);
                }
            });
        }

        binding.btnMedicalInfo.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), MedicalInfoActivity.class));
        });

        binding.btnUpdate.setOnClickListener(v -> {
            updateProfileData();
        });


        binding.ivProfileImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        ///  Gallery
        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                Uri imageUri = result.getData().getData();
                if (imageUri != null) {
                    uploadImageToImgBB(imageUri);
                }
            }
        });

        return binding.getRoot();
    }

    private void updateUI(User user) {
        binding.tvUserName.setText(user.getName() != null ? user.getName() : "Add your name");
        binding.tvUserEmail.setText(user.getEmail() != null ? user.getEmail() : "No email linked");

        if (user.getProfilePicUrl() != null && !user.getProfilePicUrl().isEmpty()) {
            Glide.with(this).load(user.getProfilePicUrl()).placeholder(R.drawable.profile_placeholder).into(binding.ivProfileImage);
        }

        setupInput(binding.inputFullName.getRoot(), "Full name", user.getName(), "Enter your full name");
        setupInput(binding.inputPhone.getRoot(), "Phone Number", user.getPhoneNumber(), "e.g. +94771234567");
        setupInput(binding.inputAddress.getRoot(), "Address", user.getAddress(), "Enter your home address");
        setupInput(binding.inputNIC.getRoot(), "NIC / Passport", user.getNicNumber(), "Enter NIC or Passport number");

        binding.etNick1.setText(user.getEmergencyNick1());
        binding.etEmergencyNum1.setText(user.getEmergencyContact1());
        binding.etNick2.setText(user.getEmergencyNick2());
        binding.etEmergencyNum2.setText(user.getEmergencyContact2());

        int count = user.getTotalReports();
        binding.tvTotalReports.setText(count > 0 ? count + "+" : "0");
    }

    private void updateProfileData() {
        String uid = firebaseAuth.getUid();
        if (uid == null) return;

        binding.btnUpdate.setEnabled(false);
        binding.btnUpdate.setText("Updating...");

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", binding.inputFullName.etInputValue.getText().toString().trim());
        updates.put("phoneNumber", binding.inputPhone.etInputValue.getText().toString().trim());
        updates.put("address", binding.inputAddress.etInputValue.getText().toString().trim());
        updates.put("nicNumber", binding.inputNIC.etInputValue.getText().toString().trim());
        updates.put("emergencyNick1", binding.etNick1.getText().toString().trim());
        updates.put("emergencyContact1", binding.etEmergencyNum1.getText().toString().trim());
        updates.put("emergencyNick2", binding.etNick2.getText().toString().trim());
        updates.put("emergencyContact2", binding.etEmergencyNum2.getText().toString().trim());

        FirebaseFirestore.getInstance().collection("users").document(uid).update(updates).addOnSuccessListener(unused -> {
            binding.btnUpdate.setEnabled(true);
            binding.btnUpdate.setText("UPDATE");

            clearFormFocus();

            Toast.makeText(getContext(), "Profile updated!", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            binding.btnUpdate.setEnabled(true);
            binding.btnUpdate.setText("UPDATE");
            Toast.makeText(getContext(), "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void clearFormFocus() {
        binding.inputFullName.etInputValue.clearFocus();
        binding.inputPhone.etInputValue.clearFocus();
        binding.inputAddress.etInputValue.clearFocus();
        binding.inputNIC.etInputValue.clearFocus();
        binding.etNick1.clearFocus();
        binding.etEmergencyNum1.clearFocus();
        binding.etNick2.clearFocus();
        binding.etEmergencyNum2.clearFocus();

        if (getActivity() != null && getActivity().getCurrentFocus() != null) {
            android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getActivity().getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void uploadImageToImgBB(Uri imageUri) {
        binding.btnUpdate.setEnabled(false);
        Toast.makeText(getContext(), "Uploading image...", Toast.LENGTH_SHORT).show();

        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(imageUri);
            byte[] bytes = getBytes(inputStream);

            RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("key", "a3c8d35257779a06bdc4b306d992a25a").addFormDataPart("image", "profile.jpg", RequestBody.create(bytes, MediaType.parse("image/*"))).build();

            Request request = new Request.Builder().url("https://api.imgbb.com/1/upload").post(requestBody).build();

            OkHttpClient client = new OkHttpClient();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    getActivity().runOnUiThread(() -> {
                        binding.btnUpdate.setEnabled(true);
                        Toast.makeText(getContext(), "Upload Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful() && response.body() != null) {
                        String responseData = response.body().string();
                        try {
                            JSONObject jsonObject = new JSONObject(responseData);
                            /// Get Direct Link
                            String imageUrl = jsonObject.getJSONObject("data").getString("url");

                            saveImageUrlToFirestore(imageUrl);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    private void saveImageUrlToFirestore(String url) {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid != null) {
            FirebaseFirestore.getInstance().collection("users").document(uid).update("profilePicUrl", url).addOnSuccessListener(unused -> {
                getActivity().runOnUiThread(() -> {
                    binding.btnUpdate.setEnabled(true);
                    Toast.makeText(getContext(), "Profile Picture Updated!", Toast.LENGTH_SHORT).show();
                });
            });
        }
    }

    private void setupInput(View inputView, String label, String value, String hint) {
        if (inputView != null) {
            TextView tvLabel = inputView.findViewById(R.id.tvInputLabel);
            EditText etValue = inputView.findViewById(R.id.etInputValue);
            if (tvLabel != null) tvLabel.setText(label);
            if (etValue != null) {
                etValue.setHint(hint);
                etValue.setText(value != null ? value : "");
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}