package com.iamlaky.emergency119.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private String uid;
    private String name;
    private String email;
    private String profilePicUrl;

    // Profile Details
    private String phoneNumber;
    private String address;
    private String nicNumber;
    private String emergencyNick1;
    private String emergencyContact1;
    private String emergencyNick2;
    private String emergencyContact2;
    private int totalReports;

    // Subscription Handling
    private String paymentStatus;
    private long expiryDate;

    // Medical Information
    private String bloodGroup;
    private String allergies;
    private String medicalConditions;
    private String currentMedications;

    // Notification
    private String fcmToken;
}