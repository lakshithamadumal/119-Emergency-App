package com.iamlaky.emergency119.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
    private String id;
    private String userId;
    private String title;
    private String type;
    private long timestamp;
}