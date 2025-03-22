package com.example.resumebuilder.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.resumebuilder.MainActivity;
import com.example.resumebuilder.R;
import com.example.resumebuilder.api.GeminiApiService;
import com.example.resumebuilder.api.RetrofitClient;
import com.example.resumebuilder.api.models.GeminiRequest;
import com.example.resumebuilder.api.models.GeminiResponse;
import com.example.resumebuilder.models.Education;
import com.example.resumebuilder.models.Skill;
import com.example.resumebuilder.models.WorkExperience;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SummaryActivity extends AppCompatActivity {

    private EditText etSummary;
    private Button btnGenerateSummary;
    private Button btnNext;
    private Button btnBack;
    private ProgressBar progressBar;
    private GeminiApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        // Initialize Retrofit service
        apiService = RetrofitClient.getClient().create(GeminiApiService.class);

        // Initialize UI components
        etSummary = findViewById(R.id.et_summary);
        btnGenerateSummary = findViewById(R.id.btn_generate_summary);
        btnNext = findViewById(R.id.btn_next);
        btnBack = findViewById(R.id.btn_back);
        progressBar = findViewById(R.id.progress_bar);

        // If summary is already generated, show it
        if (MainActivity.resumeData.getSummary() != null && !MainActivity.resumeData.getSummary().isEmpty()) {
            etSummary.setText(MainActivity.resumeData.getSummary());
        }

        // Set up click listeners
        btnGenerateSummary.setOnClickListener(v -> generateSummaryWithAI());

        btnNext.setOnClickListener(v -> {
            // Validate that summary exists
            String summary = etSummary.getText().toString().trim();
            if (summary.isEmpty()) {
                Toast.makeText(SummaryActivity.this, "Please generate or write a summary first", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save summary to resume data
            MainActivity.resumeData.setSummary(summary);
            
            // Navigate to template selection
            Intent intent = new Intent(SummaryActivity.this, TemplateSelectionActivity.class);
            startActivity(intent);
        });

        btnBack.setOnClickListener(v -> finish());
    }

    private void generateSummaryWithAI() {
        // Show loading indicator
        progressBar.setVisibility(View.VISIBLE);
        btnGenerateSummary.setEnabled(false);

        // Collect resume data to construct prompt
        String fullName = MainActivity.resumeData.getFullName();
        String jobRole = MainActivity.resumeData.getJobRole();
        ArrayList<Education> educationList = MainActivity.resumeData.getEducationList();
        ArrayList<WorkExperience> workExperienceList = MainActivity.resumeData.getWorkExperienceList();
        ArrayList<Skill> skillsList = MainActivity.resumeData.getSkillsList();

        // Build prompt for AI
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("Generate a professional resume summary for a ");
        promptBuilder.append(jobRole);
        promptBuilder.append(" named ");
        promptBuilder.append(fullName);
        promptBuilder.append(" with the following qualifications:\n\n");

        // Add education
        promptBuilder.append("Education:\n");
        if (educationList != null && !educationList.isEmpty()) {
            for (Education education : educationList) {
                promptBuilder.append("- ").append(education.getDegree())
                        .append(" from ").append(education.getInstitution())
                        .append(" (").append(education.getYear()).append(")\n");
            }
        } else {
            promptBuilder.append("- No education data provided\n");
        }

        // Add work experience
        promptBuilder.append("\nWork Experience:\n");
        if (workExperienceList != null && !workExperienceList.isEmpty()) {
            for (WorkExperience workExperience : workExperienceList) {
                promptBuilder.append("- ").append(workExperience.getPosition())
                        .append(" at ").append(workExperience.getCompany())
                        .append(" (").append(workExperience.getStartDate());
                
                if (workExperience.getEndDate() != null && !workExperience.getEndDate().isEmpty()) {
                    promptBuilder.append(" to ").append(workExperience.getEndDate());
                }
                
                promptBuilder.append(")\n");
                
                if (workExperience.getDescription() != null && !workExperience.getDescription().isEmpty()) {
                    promptBuilder.append("  ").append(workExperience.getDescription()).append("\n");
                }
            }
        } else {
            promptBuilder.append("- No work experience data provided\n");
        }

        // Add skills
        promptBuilder.append("\nSkills:\n");
        if (skillsList != null && !skillsList.isEmpty()) {
            for (Skill skill : skillsList) {
                promptBuilder.append("- ").append(skill.getName());
                if (skill.getProficiency() != null && !skill.getProficiency().isEmpty()) {
                    promptBuilder.append(" (").append(skill.getProficiency()).append(")");
                }
                promptBuilder.append("\n");
            }
        } else {
            promptBuilder.append("- No skills data provided\n");
        }

        promptBuilder.append("\nPlease write a concise, professional summary paragraph (3-5 sentences) " +
                "highlighting the key qualifications, experience, and skills. The summary should be in first person and tailored for the role of ");
        promptBuilder.append(jobRole);
        promptBuilder.append(". Make it compelling and focused on value proposition to potential employers.");

        // Create request object
        GeminiRequest request = new GeminiRequest(promptBuilder.toString());

        // Make API call
        Call<GeminiResponse> call = apiService.generateContent(request);
        call.enqueue(new Callback<GeminiResponse>() {
            @Override
            public void onResponse(Call<GeminiResponse> call, Response<GeminiResponse> response) {
                progressBar.setVisibility(View.GONE);
                btnGenerateSummary.setEnabled(true);
                
                if (response.isSuccessful() && response.body() != null) {
                    String generatedSummary = response.body().getContent();
                    if (generatedSummary != null && !generatedSummary.isEmpty()) {
                        etSummary.setText(generatedSummary);
                    } else {
                        Toast.makeText(SummaryActivity.this, "Failed to generate summary: Empty response", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SummaryActivity.this, "Failed to generate summary: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GeminiResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnGenerateSummary.setEnabled(true);
                Toast.makeText(SummaryActivity.this, "Failed to connect to AI service: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                
                // Provide a basic summary as fallback (this is for demo purposes)
                provideFallbackSummary();
            }
        });
    }
    
    private void provideFallbackSummary() {
        // This is a fallback method for demo purposes
        // Create a basic summary based on available data
        
        String jobRole = MainActivity.resumeData.getJobRole();
        ArrayList<WorkExperience> workExperiences = MainActivity.resumeData.getWorkExperienceList();
        int yearsOfExperience = 0;
        
        if (workExperiences != null && !workExperiences.isEmpty()) {
            yearsOfExperience = workExperiences.size(); // Simple approximation
        }
        
        StringBuilder fallbackSummary = new StringBuilder();
        fallbackSummary.append("Dedicated and results-driven ").append(jobRole);
        fallbackSummary.append(" with ").append(yearsOfExperience > 0 ? yearsOfExperience : "several");
        fallbackSummary.append(" years of experience in the field. Proven track record of delivering high-quality work and meeting project deadlines. ");
        fallbackSummary.append("Skilled in various aspects of ").append(jobRole.toLowerCase());
        fallbackSummary.append(" with strong problem-solving abilities and attention to detail. ");
        fallbackSummary.append("Excellent communication skills with a collaborative mindset. Looking to leverage my skills and experience in a challenging role as a ").append(jobRole).append(".");
        
        etSummary.setText(fallbackSummary.toString());
        
        Toast.makeText(this, "Using fallback summary. You can edit it as needed.", Toast.LENGTH_LONG).show();
    }
}
