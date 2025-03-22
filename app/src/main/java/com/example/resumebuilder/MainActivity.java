package com.example.resumebuilder;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.resumebuilder.models.ResumeData;

/**
 * Main entry point for the Resume Builder app.
 * Displays welcome screen and options to create a new resume or edit an existing one.
 */
public class MainActivity extends AppCompatActivity {
    
    private Button btnNewResume;
    private Button btnContinueEditing;
    private Button btnViewSavedResumes;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Initialize UI components
        initUI();
        
        // Set up click listeners
        setupClickListeners();
    }
    
    /**
     * Initialize UI components
     */
    private void initUI() {
        btnNewResume = findViewById(R.id.btn_new_resume);
        btnContinueEditing = findViewById(R.id.btn_continue_editing);
        btnViewSavedResumes = findViewById(R.id.btn_view_saved_resumes);
        
        // Disable continue editing button if no draft exists
        if (!hasDraftResume()) {
            btnContinueEditing.setEnabled(false);
            btnContinueEditing.setAlpha(0.5f);
        }
    }
    
    /**
     * Set up click listeners for buttons
     */
    private void setupClickListeners() {
        // New resume button
        btnNewResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create new resume data
                ResumeData newResumeData = new ResumeData();
                
                // Navigate to PersonalInfoActivity to start resume creation process
                Intent intent = new Intent(MainActivity.this, PersonalInfoActivity.class);
                intent.putExtra("resume_data", newResumeData);
                startActivity(intent);
            }
        });
        
        // Continue editing button
        btnContinueEditing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Load draft resume data
                ResumeData draftResumeData = loadDraftResume();
                
                if (draftResumeData != null) {
                    // Determine which activity to navigate to based on completion status
                    navigateToNextIncompleteSection(draftResumeData);
                } else {
                    Toast.makeText(MainActivity.this, "No draft resume found", Toast.LENGTH_SHORT).show();
                    btnContinueEditing.setEnabled(false);
                    btnContinueEditing.setAlpha(0.5f);
                }
            }
        });
        
        // View saved resumes button
        btnViewSavedResumes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to SavedResumesActivity
                Intent intent = new Intent(MainActivity.this, SavedResumesActivity.class);
                startActivity(intent);
            }
        });
    }
    
    /**
     * Check if a draft resume exists
     * @return true if a draft exists, false otherwise
     */
    private boolean hasDraftResume() {
        // TODO: Implement persistence logic (SharedPreferences or local database)
        return false;
    }
    
    /**
     * Load draft resume data
     * @return ResumeData object or null if no draft exists
     */
    private ResumeData loadDraftResume() {
        // TODO: Implement persistence logic (SharedPreferences or local database)
        return null;
    }
    
    /**
     * Navigate to the next incomplete section of the resume
     * @param resumeData The resume data to check for completion
     */
    private void navigateToNextIncompleteSection(ResumeData resumeData) {
        Intent intent = null;
        
        // Check which sections are incomplete and navigate to the first one
        if (resumeData.getFullName() == null || resumeData.getEmail() == null) {
            // Personal info section is incomplete
            intent = new Intent(MainActivity.this, PersonalInfoActivity.class);
        } else if (resumeData.getEducationList() == null || resumeData.getEducationList().isEmpty()) {
            // Education section is incomplete
            intent = new Intent(MainActivity.this, EducationActivity.class);
        } else if (resumeData.getWorkExperienceList() == null || resumeData.getWorkExperienceList().isEmpty()) {
            // Work experience section is incomplete
            intent = new Intent(MainActivity.this, WorkExperienceActivity.class);
        } else if (resumeData.getSkillsList() == null || resumeData.getSkillsList().isEmpty()) {
            // Skills section is incomplete
            intent = new Intent(MainActivity.this, SkillsActivity.class);
        } else if (resumeData.getSummary() == null || resumeData.getSummary().isEmpty()) {
            // Summary section is incomplete
            intent = new Intent(MainActivity.this, SummaryActivity.class);
        } else {
            // All sections are complete, go to preview
            intent = new Intent(MainActivity.this, PreviewActivity.class);
        }
        
        // Start the appropriate activity with the resume data
        if (intent != null) {
            intent.putExtra("resume_data", resumeData);
            startActivity(intent);
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        // Update button state when returning to this activity
        if (!hasDraftResume()) {
            btnContinueEditing.setEnabled(false);
            btnContinueEditing.setAlpha(0.5f);
        } else {
            btnContinueEditing.setEnabled(true);
            btnContinueEditing.setAlpha(1.0f);
        }
    }
}