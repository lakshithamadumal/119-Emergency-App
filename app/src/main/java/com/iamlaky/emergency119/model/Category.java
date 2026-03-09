package com.iamlaky.emergency119.model;

import com.google.firebase.firestore.PropertyName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Category {
    private String categoryId;
    private String name;
    private String imageUrl;

    @PropertyName("isSelected")
    private boolean isSelected;

    @PropertyName("isSelected")
    public boolean isSelected() {
        return isSelected;
    }

    @PropertyName("isSelected")
    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}