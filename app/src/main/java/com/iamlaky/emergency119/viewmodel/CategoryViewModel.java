package com.iamlaky.emergency119.viewmodel;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.iamlaky.emergency119.model.Category;
import java.util.ArrayList;
import java.util.List;

public class CategoryViewModel extends ViewModel {

    private final MutableLiveData<List<Category>> _categories = new MutableLiveData<>();
    public LiveData<List<Category>> categories = _categories;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void fetchCategories() {
        db.collection("categories").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Category> list = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Category category = document.toObject(Category.class);
                    list.add(category);
                }
                _categories.setValue(list);
            } else {
                Log.e("VIEWMODEL_ERROR", "Error fetching categories", task.getException());
            }
        });
    }
}