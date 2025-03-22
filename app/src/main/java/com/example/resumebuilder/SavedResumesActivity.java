package com.example.resumebuilder;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.compose.ui.tooling.PreviewActivity;

import com.example.resumebuilder.models.ResumeData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Activity for managing saved resumes
 */
public class SavedResumesActivity extends AppCompatActivity {
    
    private LinearLayout savedResumesContainer;
    private Button btnBack;
    
    private List<SavedResume> savedResumes;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_resumes);
        
        // Set title
        setTitle("Saved Resumes");
        
        // Initialize UI components
        initUI();
        
        // Load saved resumes
        loadSavedResumes();
        
        // Display saved resumes
        displaySavedResumes();
        
        // Set up listeners
        setupListeners();
    }
    
    private void initUI() {
        savedResumesContainer = findViewById(R.id.saved_resumes_container);
        btnBack = findViewById(R.id.btn_back);
    }
    
    private void loadSavedResumes() {
        // TODO: Implement loading saved resumes from storage (SharedPreferences, files, or database)
        // For now, use dummy data
        savedResumes = new ArrayList<>();
        
        // Example of how it would be implemented with real data:
        /*
        // Get saved resume files from internal storage
        File resumesDir = new File(getFilesDir(), "saved_resumes");
        if (resumesDir.exists() && resumesDir.isDirectory()) {
            File[] files = resumesDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(".json")) {
                        try {
                            // Read resume data from file
                            FileInputStream fis = new FileInputStream(file);
                            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
                            StringBuilder sb = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                sb.append(line);
                            }
                            reader.close();
                            fis.close();
                            
                            // Parse JSON to ResumeData
                            Gson gson = new Gson();
                            ResumeData resumeData = gson.fromJson(sb.toString(), ResumeData.class);
                            
                            // Create SavedResume object
                            String fileName = file.getName().replace(".json", "");
                            String[] parts = fileName.split("_");
                            String jobTitle = parts.length > 1 ? parts[1].replace("_", " ") : "Untitled";
                            String dateCreated = parts.length > 2 ? parts[2] : "Unknown";
                            
                            SavedResume savedResume = new SavedResume(
                                    file.getAbsolutePath(),
                                    resumeData.getFullName(),
                                    jobTitle,
                                    dateCreated,
                                    resumeData
                            );
                            
                            savedResumes.add(savedResume);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        */
    }
    
    private void displaySavedResumes() {
        savedResumesContainer.removeAllViews();
        
        if (savedResumes.isEmpty()) {
            // Show empty state
            TextView emptyView = new TextView(this);
            emptyView.setText("No saved resumes yet. Create a resume from the main screen and save it as PDF to see it here.");
            emptyView.setPadding(32, 32, 32, 32);
            savedResumesContainer.addView(emptyView);
        } else {
            // Display each saved resume
            for (int i = 0; i < savedResumes.size(); i++) {
                addSavedResumeItemView(savedResumes.get(i), i);
            }
        }
    }
    
    private void addSavedResumeItemView(final SavedResume savedResume, final int position) {
        View view = LayoutInflater.from(this).inflate(R.layout.item_saved_resume, null);
        
        // Get references to views
        TextView tvName = view.findViewById(R.id.tv_name);
        TextView tvJobTitle = view.findViewById(R.id.tv_job_title);
        TextView tvDate = view.findViewById(R.id.tv_date);
        Button btnView = view.findViewById(R.id.btn_view);
        Button btnEdit = view.findViewById(R.id.btn_edit);
        Button btnDelete = view.findViewById(R.id.btn_delete);
        
        // Set data to views
        tvName.setText(savedResume.getName());
        tvJobTitle.setText(savedResume.getJobTitle());
        tvDate.setText("Created: " + savedResume.getDateCreated());
        
        // Set click listeners
        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openResumeFile(savedResume);
            }
        });
        
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editResume(savedResume);
            }
        });
        
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDeleteResume(savedResume, position);
            }
        });
        
        // Add to container
        savedResumesContainer.addView(view);
    }
    
    private void setupListeners() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    
    private void openResumeFile(SavedResume savedResume) {
        // Check if file exists
        File file = new File(savedResume.getFilePath());
        if (!file.exists()) {
            Toast.makeText(this, "Resume file not found", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Open PDF file with default PDF viewer
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(android.net.Uri.fromFile(file), "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        
        try {
            startActivity(intent);
        } catch (android.content.ActivityNotFoundException e) {
            Toast.makeText(this, "No application available to view PDF", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void editResume(SavedResume savedResume) {
        // Navigate to preview activity with the resume data
        Intent intent = new Intent(this, PreviewActivity.class);
        intent.putExtra("resume_data", savedResume.getResumeData());
        startActivity(intent);
    }
    
    private void confirmDeleteResume(final SavedResume savedResume, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Delete");
        builder.setMessage("Are you sure you want to delete this resume? This action cannot be undone.");
        
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteResume(savedResume, position);
            }
        });
        
        builder.setNegativeButton("Cancel", null);
        
        builder.show();
    }
    
    private void deleteResume(SavedResume savedResume, int position) {
        // Delete file
        File file = new File(savedResume.getFilePath());
        boolean deleted = file.delete();
        
        if (deleted) {
            // Remove from list
            savedResumes.remove(position);
            
            // Refresh display
            displaySavedResumes();
            
            Toast.makeText(this, "Resume deleted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to delete resume", Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        // Reload saved resumes in case they changed
        loadSavedResumes();
        displaySavedResumes();
    }
    
    /**
     * Class to hold information about a saved resume
     */
    private static class SavedResume {
        private String filePath;
        private String name;
        private String jobTitle;
        private String dateCreated;
        private ResumeData resumeData;
        
        public SavedResume(String filePath, String name, String jobTitle, String dateCreated, ResumeData resumeData) {
            this.filePath = filePath;
            this.name = name;
            this.jobTitle = jobTitle;
            this.dateCreated = dateCreated;
            this.resumeData = resumeData;
        }
        
        public String getFilePath() {
            return filePath;
        }
        
        public String getName() {
            return name;
        }
        
        public String getJobTitle() {
            return jobTitle;
        }
        
        public String getDateCreated() {
            return dateCreated;
        }
        
        public ResumeData getResumeData() {
            return resumeData;
        }
    }
}