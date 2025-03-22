package com.example.resumebuilder.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.resumebuilder.MainActivity;
import com.example.resumebuilder.R;
import com.google.android.material.textfield.TextInputLayout;

public class UserDetailsActivity extends AppCompatActivity {

    // UI components
    private EditText etFullName;
    private EditText etEmail;
    private EditText etPhone;
    private EditText etAddress;
    private EditText etLinkedIn;
    private EditText etJobRole;
    private Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        // Initialize UI components
        etFullName = findViewById(R.id.et_full_name);
        etEmail = findViewById(R.id.et_email);
        etPhone = findViewById(R.id.et_phone);
        etAddress = findViewById(R.id.et_address);
        etLinkedIn = findViewById(R.id.et_linkedin);
        etJobRole = findViewById(R.id.et_job_role);
        btnNext = findViewById(R.id.btn_next);

        // Set up pre-filled data if available
        if (MainActivity.resumeData.getFullName() != null) {
            etFullName.setText(MainActivity.resumeData.getFullName());
            etEmail.setText(MainActivity.resumeData.getEmail());
            etPhone.setText(MainActivity.resumeData.getPhone());
            etAddress.setText(MainActivity.resumeData.getAddress());
            etLinkedIn.setText(MainActivity.resumeData.getLinkedIn());
            etJobRole.setText(MainActivity.resumeData.getJobRole());
        }

        // Set up next button click listener
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    saveUserDetails();
                    navigateToEducationActivity();
                }
            }
        });
    }

    private boolean validateInputs() {
        // Validate full name
        if (etFullName.getText().toString().trim().isEmpty()) {
            showError(etFullName, "Full name is required");
            return false;
        }

        // Validate email
        String email = etEmail.getText().toString().trim();
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError(etEmail, "Valid email is required");
            return false;
        }

        // Validate phone (basic validation)
        String phone = etPhone.getText().toString().trim();
        if (phone.isEmpty() || phone.length() < 10) {
            showError(etPhone, "Valid phone number is required");
            return false;
        }

        // Job role is required
        if (etJobRole.getText().toString().trim().isEmpty()) {
            showError(etJobRole, "Job role is required");
            return false;
        }

        return true;
    }

    private void showError(EditText editText, String errorMessage) {
        // Find the parent TextInputLayout if it exists
        if (editText.getParent().getParent() instanceof TextInputLayout) {
            TextInputLayout textInputLayout = (TextInputLayout) editText.getParent().getParent();
            textInputLayout.setError(errorMessage);
        } else {
            // Fallback to Toast if TextInputLayout is not found
            editText.setError(errorMessage);
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        }
    }

    private void saveUserDetails() {
        // Save user details to the global resume data object
        MainActivity.resumeData.setFullName(etFullName.getText().toString().trim());
        MainActivity.resumeData.setEmail(etEmail.getText().toString().trim());
        MainActivity.resumeData.setPhone(etPhone.getText().toString().trim());
        MainActivity.resumeData.setAddress(etAddress.getText().toString().trim());
        MainActivity.resumeData.setLinkedIn(etLinkedIn.getText().toString().trim());
        MainActivity.resumeData.setJobRole(etJobRole.getText().toString().trim());
    }

    private void navigateToEducationActivity() {
        Intent intent = new Intent(UserDetailsActivity.this, EducationActivity.class);
        startActivity(intent);
    }
}
