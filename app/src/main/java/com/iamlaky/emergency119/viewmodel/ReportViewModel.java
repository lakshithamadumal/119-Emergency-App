package com.iamlaky.emergency119.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

public class ReportViewModel extends ViewModel {

    private final MutableLiveData<Integer> _allReportsCount = new MutableLiveData<>(0);
    public LiveData<Integer> allReportsCount = _allReportsCount;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ListenerRegistration countListener;

    public void fetchAllReportsCount() {
        countListener = db.collection("reports").addSnapshotListener((value, error) -> {
            if (error != null) return;
            if (value != null) {
                _allReportsCount.setValue(value.size());
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (countListener != null) countListener.remove();
    }
}