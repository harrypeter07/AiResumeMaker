package com.example.resumebuilder.api;

import android.util.Log;

import com.example.resumebuilder.api.models.GeminiResponse;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Singleton class for creating and managing API clients
 */
public class ApiClient {
    private static final String TAG = "ApiClient";
    private static final String BASE_URL = "https://generativelanguage.googleapis.com/";
    private static final int TIMEOUT_SECONDS = 30;
    private static Retrofit retrofit = null;
    private static String apiKey = null;
    
    /**
     * Get the Retrofit instance with the API key in the header
     * @return Retrofit instance
     */
    public static Retrofit getClient() {
        if (retrofit == null || apiKey == null) {
            if (apiKey == null) {
                throw new IllegalStateException("API key must be set before creating client");
            }
            
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            
            // Add logging interceptor for debug builds
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> 
                Log.d(TAG, message));
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClient.addInterceptor(loggingInterceptor);
            
            // Add API key as query parameter to each request
            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();
                    
                    try {
                        // Add API key as query parameter
                        HttpUrl url = original.url().newBuilder()
                                .addQueryParameter("key", apiKey)
                                .build();
                        
                        Request.Builder requestBuilder = original.newBuilder()
                                .url(url);
                        
                        Request request = requestBuilder.build();
                        return chain.proceed(request);
                    } catch (Exception e) {
                        Log.e(TAG, "Error during API request: " + e.getMessage());
                        throw e;
                    }
                }
            });
            
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();
        }
        
        return retrofit;
    }
    
    /**
     * Set the API key for the Gemini API
     * @param key The API key
     * @throws IllegalArgumentException if key is null or empty
     */
    public static void setApiKey(String key) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("API key cannot be null or empty");
        }
        apiKey = key;
        // Reset retrofit instance to create a new one with the updated API key
        retrofit = null;
    }
    
    /**
     * Create an instance of the GeminiApiService
     * @return GeminiApiService instance
     * @throws IllegalStateException if API key is not set
     */
    public static GeminiApiService getGeminiApiService() {
        return getClient().create(GeminiApiService.class);
    }

    public static Call<GeminiResponse> generateContent(String string) {
        return null;
    }
}