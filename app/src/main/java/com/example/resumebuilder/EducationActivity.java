package com.example.resumebuilder;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.resumebuilder.models.Education;
import com.example.resumebuilder.models.ResumeData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

/**
 * Activity for adding education information to the resume
 */
public class EducationActivity extends AppCompatActivity {
    
    private LinearLayout educationContainer;
    private FloatingActionButton fabAddEducation;
    private Button btnNext;
    private Button btnBack;
    private Button btnSave;
    
    private ResumeData resumeData;
    private ArrayList<Education> educationList;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_education);
        
        // Set title
        setTitle("Education");
        
        // Get resume data from intent
        if (getIntent() != null && getIntent().hasExtra("resume_data")) {
            resumeData = (ResumeData) getIntent().getSerializableExtra("resume_data");
            
            // Initialize education list if not already created
            if (resumeData.getEducationList() != null) {
                educationList = new ArrayList<>(resumeData.getEducationList());
            } else {
                educationList = new ArrayList<>();
            }
        } else {
            // Handle error: no resume data
            Toast.makeText(this, "Error: No resume data found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        // Initialize UI components
        initUI();
        
        // Display existing education entries if any
        displayEducationList();
        
        // Set up listeners
        setupListeners();
    }
    
    private void initUI() {
        educationContainer = findViewById(R.id.education_container);
        fabAddEducation = findViewById(R.id.fab_add_education);
        btnNext = findViewById(R.id.btn_next);
        btnBack = findViewById(R.id.btn_back);
        btnSave = findViewById(R.id.btn_save);
    }
    
    private void displayEducationList() {
        educationContainer.removeAllViews();
        
        if (educationList.isEmpty()) {
            // Show empty state
            TextView emptyView = new TextView(this);
            emptyView.setText("No education entries yet. Tap + to add your education.");
            emptyView.setPadding(32, 32, 32, 32);
            educationContainer.addView(emptyView);
        } else {
            // Display each education entry
            for (int i = 0; i < educationList.size(); i++) {
                addEducationItemView(educationList.get(i), i);
            }
        }
    }
    
    private void addEducationItemView(final Education education, final int position) {
        View view = LayoutInflater.from(this).inflate(R.layout.item_education, null);
        
        // Get references to views
        TextView tvDegree = view.findViewById(R.id.tv_degree);
        TextView tvInstitution = view.findViewById(R.id.tv_institution);
        TextView tvDates = view.findViewById(R.id.tv_dates);
        Button btnEdit = view.findViewById(R.id.btn_edit);
        Button btnDelete = view.findViewById(R.id.btn_delete);
        
        // Set data to views
        tvDegree.setText(education.getDegree() + " in " + education.getFieldOfStudy());
        tvInstitution.setText(education.getInstitution());
        tvDates.setText(education.getStartDate() + " - " + education.getEndDate());
        
        // Set click listeners
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEducationDialog(education, position);
            }
        });
        
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog(position);
            }
        });
        
        // Add to container
        educationContainer.addView(view);
    }
    
    private void setupListeners() {
        fabAddEducation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEducationDialog(null, -1);
            }
        });
        
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
                // Navigate to work experience activity
                Intent intent = new Intent(EducationActivity.this, WorkExperienceActivity.class);
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
                Toast.makeText(EducationActivity.this, "Education information saved", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
    
    private void showEducationDialog(final Education education, final int position) {
        // Create dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_education, null);
        builder.setView(dialogView);
        
        // Get references to views
        final EditText etDegree = dialogView.findViewById(R.id.et_degree);
        final EditText etFieldOfStudy = dialogView.findViewById(R.id.et_field_of_study);
        final EditText etInstitution = dialogView.findViewById(R.id.et_institution);
        final EditText etStartDate = dialogView.findViewById(R.id.et_start_date);
        final EditText etEndDate = dialogView.findViewById(R.id.et_end_date);
        final EditText etGpa = dialogView.findViewById(R.id.et_gpa);
        final EditText etDescription = dialogView.findViewById(R.id.et_description);
        
        final TextInputLayout tilDegree = dialogView.findViewById(R.id.til_degree);
        final TextInputLayout tilFieldOfStudy = dialogView.findViewById(R.id.til_field_of_study);
        final TextInputLayout tilInstitution = dialogView.findViewById(R.id.til_institution);
        final TextInputLayout tilStartDate = dialogView.findViewById(R.id.til_start_date);
        final TextInputLayout tilEndDate = dialogView.findViewById(R.id.til_end_date);
        
        // Set data if editing
        if (education != null) {
            etDegree.setText(education.getDegree());
            etFieldOfStudy.setText(education.getFieldOfStudy());
            etInstitution.setText(education.getInstitution());
            etStartDate.setText(education.getStartDate());
            etEndDate.setText(education.getEndDate());
            etGpa.setText(education.getGpa());
            etDescription.setText(education.getDescription());
        }
        
        // Set title
        String title = (education == null) ? "Add Education" : "Edit Education";
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
                
                if (etDegree.getText().toString().trim().isEmpty()) {
                    tilDegree.setError("Degree is required");
                    isValid = false;
                } else {
                    tilDegree.setError(null);
                }
                
                if (etFieldOfStudy.getText().toString().trim().isEmpty()) {
                    tilFieldOfStudy.setError("Field of study is required");
                    isValid = false;
                } else {
                    tilFieldOfStudy.setError(null);
                }
                
                if (etInstitution.getText().toString().trim().isEmpty()) {
                    tilInstitution.setError("Institution is required");
                    isValid = false;
                } else {
                    tilInstitution.setError(null);
                }
                
                if (etStartDate.getText().toString().trim().isEmpty()) {
                    tilStartDate.setError("Start date is required");
                    isValid = false;
                } else {
                    tilStartDate.setError(null);
                }
                
                if (etEndDate.getText().toString().trim().isEmpty()) {
                    tilEndDate.setError("End date is required");
                    isValid = false;
                } else {
                    tilEndDate.setError(null);
                }
                
                if (isValid) {
                    // Create or update education object
                    Education educationItem = (education != null) ? education : new Education();
                    educationItem.setDegree(etDegree.getText().toString().trim());
                    educationItem.setFieldOfStudy(etFieldOfStudy.getText().toString().trim());
                    educationItem.setInstitution(etInstitution.getText().toString().trim());
                    educationItem.setStartDate(etStartDate.getText().toString().trim());
                    educationItem.setEndDate(etEndDate.getText().toString().trim());
                    educationItem.setGpa(etGpa.getText().toString().trim());
                    educationItem.setDescription(etDescription.getText().toString().trim());
                    
                    // Add or update in list
                    if (position == -1) {
                        educationList.add(educationItem);
                    } else {
                        educationList.set(position, educationItem);
                    }
                    
                    // Refresh display
                    displayEducationList();
                    
                    // Dismiss dialog
                    dialog.dismiss();
                }
            }
        });
    }
    
    private void showDeleteConfirmationDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Delete");
        builder.setMessage("Are you sure you want to delete this education entry?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                educationList.remove(position);
                displayEducationList();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
    
    private void saveData() {
        // Save education list to resume data
        resumeData.setEducationList(educationList);
    }
    
    private void saveDraft() {
        // TODO: Implement persistence logic (SharedPreferences or local database)
    }
    
    @Override
    public void onBackPressed() {
        // Show confirmation dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Go Back");
        builder.setMessage("Are you sure you want to go back? Your changes will be saved.");
        builder.setPositiveButton("Save and Go Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveData();
                EducationActivity.super.onBackPressed();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}