package com.example.resumebuilder.api;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    
    private static final String BASE_URL = "https://generativelanguage.googleapis.com/";
    private static Retrofit retrofit = null;
    
    public static Retrofit getClient() {
        if (retrofit == null) {
            // Create OkHttpClient with API key interceptor
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new ApiKeyInterceptor())
                    .connectTimeout(60, TimeUnit.SECONDS) // Increased timeout for API calls
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .build();
            
            // Create Retrofit instance
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
    
    /**
     * Interceptor to add API key to all requests
     */
    private static class ApiKeyInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request original = chain.request();
            HttpUrl originalHttpUrl = original.url();
            
            // Get API key from environment or use a placeholder for development
            String apiKey = System.getenv("GEMINI_API_KEY");
            if (apiKey == null || apiKey.isEmpty()) {
                // Fallback for development - in production, API key should be securely stored
                apiKey = "YOUR_API_KEY"; // Replace with your actual API key for testing
            }
            
            // Add API key as query parameter
            HttpUrl url = originalHttpUrl.newBuilder()
                    .addQueryParameter("key", apiKey)
                    .build();
            
            // Build new request
            Request.Builder requestBuilder = original.newBuilder()
                    .url(url);
            
            Request request = requestBuilder.build();
            return chain.proceed(request);
        }
    }
}
