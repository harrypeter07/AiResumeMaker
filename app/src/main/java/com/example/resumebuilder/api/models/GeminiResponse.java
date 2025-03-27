// app/src/main/java/com/example/resumebuilder/api/models/GeminiResponse.java
package com.example.resumebuilder.api.models;

import com.google.gson.annotations.SerializedName;

public class GeminiResponse {
    @SerializedName("text")
    private String text;

    @SerializedName("error")
    private String error;

    public String getText() {
        return text;
    }

    public String getError() {
        return error;
    }
}