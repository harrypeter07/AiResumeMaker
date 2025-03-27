package com.example.resumebuilder.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.resumebuilder.R;
import com.example.resumebuilder.api.ApiClient;
import com.example.resumebuilder.api.models.GeminiRequest;
import com.example.resumebuilder.api.models.GeminiResponse;
import com.example.resumebuilder.models.Education;
import com.example.resumebuilder.models.ResumeData;
import com.example.resumebuilder.models.Skill;
import com.example.resumebuilder.models.WorkExperience;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SummaryActivity extends AppCompatActivity {

    private EditText etSummary;
    private Button btnGenerateSummary;
    private Button btnNext;
    private Button btnBack;
    private ProgressBar progressBar;
    private ResumeData resumeData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        // Get ResumeData from Intent
        if (getIntent() != null && getIntent().hasExtra("resume_data")) {
            resumeData = (ResumeData) getIntent().getSerializableExtra("resume_data");
        } else {
            Toast.makeText(this, "Error: No resume data found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize UI components
        etSummary = findViewById(R.id.et_summary);
        btnGenerateSummary = findViewById(R.id.btn_generate_summary);
        btnNext = findViewById(R.id.btn_next);
        btnBack = findViewById(R.id.btn_back);
        progressBar = findViewById(R.id.progress_bar);

        // Ensure ProgressBar is initially hidden
        progressBar.setVisibility(View.GONE);

        // Populate summary if it exists
        if (resumeData.getSummary() != null && !resumeData.getSummary().isEmpty()) {
            etSummary.setText(resumeData.getSummary());
        }

        // Set up click listeners
        btnGenerateSummary.setOnClickListener(v -> generateSummaryWithAI());

        btnNext.setOnClickListener(v -> {
            String summary = etSummary.getText().toString().trim();
            if (summary.isEmpty()) {
                Toast.makeText(this, "Please generate or write a summary first", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save summary to resume data
            resumeData.setSummary(summary);

            // Navigate to TemplateSelectionActivity
            Intent intent = new Intent(SummaryActivity.this, TemplateSelectionActivity.class);
            intent.putExtra("resume_data", resumeData);
            startActivity(intent);
        });

        btnBack.setOnClickListener(v -> finish());
    }

    private void generateSummaryWithAI() {
        // Show loading indicator
        progressBar.setVisibility(View.VISIBLE);
        btnGenerateSummary.setEnabled(false);

        // Collect resume data to construct prompt
        String fullName = resumeData.getFullName();
        String jobRole = resumeData.getJobRole();
        List<Education> educationList = resumeData.getEducationList();
        List<WorkExperience> workExperienceList = resumeData.getWorkExperienceList();
        List<Skill> skillsList = resumeData.getSkillsList();

        // Build prompt for AI
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("Generate a professional resume summary for a ");
        promptBuilder.append(jobRole != null ? jobRole : "professional");
        promptBuilder.append(" named ");
        promptBuilder.append(fullName != null ? fullName : "an individual");
        promptBuilder.append(" with the following qualifications:\n\n");

        // Add education
        promptBuilder.append("Education:\n");
        if (educationList != null && !educationList.isEmpty()) {
            for (Education education : educationList) {
                promptBuilder.append("- ").append(education.getDegree())
                        .append(" from ").append(education.getInstitution())
                        .append(" (").append(education.getStartDate())
                        .append(" to ").append(education.getEndDate()).append(")\n");
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
                } else {
                    promptBuilder.append(" - Present");
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
                if (true) {
                    promptBuilder.append(" (").append(skill.getProficiency()).append(")");
                }
                promptBuilder.append("\n");
            }
        } else {
            promptBuilder.append("- No skills data provided\n");
        }

        promptBuilder.append("\nPlease write a concise, professional summary paragraph (3-5 sentences) " +
                "highlighting the key qualifications, experience, and skills. The summary should be in first person and tailored for the role of ");
        promptBuilder.append(jobRole != null ? jobRole : "a professional");
        promptBuilder.append(". Make it compelling and focused on value proposition to potential employers.");

        // Create request object (API key should be set in ApiClient)
        GeminiRequest request = new GeminiRequest(promptBuilder.toString());

        // Make API call using ApiClient
        Call<GeminiResponse> call = ApiClient.generateContent(promptBuilder.toString());
        call.enqueue(new Callback<GeminiResponse>() {
            @Override
            public void onResponse(Call<GeminiResponse> call, Response<GeminiResponse> response) {
                progressBar.setVisibility(View.GONE);
                btnGenerateSummary.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    String generatedSummary = response.body().getText(); // Use getText() as per GeminiResponse
                    if (generatedSummary != null && !generatedSummary.isEmpty()) {
                        etSummary.setText(generatedSummary);
                    } else {
                        Toast.makeText(SummaryActivity.this, "Failed to generate summary: Empty response", Toast.LENGTH_SHORT).show();
                        provideFallbackSummary();
                    }
                } else {
                    Toast.makeText(SummaryActivity.this, "Failed to generate summary: " + response.message(), Toast.LENGTH_SHORT).show();
                    provideFallbackSummary();
                }
            }

            @Override
            public void onFailure(Call<GeminiResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnGenerateSummary.setEnabled(true);
                Toast.makeText(SummaryActivity.this, "Failed to connect to AI service: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                provideFallbackSummary();
            }
        });
    }

    private void provideFallbackSummary() {
        String jobRole = resumeData.getJobRole();
        List<WorkExperience> workExperiences = resumeData.getWorkExperienceList();
        int yearsOfExperience = 0;

        if (workExperiences != null && !workExperiences.isEmpty()) {
            // Improved approximation: Calculate years based on earliest start date to latest end date
            for (WorkExperience exp : workExperiences) {
                try {
                    int startYear = Integer.parseInt(exp.getStartDate().substring(exp.getStartDate().length() - 4));
                    int endYear = exp.getEndDate() != null && !exp.getEndDate().isEmpty() ?
                            Integer.parseInt(exp.getEndDate().substring(exp.getEndDate().length() - 4)) :
                            2025; // Use current year if ongoing
                    yearsOfExperience = Math.max(yearsOfExperience, endYear - startYear);
                } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                    yearsOfExperience = workExperiences.size(); // Fallback to count
                }
            }
        }

        StringBuilder fallbackSummary = new StringBuilder();
        fallbackSummary.append("I am a dedicated and results-driven ").append(jobRole != null ? jobRole : "professional");
        fallbackSummary.append(" with ").append(yearsOfExperience > 0 ? yearsOfExperience : "several");
        fallbackSummary.append(" years of experience in the field. I have a proven track record of delivering high-quality work and meeting project deadlines. ");
        fallbackSummary.append("My skills in ").append(jobRole != null ? jobRole.toLowerCase() : "my field");
        fallbackSummary.append(" enable me to solve complex problems with attention to detail. ");
        fallbackSummary.append("With excellent communication and a collaborative mindset, I am eager to leverage my expertise in a challenging ").append(jobRole != null ? jobRole : "role").append(" position.");

        etSummary.setText(fallbackSummary.toString());
        Toast.makeText(this, "Using fallback summary due to API failure. You can edit it as needed.", Toast.LENGTH_LONG).show();
    }
}