package com.example.resumebuilder.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.example.resumebuilder.api.models.GeminiResponse;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.resumebuilder.MainActivity;
import com.example.resumebuilder.R;
import com.example.resumebuilder.models.WorkExperience;
import com.example.resumebuilder.ui.adapters.WorkExperienceAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class WorkExperienceActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private WorkExperienceAdapter adapter;
    private ArrayList<WorkExperience> workExperienceList;
    private FloatingActionButton fabAddExperience;
    private Button btnNext;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_experience);

        // Initialize the work experience list
        if (MainActivity.resumeData.getWorkExperienceList() != null) {
            workExperienceList = MainActivity.resumeData.getWorkExperienceList();
        } else {
            workExperienceList = new ArrayList<>();
        }

        // Initialize UI components
        recyclerView = findViewById(R.id.recycler_work_experience);
        fabAddExperience = findViewById(R.id.fab_add_experience);
        btnNext = findViewById(R.id.btn_next);
        btnBack = findViewById(R.id.btn_back);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WorkExperienceAdapter(workExperienceList);
        recyclerView.setAdapter(adapter);

        // Set up click listeners
        fabAddExperience.setOnClickListener(v -> showAddExperienceDialog());

        btnNext.setOnClickListener(v -> {
            // Save work experience list to resume data
            MainActivity.resumeData.setWorkExperienceList(workExperienceList);
            // Navigate to skills activity
            Intent intent = new Intent(WorkExperienceActivity.this, SkillsActivity.class);
            startActivity(intent);
        });

        btnBack.setOnClickListener(v -> finish());
        
        // Setup item click for editing
        adapter.setOnItemClickListener(new WorkExperienceAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                showEditExperienceDialog(position);
            }
            
            @Override
            public void onDeleteClick(int position) {
                removeExperienceItem(position);
            }
        });
    }

    private void showAddExperienceDialog() {
        // Create dialog for adding work experience
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_work_experience, null);
        builder.setView(dialogView);
        builder.setTitle("Add Work Experience");

        final TextInputEditText etCompany = dialogView.findViewById(R.id.et_company);
        final TextInputEditText etPosition = dialogView.findViewById(R.id.et_position);
        final TextInputEditText etStartDate = dialogView.findViewById(R.id.et_start_date);
        final TextInputEditText etEndDate = dialogView.findViewById(R.id.et_end_date);
        final TextInputEditText etDescription = dialogView.findViewById(R.id.et_description);

        builder.setPositiveButton("Add", (dialog, which) -> {
            // Validate inputs
            String company = etCompany.getText().toString().trim();
            String position = etPosition.getText().toString().trim();
            String startDate = etStartDate.getText().toString().trim();
            String endDate = etEndDate.getText().toString().trim();
            String description = etDescription.getText().toString().trim();

            if (company.isEmpty() || position.isEmpty() || startDate.isEmpty()) {
                Toast.makeText(WorkExperienceActivity.this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Add work experience to the list
            WorkExperience workExperience = new WorkExperience(company, position, startDate, endDate, description);
            workExperienceList.add(workExperience);
            adapter.notifyItemInserted(workExperienceList.size() - 1);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private void showEditExperienceDialog(int position) {
        // Create dialog for editing work experience
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_work_experience, null);
        builder.setView(dialogView);
        builder.setTitle("Edit Work Experience");

        final TextInputEditText etCompany = dialogView.findViewById(R.id.et_company);
        final TextInputEditText etPosition = dialogView.findViewById(R.id.et_position);
        final TextInputEditText etStartDate = dialogView.findViewById(R.id.et_start_date);
        final TextInputEditText etEndDate = dialogView.findViewById(R.id.et_end_date);
        final TextInputEditText etDescription = dialogView.findViewById(R.id.et_description);

        // Pre-fill the fields with existing data
        WorkExperience workExperience = workExperienceList.get(position);
        etCompany.setText(workExperience.getCompany());
        etPosition.setText(workExperience.getPosition());
        etStartDate.setText(workExperience.getStartDate());
        etEndDate.setText(workExperience.getEndDate());
        etDescription.setText(workExperience.getDescription());

        builder.setPositiveButton("Save", (dialog, which) -> {
            // Validate inputs
            String company = etCompany.getText().toString().trim();
            String position = etPosition.getText().toString().trim();
            String startDate = etStartDate.getText().toString().trim();
            String endDate = etEndDate.getText().toString().trim();
            String description = etDescription.getText().toString().trim();

            if (company.isEmpty() || position.isEmpty() || startDate.isEmpty()) {
                Toast.makeText(WorkExperienceActivity.this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Update work experience in the list
            workExperience.setCompany(company);
            workExperience.setPosition(position);
            workExperience.setStartDate(startDate);
            workExperience.setEndDate(endDate);
            workExperience.setDescription(description);
            adapter.notifyItemChanged(position);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private void removeExperienceItem(int position) {
        // Ask for confirmation before removing
        new AlertDialog.Builder(this)
                .setTitle("Delete Work Experience")
                .setMessage("Are you sure you want to delete this work experience entry?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    workExperienceList.remove(position);
                    adapter.notifyItemRemoved(position);
                })
                .setNegativeButton("No", null)
                .show();
    }
}
