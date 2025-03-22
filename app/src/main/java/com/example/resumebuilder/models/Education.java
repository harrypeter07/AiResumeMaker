package com.example.resumebuilder.models;

import java.io.Serializable;

/**
 * Model class for education information
 */
public class Education implements Serializable {
    private String institution;
    private String degree;
    private String fieldOfStudy;
    private String startDate;
    private String endDate;
    private String description;
    private String gpa;
    
    // Default constructor
    public Education() {
    }
    
    // Parameterized constructor
    public Education(String institution, String degree, String fieldOfStudy, 
                    String startDate, String endDate, String description, String gpa) {
        this.institution = institution;
        this.degree = degree;
        this.fieldOfStudy = fieldOfStudy;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        this.gpa = gpa;
    }
    
    // Getters and Setters
    public String getInstitution() {
        return institution;
    }
    
    public void setInstitution(String institution) {
        this.institution = institution;
    }
    
    public String getDegree() {
        return degree;
    }
    
    public void setDegree(String degree) {
        this.degree = degree;
    }
    
    public String getFieldOfStudy() {
        return fieldOfStudy;
    }
    
    public void setFieldOfStudy(String fieldOfStudy) {
        this.fieldOfStudy = fieldOfStudy;
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
    
    public String getGpa() {
        return gpa;
    }
    
    public void setGpa(String gpa) {
        this.gpa = gpa;
    }
    
    @Override
    public String toString() {
        return degree + " in " + fieldOfStudy + " at " + institution + " (" + startDate + " - " + endDate + ")";
    }
}