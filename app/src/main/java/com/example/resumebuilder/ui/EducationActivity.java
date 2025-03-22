package com.example.resumebuilder.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.resumebuilder.MainActivity;
import com.example.resumebuilder.R;
import com.example.resumebuilder.models.Education;
import com.example.resumebuilder.ui.adapters.EducationAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class EducationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EducationAdapter adapter;
    private ArrayList<Education> educationList;
    private FloatingActionButton fabAddEducation;
    private Button btnNext;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_education);

        // Initialize the education list
        if (MainActivity.resumeData.getEducationList() != null) {
            educationList = MainActivity.resumeData.getEducationList();
        } else {
            educationList = new ArrayList<>();
        }

        // Initialize UI components
        recyclerView = findViewById(R.id.recycler_education);
        fabAddEducation = findViewById(R.id.fab_add_education);
        btnNext = findViewById(R.id.btn_next);
        btnBack = findViewById(R.id.btn_back);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EducationAdapter(educationList);
        recyclerView.setAdapter(adapter);

        // Set up click listeners
        fabAddEducation.setOnClickListener(v -> showAddEducationDialog());

        btnNext.setOnClickListener(v -> {
            // Save education list to resume data
            if (educationList.isEmpty()) {
                Toast.makeText(this, "Please add at least one education entry", Toast.LENGTH_SHORT).show();
                return;
            }
            
            MainActivity.resumeData.setEducationList(educationList);
            // Navigate to work experience activity
            Intent intent = new Intent(EducationActivity.this, WorkExperienceActivity.class);
            startActivity(intent);
        });

        btnBack.setOnClickListener(v -> finish());
        
        // Setup item click for editing
        adapter.setOnItemClickListener(new EducationAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                showEditEducationDialog(position);
            }
            
            @Override
            public void onDeleteClick(int position) {
                removeEducationItem(position);
            }
        });
    }

    private void showAddEducationDialog() {
        // Create dialog for adding education
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_education, null);
        builder.setView(dialogView);
        builder.setTitle("Add Education");

        final TextInputEditText etDegree = dialogView.findViewById(R.id.et_degree);
        final TextInputEditText etInstitution = dialogView.findViewById(R.id.et_institution);
        final TextInputEditText etYear = dialogView.findViewById(R.id.et_year);
        final TextInputEditText etGrade = dialogView.findViewById(R.id.et_grade);

        builder.setPositiveButton("Add", (dialog, which) -> {
            // Validate inputs
            String degree = etDegree.getText().toString().trim();
            String institution = etInstitution.getText().toString().trim();
            String year = etYear.getText().toString().trim();
            String grade = etGrade.getText().toString().trim();

            if (degree.isEmpty() || institution.isEmpty() || year.isEmpty()) {
                Toast.makeText(EducationActivity.this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Add education to the list
            Education education = new Education(degree, institution, year, grade);
            educationList.add(education);
            adapter.notifyItemInserted(educationList.size() - 1);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private void showEditEducationDialog(int position) {
        // Create dialog for editing education
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_education, null);
        builder.setView(dialogView);
        builder.setTitle("Edit Education");

        final TextInputEditText etDegree = dialogView.findViewById(R.id.et_degree);
        final TextInputEditText etInstitution = dialogView.findViewById(R.id.et_institution);
        final TextInputEditText etYear = dialogView.findViewById(R.id.et_year);
        final TextInputEditText etGrade = dialogView.findViewById(R.id.et_grade);

        // Pre-fill the fields with existing data
        Education education = educationList.get(position);
        etDegree.setText(education.getDegree());
        etInstitution.setText(education.getInstitution());
        etYear.setText(education.getYear());
        etGrade.setText(education.getGrade());

        builder.setPositiveButton("Save", (dialog, which) -> {
            // Validate inputs
            String degree = etDegree.getText().toString().trim();
            String institution = etInstitution.getText().toString().trim();
            String year = etYear.getText().toString().trim();
            String grade = etGrade.getText().toString().trim();

            if (degree.isEmpty() || institution.isEmpty() || year.isEmpty()) {
                Toast.makeText(EducationActivity.this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Update education in the list
            education.setDegree(degree);
            education.setInstitution(institution);
            education.setYear(year);
            education.setGrade(grade);
            adapter.notifyItemChanged(position);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private void removeEducationItem(int position) {
        // Ask for confirmation before removing
        new AlertDialog.Builder(this)
                .setTitle("Delete Education")
                .setMessage("Are you sure you want to delete this education entry?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    educationList.remove(position);
                    adapter.notifyItemRemoved(position);
                })
                .setNegativeButton("No", null)
                .show();
    }
}
