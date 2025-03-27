package com.example.resumebuilder.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Model class for complete resume data
 */
public class ResumeData implements Serializable {
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private String website;
    private String linkedin;
    private String summary;
    private List<Education> educationList;
    private List<WorkExperience> workExperienceList;
    private List<Skill> skillsList;
    private int selectedTemplateId; // 0, 1, or 2 for the different templates
    
    // Default constructor
    public ResumeData() {
        this.educationList = new ArrayList<>();
        this.workExperienceList = new ArrayList<>();
        this.skillsList = new ArrayList<>();
        this.selectedTemplateId = 0; // Default to modern template
    }
    
    // Getters and Setters
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getWebsite() {
        return website;
    }
    
    public void setWebsite(String website) {
        this.website = website;
    }
    
    public String getLinkedin() {
        return linkedin;
    }
    
    public void setLinkedin(String linkedin) {
        this.linkedin = linkedin;
    }
    
    public String getSummary() {
        return summary;
    }
    
    public void setSummary(String summary) {
        this.summary = summary;
    }
    
    public List<Education> getEducationList() {
        return educationList;
    }
    
    public void setEducationList(List<Education> educationList) {
        this.educationList = educationList;
    }
    
    public void addEducation(Education education) {
        this.educationList.add(education);
    }
    
    public List<WorkExperience> getWorkExperienceList() {
        return workExperienceList;
    }
    
    public void setWorkExperienceList(List<WorkExperience> workExperienceList) {
        this.workExperienceList = workExperienceList;
    }
    
    public void addWorkExperience(WorkExperience workExperience) {
        this.workExperienceList.add(workExperience);
    }
    
    public List<Skill> getSkillsList() {
        return skillsList;
    }
    
    public void setSkillsList(List<Skill> skillsList) {
        this.skillsList = skillsList;
    }
    
    public void addSkill(Skill skill) {
        this.skillsList.add(skill);
    }
    
    public int getSelectedTemplateId() {
        return selectedTemplateId;
    }
    
    public void setSelectedTemplateId(int selectedTemplateId) {
        this.selectedTemplateId = selectedTemplateId;
    }

    public String getJobRole() {
        return "job role";
    }
}