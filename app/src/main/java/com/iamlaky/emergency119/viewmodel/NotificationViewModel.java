package com.iamlaky.emergency119.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.iamlaky.emergency119.model.Notification;

import java.util.List;

public class NotificationViewModel extends ViewModel {

    private final MutableLiveData<List<Notification>> _notifications = new MutableLiveData<>();
    public LiveData<List<Notification>> notifications = _notifications;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    public void fetchNotifications() {
        db.collection("notifications")
                .whereEqualTo("userId", currentUserId)
//                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("FIRESTORE_ERROR", error.getMessage());
                        return;
                    }
                    if (value != null) {
                        List<Notification> list = value.toObjects(Notification.class);
                        Log.d("FIRESTORE_DATA", "Size: " + list.size());
                        _notifications.setValue(list);
                    }
                });
    }
}