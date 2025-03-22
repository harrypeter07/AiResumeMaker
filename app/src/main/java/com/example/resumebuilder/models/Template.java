package com.example.resumebuilder.models;

public class Template {
    
    private String id;
    private String name;
    private String description;
    private String htmlPath;
    private String cssPath;
    private String previewPath;
    
    public Template(String id, String name, String description, String htmlPath, String cssPath, String previewPath) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.htmlPath = htmlPath;
        this.cssPath = cssPath;
        this.previewPath = previewPath;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getHtmlPath() {
        return htmlPath;
    }
    
    public void setHtmlPath(String htmlPath) {
        this.htmlPath = htmlPath;
    }
    
    public String getCssPath() {
        return cssPath;
    }
    
    public void setCssPath(String cssPath) {
        this.cssPath = cssPath;
    }
    
    public String getPreviewPath() {
        return previewPath;
    }
    
    public void setPreviewPath(String previewPath) {
        this.previewPath = previewPath;
    }
}
