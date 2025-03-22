package com.example.resumebuilder.api.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Model class for Gemini API requests with builder pattern
 */
public class GeminiRequest {

    @SerializedName("contents")
    private List<Content> contents;

    // Simple constructor for basic text prompts
    public GeminiRequest(String prompt) {
        this.contents = new ArrayList<>();
        Content content = new Content();
        content.role = "user";

        // Create text part
        Part textPart = new Part();
        textPart.text = prompt;

        // Add text part to parts list
        content.parts = new ArrayList<>();
        content.parts.add(textPart);

        // Add content to contents list
        this.contents.add(content);
    }

    // Empty constructor for builder usage
    private GeminiRequest() {
        this.contents = new ArrayList<>();
    }

    public List<Content> getContents() {
        return contents;
    }

    /**
     * Convert the request object to a Map for Retrofit
     */
    public Map<String, Object> toMap() {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("contents", contents);
        return requestMap;
    }

    /**
     * Builder class for creating complex Gemini requests
     */
    public static class Builder {
        private final GeminiRequest request;
        private Content currentContent;

        public Builder() {
            request = new GeminiRequest();
            startNewContent("user");
        }

        public Builder startNewContent(String role) {
            currentContent = new Content();
            currentContent.role = role;
            currentContent.parts = new ArrayList<>();
            request.contents.add(currentContent);
            return this;
        }

        public Builder addText(String text) {
            Part part = new Part();
            part.text = text;
            currentContent.parts.add(part);
            return this;
        }

        public GeminiRequest build() {
            return request;
        }
    }

    // Inner classes for Gemini API request format
    public static class Content {
        @SerializedName("parts")
        public List<Part> parts;

        @SerializedName("role")
        public String role;
    }

    public static class Part {
        @SerializedName("text")
        public String text;
    }
}