package com.iamlaky.emergency119.viewmodel;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.iamlaky.emergency119.model.Report;
import java.util.List;

public class ReportViewModel extends ViewModel {

    private final MutableLiveData<Integer> _allReportsCount = new MutableLiveData<>(0);
    public LiveData<Integer> allReportsCount = _allReportsCount;

    private final MutableLiveData<List<Report>> _myReports = new MutableLiveData<>();
    public LiveData<List<Report>> myReports = _myReports;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ListenerRegistration countListener;
    private ListenerRegistration myReportsListener;

    public void fetchAllReportsCount() {
        if (countListener != null) return;
        countListener = db.collection("reports").addSnapshotListener((value, error) -> {
            if (error != null) return;
            if (value != null) {
                _allReportsCount.setValue(value.size());
            }
        });
    }

    public void fetchMyReports() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        if (myReportsListener != null) {
            myReportsListener.remove();
        }

        myReportsListener = db.collection("reports")
                .whereEqualTo("uid", uid)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("FIRESTORE", "Listen failed.", error);
                        return;
                    }
                    if (value != null) {
                        _myReports.setValue(value.toObjects(Report.class));
                    }
                });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (countListener != null) countListener.remove();
        if (myReportsListener != null) myReportsListener.remove();
    }
}