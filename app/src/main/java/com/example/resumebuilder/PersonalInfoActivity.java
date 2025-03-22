package com.example.resumebuilder;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.resumebuilder.models.ResumeData;
import com.google.android.material.textfield.TextInputLayout;

/**
 * Activity for entering personal information for the resume
 */
public class PersonalInfoActivity extends AppCompatActivity {
    
    private EditText etFullName;
    private EditText etEmail;
    private EditText etPhone;
    private EditText etAddress;
    private EditText etWebsite;
    private EditText etLinkedin;
    
    private TextInputLayout tilFullName;
    private TextInputLayout tilEmail;
    private TextInputLayout tilPhone;
    
    private Button btnNext;
    private Button btnSave;
    
    private ResumeData resumeData;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);
        
        // Set title
        setTitle("Personal Information");
        
        // Get resume data from intent
        if (getIntent() != null && getIntent().hasExtra("resume_data")) {
            resumeData = (ResumeData) getIntent().getSerializableExtra("resume_data");
        } else {
            resumeData = new ResumeData();
        }
        
        // Initialize UI
        initUI();
        
        // Populate fields if data exists
        populateFields();
        
        // Set up listeners
        setupListeners();
    }
    
    private void initUI() {
        etFullName = findViewById(R.id.et_full_name);
        etEmail = findViewById(R.id.et_email);
        etPhone = findViewById(R.id.et_phone);
        etAddress = findViewById(R.id.et_address);
        etWebsite = findViewById(R.id.et_website);
        etLinkedin = findViewById(R.id.et_linkedin);
        
        tilFullName = findViewById(R.id.til_full_name);
        tilEmail = findViewById(R.id.til_email);
        tilPhone = findViewById(R.id.til_phone);
        
        btnNext = findViewById(R.id.btn_next);
        btnSave = findViewById(R.id.btn_save);
    }
    
    private void populateFields() {
        if (resumeData.getFullName() != null) {
            etFullName.setText(resumeData.getFullName());
        }
        
        if (resumeData.getEmail() != null) {
            etEmail.setText(resumeData.getEmail());
        }
        
        if (resumeData.getPhone() != null) {
            etPhone.setText(resumeData.getPhone());
        }
        
        if (resumeData.getAddress() != null) {
            etAddress.setText(resumeData.getAddress());
        }
        
        if (resumeData.getWebsite() != null) {
            etWebsite.setText(resumeData.getWebsite());
        }
        
        if (resumeData.getLinkedin() != null) {
            etLinkedin.setText(resumeData.getLinkedin());
        }
    }
    
    private void setupListeners() {
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    saveData();
                    // Navigate to Education activity
                    Intent intent = new Intent(PersonalInfoActivity.this, EducationActivity.class);
                    intent.putExtra("resume_data", resumeData);
                    startActivity(intent);
                }
            }
        });
        
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    saveData();
                    // Save as draft and go back to main
                    saveDraft();
                    Toast.makeText(PersonalInfoActivity.this, "Information saved as draft", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }
    
    private boolean validateInputs() {
        boolean isValid = true;
        
        // Validate full name (required)
        if (etFullName.getText().toString().trim().isEmpty()) {
            tilFullName.setError("Full name is required");
            isValid = false;
        } else {
            tilFullName.setError(null);
        }
        
        // Validate email (required and format)
        String email = etEmail.getText().toString().trim();
        if (email.isEmpty()) {
            tilEmail.setError("Email is required");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Enter a valid email address");
            isValid = false;
        } else {
            tilEmail.setError(null);
        }
        
        // Validate phone (required)
        if (etPhone.getText().toString().trim().isEmpty()) {
            tilPhone.setError("Phone number is required");
            isValid = false;
        } else {
            tilPhone.setError(null);
        }
        
        return isValid;
    }
    
    private void saveData() {
        // Save personal information to resume data
        resumeData.setFullName(etFullName.getText().toString().trim());
        resumeData.setEmail(etEmail.getText().toString().trim());
        resumeData.setPhone(etPhone.getText().toString().trim());
        resumeData.setAddress(etAddress.getText().toString().trim());
        resumeData.setWebsite(etWebsite.getText().toString().trim());
        resumeData.setLinkedin(etLinkedin.getText().toString().trim());
    }
    
    private void saveDraft() {
        // TODO: Implement persistence logic (SharedPreferences or local database)
    }
    
    @Override
    public void onBackPressed() {
        // Show confirmation dialog
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Discard Changes?");
        builder.setMessage("Are you sure you want to go back? Any unsaved changes will be lost.");
        builder.setPositiveButton("Discard", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(android.content.DialogInterface dialog, int which) {
                PersonalInfoActivity.super.onBackPressed();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}