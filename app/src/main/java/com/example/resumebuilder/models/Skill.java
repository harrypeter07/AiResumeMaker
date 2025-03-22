package com.example.resumebuilder.models;

import java.io.Serializable;

/**
 * Model class for skill information
 */
public class Skill implements Serializable {
    private String name;
    private int proficiencyLevel; // 1-5 (1 = Beginner, 5 = Expert)
    private String category; // Technical, Soft Skills, Languages, etc.
    
    // Default constructor
    public Skill() {
        this.proficiencyLevel = 3; // Default is intermediate
    }
    
    // Parameterized constructor
    public Skill(String name, int proficiencyLevel, String category) {
        this.name = name;
        this.proficiencyLevel = proficiencyLevel;
        this.category = category;
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public int getProficiencyLevel() {
        return proficiencyLevel;
    }
    
    public void setProficiencyLevel(int proficiencyLevel) {
        // Ensure value is between 1-5
        if (proficiencyLevel < 1) {
            this.proficiencyLevel = 1;
        } else if (proficiencyLevel > 5) {
            this.proficiencyLevel = 5;
        } else {
            this.proficiencyLevel = proficiencyLevel;
        }
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getProficiencyText() {
        switch (proficiencyLevel) {
            case 1:
                return "Beginner";
            case 2:
                return "Basic";
            case 3:
                return "Intermediate";
            case 4:
                return "Advanced";
            case 5:
                return "Expert";
            default:
                return "Intermediate";
        }
    }
    
    @Override
    public String toString() {
        return name + " (" + getProficiencyText() + ")";
    }
}