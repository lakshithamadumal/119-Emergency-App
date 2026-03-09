package com.iamlaky.emergency119.model;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.PropertyName;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgnoreExtraProperties
public class Report {
    private String reportId;
    private String uid;
    private String categoryId;
    private String categoryName;
    private double latitude;
    private double longitude;
    private String address;
    private String severity;
    private String message;
    private String status;

    @ServerTimestamp
    private Date timestamp;

    private List<String> imageUrls;
}