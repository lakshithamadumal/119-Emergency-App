package com.iamlaky.emergency119.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmergencyReport {
    private String reportId;
    private String userId;
    private long timestamp;
}