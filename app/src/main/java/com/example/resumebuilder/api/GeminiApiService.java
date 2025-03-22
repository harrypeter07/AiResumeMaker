package com.example.resumebuilder.api;

import com.example.resumebuilder.api.models.GeminiResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

import java.util.Map;

/**
 * Interface for the Gemini API service
 */
public interface GeminiApiService {
    
    @Headers({
        "Content-Type: application/json"
    })
    @POST("v1beta/models/gemini-pro:generateContent")
    Call<GeminiResponse> generateContent(@Body Map<String, Object> requestBody);
    
    // Method to generate improved summary
    @Headers({
        "Content-Type: application/json"
    })
    @POST("v1beta/models/gemini-pro:generateContent")
    Call<GeminiResponse> generateSummary(@Body Map<String, Object> requestBody);
    
    // Method to enhance job descriptions
    @Headers({
        "Content-Type: application/json"
    })
    @POST("v1beta/models/gemini-pro:generateContent")
    Call<GeminiResponse> enhanceJobDescription(@Body Map<String, Object> requestBody);
    
    // Method to suggest skills based on experience
    @Headers({
        "Content-Type: application/json"
    })
    @POST("v1beta/models/gemini-pro:generateContent")
    Call<GeminiResponse> suggestSkills(@Body Map<String, Object> requestBody);
}