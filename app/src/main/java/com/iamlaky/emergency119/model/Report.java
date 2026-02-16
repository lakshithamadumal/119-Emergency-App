package com.iamlaky.emergency119.model;

public class Report {
    private String title;
    private String location;
    private String dateTime;
    private String status;
    private String category;

    public Report(String title, String location, String dateTime, String status, String category) {
        this.title = title;
        this.location = location;
        this.dateTime = dateTime;
        this.status = status;
        this.category = category;
    }

    public String getTitle() { return title; }
    public String getLocation() { return location; }
    public String getDateTime() { return dateTime; }
    public String getStatus() { return status; }
    public String getCategory() { return category; }
}