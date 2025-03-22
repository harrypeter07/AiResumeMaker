package com.example.resumebuilder.models;

import java.io.Serializable;

/**
 * Model class for work experience information
 */
public class WorkExperience implements Serializable {
    private String company;
    private String position;
    private String location;
    private String startDate;
    private String endDate;
    private String description;
    private boolean currentJob;
    
    // Default constructor
    public WorkExperience() {
        this.currentJob = false;
    }
    
    // Parameterized constructor
    public WorkExperience(String company, String position, String location, 
                         String startDate, String endDate, String description, boolean currentJob) {
        this.company = company;
        this.position = position;
        this.location = location;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        this.currentJob = currentJob;
    }
    
    // Getters and Setters
    public String getCompany() {
        return company;
    }
    
    public void setCompany(String company) {
        this.company = company;
    }
    
    public String getPosition() {
        return position;
    }
    
    public void setPosition(String position) {
        this.position = position;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public String getStartDate() {
        return startDate;
    }
    
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
    
    public String getEndDate() {
        return endDate;
    }
    
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public boolean isCurrentJob() {
        return currentJob;
    }
    
    public void setCurrentJob(boolean currentJob) {
        this.currentJob = currentJob;
    }
    
    @Override
    public String toString() {
        return position + " at " + company + " (" + startDate + " - " + (currentJob ? "Present" : endDate) + ")";
    }
}