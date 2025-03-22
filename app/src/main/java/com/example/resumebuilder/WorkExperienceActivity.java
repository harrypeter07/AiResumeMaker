package com.example.resumebuilder;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.resumebuilder.api.models.GeminiResponse;

import com.example.resumebuilder.api.models.GeminiRequest;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.resumebuilder.api.ApiClient;
import com.example.resumebuilder.api.GeminiApiService;
import com.example.resumebuilder.models.ResumeData;
import com.example.resumebuilder.models.WorkExperience;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.example.resumebuilder.api.models.GeminiResponse;

/**
 * Activity for adding work experience information to the resume
 */
public class WorkExperienceActivity extends AppCompatActivity {

    private LinearLayout experienceContainer;
    private FloatingActionButton fabAddExperience;
    private Button btnNext;
    private Button btnBack;
    private Button btnSave;

    private ResumeData resumeData;
    private ArrayList<WorkExperience> experienceList;

    private GeminiApiService geminiApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_experience);

        // Set title
        setTitle("Work Experience");

        // Initialize API service
        geminiApiService = ApiClient.getGeminiApiService();

        // Get resume data from intent
        if (getIntent() != null && getIntent().hasExtra("resume_data")) {
            resumeData = (ResumeData) getIntent().getSerializableExtra("resume_data");

            // Initialize experience list if not already created
            if (resumeData.getWorkExperienceList() != null) {
                experienceList = new ArrayList<>(resumeData.getWorkExperienceList());
            } else {
                experienceList = new ArrayList<>();
            }
        } else {
            // Handle error: no resume data
            Toast.makeText(this, "Error: No resume data found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize UI components
        initUI();

        // Display existing work experience entries if any
        displayExperienceList();

        // Set up listeners
        setupListeners();
    }

    private void initUI() {
        experienceContainer = findViewById(R.id.experience_container);
        fabAddExperience = findViewById(R.id.fab_add_experience);
        btnNext = findViewById(R.id.btn_next);
        btnBack = findViewById(R.id.btn_back);
        btnSave = findViewById(R.id.btn_save);
    }

    private void displayExperienceList() {
        experienceContainer.removeAllViews();

        if (experienceList.isEmpty()) {
            // Show empty state
            TextView emptyView = new TextView(this);
            emptyView.setText("No work experience entries yet. Tap + to add your work experience.");
            emptyView.setPadding(32, 32, 32, 32);
            experienceContainer.addView(emptyView);
        } else {
            // Display each work experience entry
            for (int i = 0; i < experienceList.size(); i++) {
                addExperienceItemView(experienceList.get(i), i);
            }
        }
    }

    private void addExperienceItemView(final WorkExperience experience, final int position) {
        View view = LayoutInflater.from(this).inflate(R.layout.item_work_experience, null);

        // Get references to views
        TextView tvPosition = view.findViewById(R.id.tv_position);
        TextView tvCompany = view.findViewById(R.id.tv_company);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView tvDates = view.findViewById(R.id.tv_dates);
        Button btnEdit = view.findViewById(R.id.btn_edit);
        Button btnDelete = view.findViewById(R.id.btn_delete);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button btnEnhance = view.findViewById(R.id.btn_enhance);

        // Set data to views
        tvPosition.setText(experience.getPosition());
        tvCompany.setText(experience.getCompany() + " • " + experience.getLocation());

        String dateText = experience.getStartDate() + " - ";
        dateText += experience.isCurrentJob() ? "Present" : experience.getEndDate();
        tvDates.setText(dateText);

        // Set click listeners
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExperienceDialog(experience, position);
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog(position);
            }
        });

        btnEnhance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enhanceJobDescription(experience, position);
            }
        });

        // Add to container
        experienceContainer.addView(view);
    }

    private void setupListeners() {
        fabAddExperience.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExperienceDialog(null, -1);
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
                // Navigate to skills activity
                Intent intent = new Intent(WorkExperienceActivity.this, SkillsActivity.class);
                intent.putExtra("resume_data", resumeData);
                startActivity(intent);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
                saveDraft();
                Toast.makeText(WorkExperienceActivity.this, "Work experience information saved", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void showExperienceDialog(final WorkExperience experience, final int position) {
        // Create dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_work_experience, null);
        builder.setView(dialogView);

        // Get references to views
        final EditText etCompany = dialogView.findViewById(R.id.et_company);
        final EditText etPosition = dialogView.findViewById(R.id.et_position);
        final EditText etLocation = dialogView.findViewById(R.id.et_location);
        final EditText etStartDate = dialogView.findViewById(R.id.et_start_date);
        final EditText etEndDate = dialogView.findViewById(R.id.et_end_date);
        final EditText etDescription = dialogView.findViewById(R.id.et_description);
        final CheckBox cbCurrentJob = dialogView.findViewById(R.id.cb_current_job);

        final TextInputLayout tilCompany = dialogView.findViewById(R.id.til_company);
        final TextInputLayout tilPosition = dialogView.findViewById(R.id.til_position);
        final TextInputLayout tilLocation = dialogView.findViewById(R.id.til_location);
        final TextInputLayout tilStartDate = dialogView.findViewById(R.id.til_start_date);
        final TextInputLayout tilEndDate = dialogView.findViewById(R.id.til_end_date);

        // Set data if editing
        if (experience != null) {
            etCompany.setText(experience.getCompany());
            etPosition.setText(experience.getPosition());
            etLocation.setText(experience.getLocation());
            etStartDate.setText(experience.getStartDate());
            etEndDate.setText(experience.getEndDate());
            etDescription.setText(experience.getDescription());
            cbCurrentJob.setChecked(experience.isCurrentJob());

            // Disable end date if current job
            if (experience.isCurrentJob()) {
                etEndDate.setEnabled(false);
                tilEndDate.setEnabled(false);
            }
        }

        // Set listener for current job checkbox
        cbCurrentJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isCurrentJob = cbCurrentJob.isChecked();
                etEndDate.setEnabled(!isCurrentJob);
                tilEndDate.setEnabled(!isCurrentJob);

                if (isCurrentJob) {
                    etEndDate.setText("Present");
                } else {
                    if (etEndDate.getText().toString().equals("Present")) {
                        etEndDate.setText("");
                    }
                }
            }
        });

        // Set title
        String title = (experience == null) ? "Add Work Experience" : "Edit Work Experience";
        builder.setTitle(title);

        // Set buttons
        builder.setPositiveButton("Save", null); // Set listener later to prevent auto-dismiss
        builder.setNegativeButton("Cancel", null);

        // Create dialog
        final AlertDialog dialog = builder.create();
        dialog.show();

        // Override positive button to validate before dismiss
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Validate inputs
                boolean isValid = true;
                boolean isCurrentJob = cbCurrentJob.isChecked();

                if (etCompany.getText().toString().trim().isEmpty()) {
                    tilCompany.setError("Company is required");
                    isValid = false;
                } else {
                    tilCompany.setError(null);
                }

                if (etPosition.getText().toString().trim().isEmpty()) {
                    tilPosition.setError("Position is required");
                    isValid = false;
                } else {
                    tilPosition.setError(null);
                }

                if (etLocation.getText().toString().trim().isEmpty()) {
                    tilLocation.setError("Location is required");
                    isValid = false;
                } else {
                    tilLocation.setError(null);
                }

                if (etStartDate.getText().toString().trim().isEmpty()) {
                    tilStartDate.setError("Start date is required");
                    isValid = false;
                } else {
                    tilStartDate.setError(null);
                }

                if (!isCurrentJob && etEndDate.getText().toString().trim().isEmpty()) {
                    tilEndDate.setError("End date is required");
                    isValid = false;
                } else {
                    tilEndDate.setError(null);
                }

                if (isValid) {
                    // Create or update work experience object
                    WorkExperience experienceItem = (experience != null) ? experience : new WorkExperience();
                    experienceItem.setCompany(etCompany.getText().toString().trim());
                    experienceItem.setPosition(etPosition.getText().toString().trim());
                    experienceItem.setLocation(etLocation.getText().toString().trim());
                    experienceItem.setStartDate(etStartDate.getText().toString().trim());
                    experienceItem.setEndDate(isCurrentJob ? "Present" : etEndDate.getText().toString().trim());
                    experienceItem.setDescription(etDescription.getText().toString().trim());
                    experienceItem.setCurrentJob(isCurrentJob);

                    // Add or update in list
                    if (position == -1) {
                        experienceList.add(experienceItem);
                    } else {
                        experienceList.set(position, experienceItem);
                    }

                    // Refresh display
                    displayExperienceList();

                    // Dismiss dialog
                    dialog.dismiss();
                }
            }
        });
    }

    private void showDeleteConfirmationDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Delete");
        builder.setMessage("Are you sure you want to delete this work experience entry?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                experienceList.remove(position);
                displayExperienceList();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }



    private void enhanceJobDescription(final WorkExperience experience, final int position) {
        // Show loading dialog
        final AlertDialog loadingDialog = new AlertDialog.Builder(this)
                .setTitle("Enhancing Description")
                .setMessage("Using AI to improve your job description...")
                .setCancelable(false)
                .show();

        // Create prompt for the Gemini API using the builder pattern
        String prompt = "Enhance the following job description for a resume. Make it more professional, " +
                "impactful, and results-oriented. Focus on achievements and use strong action verbs. " +
                "Keep it concise (maximum 3-4 bullet points).\n\n" +
                "Position: " + experience.getPosition() + "\n" +
                "Company: " + experience.getCompany() + "\n" +
                "Original Description: " + experience.getDescription();

        // Create request using the builder
        GeminiRequest request = new GeminiRequest(prompt);

        // Make API call
        Call<GeminiResponse> call = geminiApiService.enhanceJobDescription(request.toMap());
        call.enqueue(new Callback<GeminiResponse>() {
            @Override
            public void onResponse(Call<GeminiResponse> call, Response<GeminiResponse> response) {
                loadingDialog.dismiss();

                if (response.isSuccessful() && response.body() != null) {
                    try {
                        // Extract the enhanced description using the convenience method
                        String enhancedDescription = response.body().getContent();

                        if (enhancedDescription != null) {
                            // Update the experience with the enhanced description
                            experience.setDescription(enhancedDescription);
                            experienceList.set(position, experience);
                            displayExperienceList();

                            // Show success message
                            Toast.makeText(WorkExperienceActivity.this,
                                    "Job description enhanced successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            throw new Exception("Could not extract text from response");
                        }
                    } catch (Exception e) {
                        Toast.makeText(WorkExperienceActivity.this,
                                "Failed to enhance description: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(WorkExperienceActivity.this,
                            "Failed to enhance description", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GeminiResponse> call, Throwable t) {
                loadingDialog.dismiss();
                Toast.makeText(WorkExperienceActivity.this,
                        "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }






    private void saveData() {
        // Save work experience list to resume data
        resumeData.setWorkExperienceList(experienceList);
    }

    private void saveDraft() {
        // TODO: Implement persistence logic (SharedPreferences or local database)
    }

    @Override
    public void onBackPressed() {
        // Show confirmation dialog
        super.onBackPressed();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Go Back");
        builder.setMessage("Are you sure you want to go back? Your changes will be saved.");
        builder.setPositiveButton("Save and Go Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveData();
                WorkExperienceActivity.super.onBackPressed();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}