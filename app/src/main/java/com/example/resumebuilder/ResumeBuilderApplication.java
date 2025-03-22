package com.example.resumebuilder;

import android.app.Application;

import com.example.resumebuilder.api.ApiClient;

/**
 * Application class for global initialization
 */
public class ResumeBuilderApplication extends Application {
    
    // Replace with your Gemini API key
    private static final String GEMINI_API_KEY = "AIzaSyAXA2ksQmZH6cqCztBg2CHMSZTSDPb4uJc";
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        // Initialize API key
        ApiClient.setApiKey(GEMINI_API_KEY);
        
        // Other application-wide initializations can go here
    }
}