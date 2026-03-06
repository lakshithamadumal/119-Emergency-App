package com.iamlaky.emergency119.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;
import com.iamlaky.emergency119.model.User;

public class UserViewModel extends ViewModel {

    private final MutableLiveData<User> userLiveData = new MutableLiveData<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public LiveData<User> getUser(String uid) {
        if (userLiveData.getValue() == null) {
            loadUserData(uid);
        }
        return userLiveData;
    }

    private void loadUserData(String uid) {
        db.collection("users").document(uid).addSnapshotListener((snapshot, error) -> {
            if (error == null && snapshot != null && snapshot.exists()) {
                User user = snapshot.toObject(User.class);

                if (user != null) {
                    long currentTime = System.currentTimeMillis();
                    if (user.getExpiryDate() != 0 && user.getExpiryDate() < currentTime) {
                        user.setPaymentStatus("Expired");
                    }
                    userLiveData.setValue(user);
                }
            }
        });
    }
}