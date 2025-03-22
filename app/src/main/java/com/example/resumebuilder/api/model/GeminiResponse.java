package com.example.resumebuilder.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Model class for Gemini API responses
 */
public class GeminiResponse {
    @SerializedName("candidates")
    private List<Candidate> candidates;

    @SerializedName("promptFeedback")
    private PromptFeedback promptFeedback;

    public List<Candidate> getCandidates() {
        return candidates;
    }

    public PromptFeedback getPromptFeedback() {
        return promptFeedback;
    }

    public static class Candidate {
        @SerializedName("content")
        private Content content;

        @SerializedName("finishReason")
        private String finishReason;

        @SerializedName("index")
        private int index;

        public Content getContent() {
            return content;
        }

        public String getFinishReason() {
            return finishReason;
        }

        public int getIndex() {
            return index;
        }
    }

    public static class Content {
        @SerializedName("parts")
        private List<Part> parts;

        @SerializedName("role")
        private String role;

        public List<Part> getParts() {
            return parts;
        }

        public String getRole() {
            return role;
        }
    }

    public static class Part {
        @SerializedName("text")
        private String text;

        public String getText() {
            return text;
        }
    }

    public static class PromptFeedback {
        @SerializedName("safetyRatings")
        private List<SafetyRating> safetyRatings;

        public List<SafetyRating> getSafetyRatings() {
            return safetyRatings;
        }
    }

    public static class SafetyRating {
        @SerializedName("category")
        private String category;

        @SerializedName("probability")
        private String probability;

        public String getCategory() {
            return category;
        }

        public String getProbability() {
            return probability;
        }
    }
}